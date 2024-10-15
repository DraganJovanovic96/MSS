package com.mss.controller;

import com.mss.dto.ServiceTypeCreateDto;
import com.mss.dto.ServiceTypeDto;
import com.mss.service.ServiceTypeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    @GetMapping(value = "/{serviceTypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:create', 'user:create')")
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
    @PreAuthorize("hasAnyAuthority('admin:create', 'user:create')")
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
}
