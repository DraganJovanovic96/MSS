package com.mss.controller;

import com.mss.dto.*;
import com.mss.service.VehicleService;
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
     * Retrieves all vehicle data which are not deleted.
     *
     * @return ResponseEntity {@link VehicleDto}  containing the vehicles' data.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get all vehicles")
    @ApiResponse(code = 200, message = "Vehicles data successfully fetched.")
    public ResponseEntity<List<VehicleDto>> getVehicles() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(vehicleService.getAllVehicles(false));
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

    /**
     * The endpoint accepts a GET request.
     * Retrieves the vehicles data for a given vehicle id that is received through path variable.
     *
     * @param vehicleId the id of the vehicle to retrieve
     * @return ResponseEntity<VehicleDto> containing the vehicle data for the specified id.
     */
    @GetMapping(value = "/id/{vehicleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get Vehicle's data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Vehicle's data successfully fetched.", response = VehicleDto.class),
            @ApiResponse(code = 404, message = "Vehicle doesn't exist.")
    })
    public ResponseEntity<VehicleDto> getVehicle(@Valid @PathVariable Long vehicleId) {
        VehicleDto vehicleDto = vehicleService.findVehicleById(vehicleId);
        return ResponseEntity.ok(vehicleDto);
    }

    /**
     * The endpoint accepts a DELETE request.
     *
     * @param vehicleId the id of the Vehicle to delete
     * @return HTTP status
     */
    @DeleteMapping(value = "/id/{vehicleId}")
    @PreAuthorize("hasAnyAuthority('admin:delete', 'user:delete')")
    @ApiOperation(value = "Delete Vehicle")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Vehicle successfully deleted."),
            @ApiResponse(code = 404, message = "Vehicle is not found."),
            @ApiResponse(code = 404, message = "Vehicle is already deleted.")
    })
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long vehicleId) {
        vehicleService.deleteVehicle(vehicleId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    /**
     * Updates the vehicle with the information provided in the VehicleUpdateDTO.
     *
     * @param vehicleUpdateDto The VehicleUpdateDTO containing the vehicle information
     * @return The ResponseEntity containing the updated VehicleDto
     */
    @PutMapping(value = "/id/{vehicleId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:update', 'user:update')")
    @ApiOperation(value = "Update vehicle through VehicleUpdateDto")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated vehicle.", response = VehicleDto.class),
            @ApiResponse(code = 404, message = "Vehicle is not found.")
    })
    public ResponseEntity<VehicleDto> updateVehicle(@Valid @RequestBody VehicleUpdateDto vehicleUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(vehicleService.updateVehicle(vehicleUpdateDto));
    }

    /**
     * The getVehicles method is a REST endpoint that returns a ResponseEntity containing a List of VehicleDtos.
     * This method accepts an optional vehicleFiltersQueryDto object as a request body, which contains the query attributes for filtering Vehicle entities.
     * It also accepts an optional pageNo parameter as a query parameter, which specifies the page number to retrieve, and numberOfResultsPerPage
     * which specifies number of results per page.
     *
     * @param vehicleFiltersQueryDto contains parameters based on data will be filtered
     * @param page                    number of wanted page
     * @param pageSize                number of wanted results per page
     * @return ResponseEntity<List> - The HTTP response containing a list of {@link VehicleDto} objects as the response body
     */
    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get all/filtered vehicles")
    @ApiResponse(code = 200, message = "Requests data successfully fetched.")
    public ResponseEntity<List<VehicleDto>> getVehicles(@RequestBody(required = false) VehicleFiltersQueryDto vehicleFiltersQueryDto,
                                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                                          @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) {
        Page<VehicleDto> resultPage = vehicleService.findFilteredVehicles(vehicleFiltersQueryDto.isDeleted(), vehicleFiltersQueryDto, page, pageSize);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Items", String.valueOf(resultPage.getTotalElements()));
        headers.add("X-Total-Pages", String.valueOf(resultPage.getTotalPages()));
        headers.add("X-Current-Page", String.valueOf(resultPage.getNumber()));

        return new ResponseEntity<>(resultPage.getContent(), headers, HttpStatus.OK);
    }
}
