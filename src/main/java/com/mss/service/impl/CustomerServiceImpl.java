package com.mss.service.impl;

import com.mss.dto.CustomerCreateDto;
import com.mss.dto.CustomerDto;
import com.mss.dto.CustomerFiltersQueryDto;
import com.mss.dto.CustomerUpdateDto;
import com.mss.mapper.CustomerMapper;
import com.mss.model.Customer;
import com.mss.model.ServiceType;
import com.mss.model.Vehicle;
import com.mss.repository.*;
import com.mss.service.CustomerService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
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
     * The repository used to retrieve customer data.
     */
    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * The repository used to retrieve customer data.
     */
    private final ServiceRepository serviceRepository;

    /**
     * The repository used to retrieve customer data.
     */
    private final VehicleRepository vehicleRepository;

    /**
     * The repository used to retrieve customer data.
     */
    private final CustomerCustomRepository customerCustomRepository;

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
     * Retrieves a list of all customers which are not deleted.
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
     * Counts the number of customers based on their deletion status.
     *
     * @param isDeleted A boolean indicating the deletion status of customers to be counted.
     *                  If {@code true}, counts only deleted customers.
     *                  If {@code false}, counts only active customers.
     * @return The total number of customers matching the specified deletion status.
     */
    @Override
    public long getCustomerCount(boolean isDeleted) {
        return customerRepository.countCustomersByDeleted(isDeleted);
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
        customerCreateDto.setPhoneNumber(customerCreateDto.getPhoneNumber().replaceAll("[^0-9]", ""));

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
     * A method for retrieving Customer entity from the database using id.
     * In case that customer doesn't exist we get ResponseStatusException.NOT_FOUND.
     *
     * @param customerId used to find Customer by id
     * @return {@link CustomerDto} which contains info about specific customer
     */
    @Override
    public CustomerDto findCustomerById(Long customerId) {
        Customer customer = customerRepository.findOneById(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer with this id doesn't exist"));

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
    @Transactional
    public void deleteCustomer(Long customerId) {
        customerRepository.findById(customerId)
                .map(customer -> {
                    if (Boolean.TRUE.equals(customer.getDeleted())) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer is already deleted.");
                    }

                    for (Vehicle vehicle : customer.getVehicles()) {
                        if (Boolean.FALSE.equals(vehicle.getDeleted()) && Boolean.FALSE.equals(vehicle.getDeletedByCascade())) {

                            for (com.mss.model.Service service : vehicle.getServices()) {
                                if (Boolean.FALSE.equals(service.getDeleted()) && Boolean.FALSE.equals(service.getDeletedByCascade())) {

                                    for (ServiceType serviceType : service.getServiceTypes()) {
                                        if (Boolean.FALSE.equals(serviceType.getDeleted()) && Boolean.FALSE.equals(serviceType.getDeletedByCascade())) {
                                            serviceType.setDeletedByCascade(true);
                                            serviceTypeRepository.save(serviceType);
                                        }
                                    }
                                    service.setDeletedByCascade(true);
                                    serviceRepository.save(service);
                                }
                            }
                            vehicle.setDeletedByCascade(true);
                            vehicleRepository.save(vehicle);
                        }
                    }

                    entityManager.flush();
                    return customer;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer is not found."));

        customerRepository.deleteById(customerId);
    }

    /**
     * Retrieves a paginated list of customers based on the provided filters and deletion status.
     * The method applies a filter to include or exclude deleted customers and then retrieves
     * customers that match the criteria specified in the {@link CustomerFiltersQueryDto}.
     * The customers are mapped to {@link CustomerDto} objects before being returned as a paginated result.
     *
     * @param isDeleted               a boolean indicating whether to include deleted customers in the results.
     * @param customerFiltersQueryDto the {@link CustomerFiltersQueryDto} containing the filter criteria for customers.
     *                                If any field is null, it will be ignored in the query.
     * @param page                    the page number for pagination.
     * @param pageSize                the size of each page for pagination.
     * @return a {@link Page} of {@link CustomerDto} objects representing the customers that match the filter criteria.
     * The page contains the list of customers, pagination details, and total number of rows.
     */
    @Override
    public Page<CustomerDto> findFilteredCustomers(boolean isDeleted, CustomerFiltersQueryDto customerFiltersQueryDto, Integer page, Integer pageSize) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(CUSTOMER_FILTER);
        filter.setParameter("isDeleted", isDeleted);

        Page<Customer> resultPage = customerCustomRepository.findFilteredCustomers(customerFiltersQueryDto, PageRequest.of(page, pageSize));
        List<Customer> customers = resultPage.getContent();

        session.disableFilter(CUSTOMER_FILTER);

        List<CustomerDto> customerDtos = customerMapper.customersToCustomerDtos(customers);

        return new PageImpl<>(customerDtos, resultPage.getPageable(), resultPage.getTotalElements());
    }

    /**
     * Updates the details of an existing customer based on the provided {@link CustomerUpdateDto}.
     *
     * <p>This method retrieves a customer by its ID from the repository, applies updates to
     * fields and updates its deletion status as specified in the DTO. After modification,
     * the updated customer is saved back to the repository, and a {@link CustomerDto}
     * representation of the updated customer is returned.</p>
     *
     * @param customerUpdateDto a DTO containing the new details for the vehicle update
     * @return {@link CustomerDto} the updated vehicle data, encapsulated in a DTO format for response
     * @throws ResponseStatusException with {@code HttpStatus.NOT_FOUND} if no customer exists with the specified ID
     */
    @Override
    @Transactional
    public CustomerDto updateCustomer(CustomerUpdateDto customerUpdateDto) {
        Customer customer = customerRepository.findOneById(customerUpdateDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Customer with this id doesn't exist"));

        customer.setUpdatedAt(Instant.now());
        customer.setFirstname(customerUpdateDto.getFirstname());
        customer.setLastname(customerUpdateDto.getLastname());
        customer.setAddress(customerUpdateDto.getAddress());
        customer.setEmail(customerUpdateDto.getEmail());
        customer.setPhoneNumber(customerUpdateDto.getPhoneNumber());
        customer.setDeleted(customerUpdateDto.getDeleted());

        for (Vehicle vehicle : customer.getVehicles()) {
            if (Boolean.TRUE.equals(vehicle.getDeletedByCascade()) && Boolean.TRUE.equals(vehicle.getDeleted())) {
                vehicle.setDeleted(false);
                vehicle.setDeletedByCascade(false);

                for (com.mss.model.Service service : vehicle.getServices()) {
                    if (Boolean.TRUE.equals(service.getDeletedByCascade()) && Boolean.TRUE.equals(service.getDeleted())) {
                        service.setDeleted(false);
                        service.setDeletedByCascade(false);

                        for (ServiceType serviceType : service.getServiceTypes()) {
                            if (Boolean.TRUE.equals(serviceType.getDeletedByCascade()) && Boolean.TRUE.equals(serviceType.getDeleted())) {
                                serviceType.setDeleted(false);
                                serviceType.setDeletedByCascade(false);
                                serviceTypeRepository.save(serviceType);
                            }
                        }
                        serviceRepository.save(service);
                    }
                }
                vehicleRepository.save(vehicle);
            }
        }
        customerRepository.save(customer);
        entityManager.flush();


        return customerMapper.customerToCustomerDto(customer);
    }
}
