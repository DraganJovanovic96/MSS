package com.mss.controller;

import com.mss.dto.ServiceDto;
import com.mss.service.ServiceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PreAuthorize("hasAuthority('admin:read')")
    @ApiOperation(value = "Get all customers")
    @ApiResponse(code = 200, message = "Customer data successfully fetched.")
    public ResponseEntity<List<ServiceDto>> getAllServices() {
        return ResponseEntity.status(HttpStatus.OK).body(serviceService.getAllServices());
    }

}
