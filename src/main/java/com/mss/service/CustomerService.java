package com.mss.service;

import com.mss.dto.CustomerCreateDto;
import com.mss.dto.CustomerDto;

import java.util.List;

/**
 * The CustomerService interface contains methods that will be implemented is CustomerServiceImpl and methods correlate
 * to Customer entity.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
public interface CustomerService {
    /**
     * A method for retrieving customers implemented in CustomerServiceImpl class.
     *
     * @param firstname parameter that represents customers first name
     * @param lastname  parameter that represents customers last name
     * @return a list of CustomersDto with given names
     */
    List<CustomerDto> getCustomersByFirstAndLastName(String firstname, String lastname);

    /**
     * A method for retrieving all customers implemented in CustomerServiceImpl class.
     *
     * @return a list of all CustomersDtos
     */
    List<CustomerDto> getAllCustomers();

    /**
     * A method for saving customer. It is implemented in CustomerServiceImpl class.
     *
     * @param customerCreateDto the DTO containing the data to create the new customer
     * @return the newly created CustomerDto
     */
    CustomerDto saveCustomer(CustomerCreateDto customerCreateDto);

    /**
     * Finds a customer by their unique identifier.
     *
     * @param skillId the unique identifier of the customer to retrieve
     * @return a {@link CustomerDto} representing the found customer
     */
    CustomerDto findOneById(Long skillId);

    /**
     * Finds a customer by their phone number.
     *
     * @param phoneNumber the phone number of the customer to retrieve
     * @return a {@link CustomerDto} representing the found customer
     */
    CustomerDto findByPhoneNumber(String phoneNumber);
}
