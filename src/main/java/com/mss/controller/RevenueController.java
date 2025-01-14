package com.mss.controller;

import com.mss.dto.CountsDto;
import com.mss.dto.TwoDateDto;
import com.mss.service.CustomerService;
import com.mss.service.ServiceService;
import com.mss.service.VehicleService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/revenue")
@RequiredArgsConstructor
public class RevenueController {
    /**
     * The service used to for customers.
     */
    private final CustomerService customerService;

    /**
     * The service used to for vehicles.
     */
    private final VehicleService vehicleService;

    /**
     * The service used to for services.
     */
    private final ServiceService serviceService;

    @PostMapping("/counts")
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get counts.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Counts' successfully fetched.")
    })
    public ResponseEntity<CountsDto> getCounts(@Valid @RequestBody TwoDateDto twoDateDto) {
        CountsDto counts = new CountsDto();
        counts.setRevenue(serviceService.getRevenue(false,twoDateDto));
        counts.setParts(serviceService.getParts(false, twoDateDto));
        counts.setServices(serviceService.getServicesForCount(false, twoDateDto));

        return ResponseEntity.status(HttpStatus.OK)
                .body(counts);
    }
}