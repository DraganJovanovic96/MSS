package com.mss.mapper;

import com.mss.dto.CustomerCreateDto;
import com.mss.dto.CustomerDto;
import com.mss.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * CustomerMapper is a mapper interface that defines mapping methods between {@link Customer} and{@link CustomerDto}
 * classes using MapStruct library. It also enables list to list mapping.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Mapper
public interface CustomerMapper {
    /**
     * Maps a Customer object to a CustomerDto object.
     *
     * @param customer the Customer object to be mapped to a CustomerDto object
     * @return a CustomerDto object containing the customer's information
     */
    @Mapping(target = "vehicleDtos", source = "customer.vehicles")
    CustomerDto customerToCustomerDto(Customer customer);

    /**
     * Maps a list of Customer objects to a list of CustomerDto objects.
     *
     * @param customer the List<Customer> to be mapped to a List<CustomerDto>
     * @return a List<CustomerDto> containing the CustomerDtos information
     */
    List<CustomerDto> customersToCustomerDtos(List<Customer> customer);

    @Mapping(target= "vehicles", ignore = true)
    Customer customerCreateDtoToCustomer(CustomerCreateDto customerCreateDto);
}
