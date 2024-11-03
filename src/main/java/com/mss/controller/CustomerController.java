package com.mss.controller;

import com.mss.dto.*;
import com.mss.service.CustomerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
     * This endpoint retrieves all customers data excluding deleted customers.
     *
     * @return ResponseEntity<List> {@link CustomerDto} - The HTTP response containing the
     * list of CustomerDto objects as the response body
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get customers' data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customers' data successfully fetched.", response = CustomerDto.class)
    })
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(customerService.getAllCustomers(false));
    }

    /**
     * The endpoint accepts a GET request.
     * Retrieves the customer data for a given customer id that is received through path variable.
     *
     * @param customerId the id of the customer to retrieve
     * @return ResponseEntity<CustomerDto> containing the customer data for the specified id.
     */
    @GetMapping(value = "/id/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get Customer's data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Customer's data successfully fetched.", response = CustomerDto.class),
            @ApiResponse(code = 404, message = "Customer doesn't exist.")
    })
    public ResponseEntity<CustomerDto> getCustomer(@Valid @PathVariable Long customerId) {
        CustomerDto customerDto = customerService.findCustomerById(customerId);
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
        CustomerDto customerDto = customerService.findByPhoneNumber(customerPhoneNumber, false);

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
    @ApiOperation(value = "Save customer through CustomerCreateDto")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully saved customer.", response = CustomerDto.class),
            @ApiResponse(code = 409, message = "Customer already exists.")
    })
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CustomerCreateDto customerCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.saveCustomer(customerCreateDto));
    }

    /**
     * The endpoint accepts a DELETE request.
     *
     * @param customerId the id of the Customer to delete
     * @return HTTP status
     */
    @DeleteMapping(value = "/id/{customerId}")
    @PreAuthorize("hasAnyAuthority('admin:delete', 'user:delete')")
    @ApiOperation(value = "Delete customer")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Customer successfully deleted."),
            @ApiResponse(code = 404, message = "Customer is not found."),
            @ApiResponse(code = 404, message = "Customer is already deleted.")
    })
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    /**
     * The getCustomers method is a REST endpoint that returns a ResponseEntity containing a List of CustomerDto.
     * This method accepts an optional customerFiltersQueryDto object as a request body, which contains the query attributes for filtering Customer entities.
     * It also accepts an optional pageNo parameter as a query parameter, which specifies the page number to retrieve, and numberOfResultsPerPage
     * which specifies number of results per page.
     *
     * @param customerFiltersQueryDto contains parameters based on data will be filtered
     * @param page                    number of wanted page
     * @param pageSize                number of wanted results per page
     * @return ResponseEntity<List> - The HTTP response containing a list of {@link CustomerDto} objects as the response body
     */
    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get all/filtered customers")
    @ApiResponse(code = 200, message = "Requests data successfully fetched.")
    public ResponseEntity<List<CustomerDto>> getCustomers(@RequestBody(required = false) CustomerFiltersQueryDto customerFiltersQueryDto,
                                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                                          @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        Page<CustomerDto> resultPage = customerService.findFilteredCustomers(false, customerFiltersQueryDto, page, pageSize);

        return new ResponseEntity<>(resultPage.getContent(), HttpStatus.OK);
    }

    /**
     * Updates the customer with the information provided in the CustomerUpdateDTO.
     *
     * @param customerUpdateDto The CustomerUpdateDTO containing the vehicle information
     * @return The ResponseEntity containing the updated VehicleDto
     */
    @PutMapping(value = "/id/{customerId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:update', 'user:update')")
    @ApiOperation(value = "Update customer through CustomerUpdateDto")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated customer.", response = CustomerDto.class),
            @ApiResponse(code = 404, message = "Customer is not found.")
    })
    public ResponseEntity<CustomerDto> updateCustomer(@Valid @RequestBody CustomerUpdateDto customerUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(customerService.updateCustomer(customerUpdateDto));
    }
}
