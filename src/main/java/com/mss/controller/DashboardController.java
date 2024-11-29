package com.mss.controller;

import com.mss.service.CustomerService;
import com.mss.service.ServiceService;
import com.mss.service.VehicleService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
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

    @GetMapping("/counts")
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get counts.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Counts' successfully fetched.")
    })
    public ResponseEntity<Map<String, Long>> getCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("customers", customerService.getCustomerCount(false));
        counts.put("vehicles", vehicleService.getVehicleCount(false));
        counts.put("services", serviceService.getServiceCount(false));

        return ResponseEntity.ok(counts);
    }
}