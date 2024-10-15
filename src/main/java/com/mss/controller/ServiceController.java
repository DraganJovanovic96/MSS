package com.mss.controller;

import com.mss.dto.ServiceCreateDto;
import com.mss.dto.ServiceDto;
import com.mss.service.ServiceService;
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
 * The ServiceController class is a REST controller which is responsible for handling HTTP requests related to Service management.
 * It communicates with the service service to perform CRUD operations on service resources.
 * The RequiredArgsConstructor is used for fetching serviceService from IoC container.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/services")
@CrossOrigin
public class ServiceController {
    /**
     * The service used to for services.
     */
    private final ServiceService serviceService;

    /**
     * The endpoint accepts a GET request.
     * Retrieves all services data.
     *
     * @return ResponseEntity {@link ServiceDto}  containing the services' data.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get all services")
    @ApiResponse(code = 200, message = "Service data successfully fetched.")
    public ResponseEntity<List<ServiceDto>> getAllServices() {
        return ResponseEntity.status(HttpStatus.OK).body(serviceService.getAllServices(false));
    }

    /**
     * Creates a new service using the information provided in the {@code ServiceCreateDto}
     * and returns a ResponseEntity object with status code 201 (Created) and the saved ServiceDto
     * object in the response body.
     *
     * @param serviceCreateDto the DTO containing the information for the new service to be created
     * @return a ResponseEntity object with status code 201 (Created) and the saved ServiceDto
     * object in the response body
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:create', 'user:create')")
    @ApiOperation(value = "Save service through ServiceCreateDto")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully saved service.", response = ServiceDto.class),
            @ApiResponse(code = 409, message = "Service already exists.")
    })
    public ResponseEntity<ServiceDto> createService(@Valid @RequestBody ServiceCreateDto serviceCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceService.saveService(serviceCreateDto));
    }

    /**
     * The endpoint accepts a GET request.
     * Retrieves the service data for a given service id that is received through path variable.
     *
     * @param serviceId the id of the service to retrieve
     * @return ResponseEntity<ServiceDto> containing the service data for the specified id.
     */
    @GetMapping(value = "/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:create', 'user:create')")
    @ApiOperation(value = "Get Service's data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service's data successfully fetched.", response = ServiceDto.class),
            @ApiResponse(code = 404, message = "Service doesn't exist.")
    })
    public ResponseEntity<ServiceDto> getService(@Valid @PathVariable Long serviceId) {
        ServiceDto serviceDto = serviceService.findServiceById(serviceId, false);
        return ResponseEntity.ok(serviceDto);
    }

    /**
     * The endpoint accepts a DELETE request.
     *
     * @param serviceId the id of the Service to delete
     * @return HTTP status
     */
    @DeleteMapping(value = "/{serviceId}")
    @PreAuthorize("hasAnyAuthority('admin:create', 'user:create')")
    @ApiOperation(value = "Delete service")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Service successfully deleted."),
            @ApiResponse(code = 404, message = "Service is not found."),
            @ApiResponse(code = 404, message = "Service is already deleted.")
    })
    public ResponseEntity<Void> deleteService(@PathVariable Long serviceId) {
        serviceService.deleteService(serviceId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();

    }
}
