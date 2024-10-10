package com.mss.service.impl;

import com.mss.dto.CustomerCreateDto;
import com.mss.dto.CustomerDto;
import com.mss.mapper.CustomerMapper;
import com.mss.model.Customer;
import com.mss.repository.CustomerRepository;
import com.mss.service.CustomerService;
import lombok.RequiredArgsConstructor;
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
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

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
        if (customerRepository.findByPhoneNumber(customerCreateDto.getPhoneNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone number already exists.");
        }
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
    @Override
    public CustomerDto findOneById(Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);

        if (customer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer id doesn't exist");
        }

        return customerMapper.customerToCustomerDto(customer.get());
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
    public CustomerDto findByPhoneNumber(String phoneNumber) {
        Optional<Customer> customer = customerRepository.findByPhoneNumber(phoneNumber);

        if (customer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer with that phone number doesn't exist");
        }

        return customerMapper.customerToCustomerDto(customer.get());
    }
}
