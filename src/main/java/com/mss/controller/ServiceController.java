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
        return ResponseEntity.status(HttpStatus.OK).body(serviceService.getAllServices());
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

}
