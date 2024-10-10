package com.mss.service;

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
}
