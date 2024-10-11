package com.mss.controller;

import com.mss.dto.ServiceTypeDto;
import com.mss.service.ServiceTypeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * The ServiceTypeController class is a REST controller which is responsible for handling HTTP requests related to ServiceType management.
 * It communicates with the service type service to perform CRUD operations on service type resources.
 * The RequiredArgsConstructor is used for fetching customerService from IoC container.
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
                .body(serviceTypeService.getAllServiceTypes());
    }
}
