package com.mss.controller;

import com.mss.dto.VehicleDto;
import com.mss.service.VehicleService;
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
 * The VehicleController class is a REST controller which is responsible for handling HTTP requests related to Vehicle management.
 * It communicates with the vehicle service to perform CRUD operations on vehicle resources.
 * The RequiredArgsConstructor is used for fetching vehicleService from IoC container.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/vehicles")
public class VehicleControllers {
    /**
     * The service used to for vehicles.
     */
    private final VehicleService vehicleService;

    /**
     * The endpoint accepts a GET request.
     * Retrieves all vehicle data.
     *
     * @return ResponseEntity {@link VehicleDto}  containing the customers' data.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('admin:read')")
    @ApiOperation(value = "Get all vehicles")
    @ApiResponse(code = 200, message = "Vehicles data successfully fetched.")
    public ResponseEntity<List<VehicleDto>> getVehicles() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(vehicleService.getAllVehicles());
    }
}
