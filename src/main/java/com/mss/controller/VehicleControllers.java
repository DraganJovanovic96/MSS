package com.mss.controller;

import com.mss.dto.VehicleCreateDto;
import com.mss.dto.VehicleDto;
import com.mss.service.VehicleService;
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
     * @return ResponseEntity {@link VehicleDto}  containing the vehicles' data.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get all vehicles")
    @ApiResponse(code = 200, message = "Vehicles data successfully fetched.")
    public ResponseEntity<List<VehicleDto>> getVehicles() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(vehicleService.getAllVehicles());
    }

    /**
     * Creates a new vehicleCreateDto using the information provided in the {@code VehicleCreateDto}
     * and returns a ResponseEntity object with status code 201 (Created) and the saved VehicleDto
     * object in the response body.
     *
     * @param vehicleCreateDto the DTO containing the information for the new vehicle to be created
     * @return a ResponseEntity object with status code 201 (Created) and the saved VehicleDto
     * object in the response body
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:create', 'user:create')")
    @ApiOperation(value = "Save vehicle through VehicleCreateDto")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully saved vehicle.", response = VehicleDto.class),
            @ApiResponse(code = 409, message = "Vehicle already exists.")
    })
    public ResponseEntity<VehicleDto> createVehicle(@Valid @RequestBody VehicleCreateDto vehicleCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleService.saveVehicle(vehicleCreateDto));
    }
}