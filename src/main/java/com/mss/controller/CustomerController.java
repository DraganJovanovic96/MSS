package com.mss.controller;

import com.mss.dto.CustomerDto;
import com.mss.service.CustomerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PreAuthorize("hasAuthority('admin:read')")
    @ApiOperation(value = "Get all customers")
    @ApiResponse(code = 200, message = "Customer data successfully fetched.")
    public ResponseEntity<List<CustomerDto>> getCustomers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(customerService.getAllCustomers());
    }

}
