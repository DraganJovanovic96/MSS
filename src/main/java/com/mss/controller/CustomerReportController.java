package com.mss.controller;

import com.mss.dto.CustomerReportCreateDto;
import com.mss.dto.CustomerReportDto;
import com.mss.dto.CustomerReportUpdateDto;
import com.mss.service.CustomerReportService;
import com.mss.service.DamageReportPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for managing customer report operations.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/customer-reports")
@RequiredArgsConstructor
public class CustomerReportController {

    private final CustomerReportService customerReportService;
    private final DamageReportPhotoService damageReportPhotoService;

    /**
     * Create a new customer report with optional photo uploads.
     *
     * @param customerId the customer ID
     * @param vehicleId the vehicle ID
     * @param issueDescription the issue description
     * @param photos optional photo files
     * @return the created customer report
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<CustomerReportDto> createCustomerReport(
            @RequestParam Long customerId,
            @RequestParam Long vehicleId,
            @RequestParam String issueDescription,
            @RequestParam(required = false) List<MultipartFile> photos) {
        
        CustomerReportCreateDto createDto = new CustomerReportCreateDto();
        createDto.setCustomerId(customerId);
        createDto.setVehicleId(vehicleId);
        createDto.setIssueDescription(issueDescription);

        CustomerReportDto createdReport = customerReportService.createCustomerReport(createDto);

        if (photos != null && !photos.isEmpty()) {
            List<String> photoUrls = damageReportPhotoService.uploadDamageReportPhotos(photos, createdReport.getId());
            if (!photoUrls.isEmpty()) {
                customerReportService.updatePhotoUrls(createdReport.getId(), String.join(",", photoUrls));
                createdReport.setPhotoUrls(String.join(",", photoUrls));
            }
        }
        
        return new ResponseEntity<>(createdReport, HttpStatus.CREATED);
    }

    /**
     * Get all customer reports for the authenticated user.
     *
     * @param page the page number (default 0)
     * @param size the page size (default 10)
     * @return list of customer reports for the authenticated user with pagination headers
     */
    @GetMapping("/my-reports")
    public ResponseEntity<List<CustomerReportDto>> getMyCustomerReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CustomerReportDto> reports = customerReportService.getMyCustomerReports(page, size);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Items", String.valueOf(reports.getTotalElements()));
        headers.add("X-Total-Pages", String.valueOf(reports.getTotalPages()));
        headers.add("X-Current-Page", String.valueOf(reports.getNumber()));
        
        return ResponseEntity.ok().headers(headers).body(reports.getContent());
    }

    /**
     * Get all customer reports (admin and receptionist only).
     *
     * @param page the page number (default 0)
     * @param size the page size (default 10)
     * @return list of all customer reports with pagination headers
     */
    @GetMapping("/all")
    public ResponseEntity<List<CustomerReportDto>> getAllCustomerReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CustomerReportDto> reports = customerReportService.getAllCustomerReports(page, size);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Items", String.valueOf(reports.getTotalElements()));
        headers.add("X-Total-Pages", String.valueOf(reports.getTotalPages()));
        headers.add("X-Current-Page", String.valueOf(reports.getNumber()));
        
        return ResponseEntity.ok().headers(headers).body(reports.getContent());
    }

    /**
     * Get all pending customer reports (admin and receptionist only).
     *
     * @param page the page number (default 0)
     * @param size the page size (default 10)
     * @return list of pending customer reports with pagination headers
     */
    @GetMapping("/pending")
    public ResponseEntity<List<CustomerReportDto>> getPendingCustomerReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CustomerReportDto> reports = customerReportService.getPendingCustomerReports(page, size);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Items", String.valueOf(reports.getTotalElements()));
        headers.add("X-Total-Pages", String.valueOf(reports.getTotalPages()));
        headers.add("X-Current-Page", String.valueOf(reports.getNumber()));
        
        return ResponseEntity.ok().headers(headers).body(reports.getContent());
    }

    /**
     * Get a customer report by id.
     *
     * @param reportId the id of the customer report
     * @return the customer report
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<CustomerReportDto> getCustomerReportById(@PathVariable Long reportId) {
        CustomerReportDto report = customerReportService.getCustomerReportById(reportId);
        return ResponseEntity.ok(report);
    }

    /**
     * Update a customer report (admin and receptionist only).
     *
     * @param reportId the id of the customer report
     * @param customerReportUpdateDto the update data
     * @return the updated customer report
     */
    @PutMapping("/{reportId}")
    public ResponseEntity<CustomerReportDto> updateCustomerReport(
            @PathVariable Long reportId,
            @RequestBody CustomerReportUpdateDto customerReportUpdateDto) {
        CustomerReportDto updatedReport = customerReportService.updateCustomerReport(reportId, customerReportUpdateDto);
        return ResponseEntity.ok(updatedReport);
    }

    /**
     * Delete a customer report.
     *
     * @param reportId the id of the customer report
     * @return no content
     */
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteCustomerReport(@PathVariable Long reportId) {
        customerReportService.deleteCustomerReport(reportId);
        return ResponseEntity.noContent().build();
    }
}
