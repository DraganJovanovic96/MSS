package com.mss.controller;

import com.mss.dto.*;
import com.mss.service.ServiceTypeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The ServiceTypeController class is a REST controller which is responsible for handling HTTP requests related to ServiceType management.
 * It communicates with the service type service to perform CRUD operations on service type resources.
 * The RequiredArgsConstructor is used for fetching serviceTypeService from IoC container.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping("/api/v1/service-types")
@RequiredArgsConstructor
@CrossOrigin
public class ServiceTypeController {
    private final ServiceTypeService serviceTypeService;

    /**
     * The endpoint accepts a GET request.
     * Retrieves all service type data.
     *
     * @return ResponseEntity {@link ServiceTypeDto}  containing the service types' data.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get all service types")
    @ApiResponse(code = 200, message = "Service types data successfully fetched.")
    public ResponseEntity<List<ServiceTypeDto>> getServiceTypes() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(serviceTypeService.getAllServiceTypes(false));
    }

    /**
     * Creates a new vehicleCreateDto using the information provided in the {@code VehicleCreateDto}
     * and returns a ResponseEntity object with status code 201 (Created) and the saved VehicleDto
     * object in the response body.
     *
     * @param serviceTypeCreateDto the DTO containing the information for the new vehicle to be created
     * @return a ResponseEntity object with status code 201 (Created) and the saved VehicleDto
     * object in the response body
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:create', 'user:create')")
    @ApiOperation(value = "Save service type through ServiceTypeCreateDto")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully saved service type.", response = ServiceTypeDto.class),
            @ApiResponse(code = 409, message = "Service type already exists.")
    })
    public ResponseEntity<ServiceTypeDto> createServiceType(@Valid @RequestBody ServiceTypeCreateDto serviceTypeCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceTypeService.saveServiceType(serviceTypeCreateDto));
    }

    /**
     * The endpoint accepts a GET request.
     * Retrieves the service type data for a given service type id that is received through path variable.
     *
     * @param serviceTypeId the id of the service type to retrieve
     * @return ResponseEntity<ServiceTypeDto> containing the service type data for the specified id.
     */
    @GetMapping(value = "/id/{serviceTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get Service Type's data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service Type's data successfully fetched.", response = ServiceTypeDto.class),
            @ApiResponse(code = 404, message = "Service Type doesn't exist.")
    })
    public ResponseEntity<ServiceTypeDto> getServiceType(@Valid @PathVariable Long serviceTypeId) {
        ServiceTypeDto serviceTypeDto = serviceTypeService.findServiceTypeById(serviceTypeId, false);
        return ResponseEntity.ok(serviceTypeDto);
    }

    /**
     * The endpoint accepts a DELETE request.
     *
     * @param serviceTypeId the id of the service type to delete
     * @return HTTP status
     */
    @DeleteMapping(value = "/{serviceTypeId}")
    @PreAuthorize("hasAnyAuthority('admin:delete', 'user:delete')")
    @ApiOperation(value = "Delete Service Type")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Service Type successfully deleted."),
            @ApiResponse(code = 404, message = "Service Type is not found."),
            @ApiResponse(code = 404, message = "Service Type is already deleted.")
    })
    public ResponseEntity<Void> deleteServiceType(@PathVariable Long serviceTypeId) {
        serviceTypeService.deleteServiceType(serviceTypeId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    /**
     * Updates the service type with the information provided in the ServiceTypeUpdateDto.
     *
     * @param serviceTypeUpdateDto The ServiceTypeUpdateDto containing the service type information
     * @return The ResponseEntity containing the updated ServiceTypeDto
     */
    @PutMapping(value = "/id/{serviceTypeId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:update', 'user:update')")
    @ApiOperation(value = "Update service type through ServiceTypeUpdateDto")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated service type.", response = ServiceTypeDto.class),
            @ApiResponse(code = 404, message = "Service type is not found.")
    })
    public ResponseEntity<ServiceTypeDto> updateServiceType(@Valid @RequestBody ServiceTypeUpdateDto serviceTypeUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(serviceTypeService.updateServiceType(serviceTypeUpdateDto));
    }

    /**
     * The getServiceTypes method is a REST endpoint that returns a ResponseEntity containing a List of ServiceTypeDto.
     * This method accepts an optional serviceTypeFiltersQueryDto object as a request body, which contains the query attributes for filtering Service Type entities.
     * It also accepts an optional pageNo parameter as a query parameter, which specifies the page number to retrieve, and numberOfResultsPerPage
     * which specifies number of results per page.
     *
     * @param serviceTypeFiltersQueryDto contains parameters based on data will be filtered
     * @param page                    number of wanted page
     * @param pageSize                number of wanted results per page
     * @return ResponseEntity<List> - The HTTP response containing a list of {@link ServiceTypeDto} objects as the response body
     */
    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get all/filtered Service Types")
    @ApiResponse(code = 200, message = "Requests data successfully fetched.")
    public ResponseEntity<List<ServiceTypeDto>> getServiceTypes(@RequestBody(required = false) ServiceTypeFiltersQueryDto serviceTypeFiltersQueryDto,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        Page<ServiceTypeDto> resultPage = serviceTypeService.findFilteredServiceTypes(serviceTypeFiltersQueryDto.isDeleted(), serviceTypeFiltersQueryDto, page, pageSize);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Items", String.valueOf(resultPage.getTotalElements()));
        headers.add("X-Total-Pages", String.valueOf(resultPage.getTotalPages()));
        headers.add("X-Current-Page", String.valueOf(resultPage.getNumber()));

        return new ResponseEntity<>(resultPage.getContent(), headers, HttpStatus.OK);
    }

}
