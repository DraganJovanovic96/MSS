package com.mss.service.impl;

import com.mss.dto.CustomerCreateDto;
import com.mss.dto.CustomerDto;
import com.mss.mapper.CustomerMapper;
import com.mss.model.Customer;
import com.mss.repository.CustomerRepository;
import com.mss.service.CustomerService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * The CustomerServiceImpl implements CustomerService and
 * all methods that are in CustomerRepository.
 * Dependency injection was used to get beans of CustomerRepository and CustomerMapper.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    /**
     * The repository used to retrieve customer data.
     */
    private final CustomerRepository customerRepository;

    /**
     * The mapper used to convert customer data between CustomerDto and Customer entities.
     */
    private final CustomerMapper customerMapper;

    /**
     * Created CUSTOMER_FILTER attribute, so we can change Filter easily if needed.
     */
    private static final String CUSTOMER_FILTER = "deletedCustomerFilter";

    /**
     * An EntityManager instance is associated with a persistence context.
     * A persistence context is a set of entity instances in which for any
     * persistent entity identity there is a unique entity instance.
     */
    private final EntityManager entityManager;

    /**
     * Retrieves a list of all customers with given first and last name.
     *
     * @return A list of CustomerDto objects representing the customers.
     */
    @Override
    public List<CustomerDto> getCustomersByFirstAndLastName(String firstname, String lastname) {
        List<Customer> customers = customerRepository.findByFirstnameAndLastname(firstname, lastname);

        return customerMapper.customersToCustomerDtos(customers);
    }

    /**
     * Retrieves a list of all customers.
     *
     * @return A list of CustomerDto objects representing the customers.
     */
    @Override
    public List<CustomerDto> getAllCustomers(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(CUSTOMER_FILTER);
        filter.setParameter("isDeleted", isDeleted);
        List<Customer> customers = customerRepository.findAll();
        session.disableFilter(CUSTOMER_FILTER);

        return customerMapper.customersToCustomerDtos(customers);
    }

    /**
     * This method saves a new customer. It is implemented in CustomerController class.
     *
     * @param customerCreateDto the DTO containing the information for the new customer to be saved
     * @return a {@link CustomerDto} object representing the saved customer
     * @throws ResponseStatusException if the phone number already exists
     */
    @Override
    public CustomerDto saveCustomer(CustomerCreateDto customerCreateDto) {
        customerRepository.findByPhoneNumber(customerCreateDto.getPhoneNumber())
                .ifPresent(customer -> {
                    if (customer.getDeleted()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer with that phone number already exists and is deleted,check your deleted resources.");
                    }
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer with that phone number already exists.");
                });

        Customer customer = customerRepository.save((customerMapper.customerCreateDtoToCustomer(customerCreateDto)));

        return customerMapper.customerToCustomerDto(customer);
    }

    /**
     * Finds a customer by their id.
     *
     * @param customerId the unique identifier of the customer to retrieve
     * @return a {@link CustomerDto} representing the found customer
     * @throws ResponseStatusException if no customer is found with the given id,
     *                                 it throws a 404 NOT FOUND response with an appropriate message
     */
    /**
     * A method for retrieving Customer entity from the database using id.
     * In case that customer doesn't exist we get ResponseStatusException.NOT_FOUND.
     *
     * @param customerId used to find Customer by id
     * @param isDeleted  used to check if object is softly deleted
     * @return {@link CustomerDto} which contains info about specific customer
     */
    @Override
    public CustomerDto findCustomerById(Long customerId, boolean isDeleted) {
        Customer customer = customerRepository.findActiveById(customerId, isDeleted)
                .orElseThrow(() -> new ResponseStatusException
                        (HttpStatus.NOT_FOUND, "Customer with this id doesn't exist"));

        return customerMapper.customerToCustomerDto(customer);
    }

    /**
     * Finds a customer by their phone number.
     *
     * @param phoneNumber the phone number of the customer to retrieve
     * @return a {@link CustomerDto} representing the found customer
     * @throws ResponseStatusException if no customer is found with the given phone number,
     *                                 it throws a 404 NOT FOUND response with an appropriate message
     */
    @Override
    public CustomerDto findByPhoneNumber(String phoneNumber, boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(CUSTOMER_FILTER);
        filter.setParameter("isDeleted", isDeleted);
        Optional<Customer> customerOptional = customerRepository.findByPhoneNumberAndDeleted(phoneNumber, isDeleted);

        if (customerOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer with that phone number doesn't exist");
        }

        session.disableFilter(CUSTOMER_FILTER);

        return customerMapper.customerToCustomerDto(customerOptional.get());
    }

    /**
     * A method for performing soft delete of Customer entity. It is implemented in CustomerController class.
     *
     * @param customerId parameter that is unique to entity
     */
    public void deleteCustomer(Long customerId) {
        customerRepository.findById(customerId)
                .map(customer -> {
                    if (Boolean.TRUE.equals(customer.getDeleted())) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer is already deleted.");
                    }

                    return customer;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer is not found."));

        customerRepository.deleteById(customerId);
    }
}
