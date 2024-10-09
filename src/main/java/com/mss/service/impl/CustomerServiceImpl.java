package com.mss.service.impl;

import com.mss.dto.CustomerDto;
import com.mss.mapper.CustomerMapper;
import com.mss.model.Customer;
import com.mss.repository.CustomerRepository;
import com.mss.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
