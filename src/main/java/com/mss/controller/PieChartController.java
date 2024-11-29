package com.mss.controller;

import com.mss.dto.PieChartCustomerDto;
import com.mss.dto.PieChartMechanicDto;
import com.mss.dto.PieChartServiceDto;
import com.mss.dto.TwoDateDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pie")
@RequiredArgsConstructor
public class PieChartController {
    /**
     * The service used to for services.
     */
    private final ServiceService serviceService;

    /**
     * Handles HTTP GET requests to retrieve data for generating a pie chart.
     * This endpoint filters services by a specified date range and calculates
     * revenue grouped by criteria such as service invoice codes.
     *
     * @param twoDateDto an object containing the start and end dates for filtering the data.
     *                   The dates must be provided in a valid format and are required.
     * @return a {@code ResponseEntity} containing a list of {@code PieChartServiceDto} objects,
     * where each object represents an individual segment of the pie chart with its
     * associated name (e.g., invoice code) and value (e.g., revenue).
     * The response has an HTTP status of 200 (OK) when successful.
     */
    @PostMapping(value = "/revenue-by-service", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read')")
    @ApiOperation(value = "Get pie chart's data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pie chart's data successfully fetched.", response = PieChartServiceDto.class)
    })
    public ResponseEntity<List<PieChartServiceDto>> getPieChartServiceData(@Valid @RequestBody TwoDateDto twoDateDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(serviceService.getInfoForPieChart(false, twoDateDto));
    }

    @PostMapping(value = "/revenue-by-mechanic", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read')")
    @ApiOperation(value = "Get pie chart's data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pie chart's data successfully fetched.", response = PieChartMechanicDto.class)
    })
    public ResponseEntity<List<PieChartMechanicDto>> getPieChartMechanicData(@Valid @RequestBody TwoDateDto twoDateDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(serviceService.getInfoForMechanicPieChart(false, twoDateDto));
    }

    @PostMapping(value = "/revenue-by-customer", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read')")
    @ApiOperation(value = "Get pie chart's data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pie chart's data successfully fetched.", response = PieChartCustomerDto.class)
    })
    public ResponseEntity<List<PieChartCustomerDto>> getPieChartCustomerData(@Valid @RequestBody TwoDateDto twoDateDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(serviceService.getInfoForCustomerPieChart(false, twoDateDto));
    }
}
