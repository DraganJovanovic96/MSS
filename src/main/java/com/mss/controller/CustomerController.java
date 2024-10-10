package com.mss.controller;

import com.mss.dto.CustomerCreateDto;
import com.mss.dto.CustomerDto;
import com.mss.service.CustomerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The CustomerController class is a REST controller which is responsible for handling HTTP requests related to Customer management.
 * It communicates with the customer service to perform CRUD operations on customer resources.
 * The RequiredArgsConstructor is used for fetching customerService from IoC container.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@CrossOrigin
public class CustomerController {
    /**
     * The service used to for customers.
     */
    private final CustomerService customerService;

    /**
     * The endpoint accepts a GET request.
     * Retrieves all customers data.
     *
     * @return ResponseEntity {@link CustomerDto}  containing the customers' data.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get all customers")
    @ApiResponse(code = 200, message = "Customer data successfully fetched.")
    public ResponseEntity<List<CustomerDto>> getCustomers() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(customerService.getAllCustomers());
    }

    /**
     * The endpoint accepts a GET request.
     * Retrieves the customer data for a given customer id that is received through path variable.
     *
     * @param customerId the id of the customer to retrieve
     * @return ResponseEntity<CustomerDto> containing the customer data for the specified id.
     */
    @GetMapping(value = "/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get customer data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer's data successfully fetched.", response = CustomerDto.class),
            @ApiResponse(code = 404, message = "Customer doesn't exist.")
    })
    public ResponseEntity<CustomerDto> getCustomer(@Valid @PathVariable Long customerId) {
        CustomerDto customerDto = customerService.findOneById(customerId);

        return ResponseEntity.ok(customerDto);
    }

    /**
     * The endpoint accepts a GET request.
     * Retrieves the customer data for a given customer id that is received through path variable.
     *
     * @param customerPhoneNumber the phone number of the customer to retrieve
     * @return ResponseEntity<CustomerDto> containing the customer data for the specified id.
     */
    @GetMapping(value = "/phone/{customerPhoneNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get customer data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer's data successfully fetched.", response = CustomerDto.class),
            @ApiResponse(code = 404, message = "Customer doesn't exist.")
    })
    public ResponseEntity<CustomerDto> getCustomerByPhone(@Valid @PathVariable String customerPhoneNumber) {
        CustomerDto customerDto = customerService.findByPhoneNumber(customerPhoneNumber);

        return ResponseEntity.ok(customerDto);
    }

    /**
     * Creates a new customer using the information provided in the {@code CustomerCreateDto}
     * and returns a ResponseEntity object with status code 201 (Created) and the saved CustomerDto
     * object in the response body.
     *
     * @param customerCreateDto the DTO containing the information for the new customer to be created
     * @return a ResponseEntity object with status code 201 (Created) and the saved CustomerDto
     * object in the response body
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:create', 'user:create')")
    @ApiOperation(value = "Save customer through CustomerDto")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully saved customer.", response = CustomerDto.class),
            @ApiResponse(code = 409, message = "Customer already exists.")
    })
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CustomerCreateDto customerCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.saveCustomer(customerCreateDto));
    }
}
