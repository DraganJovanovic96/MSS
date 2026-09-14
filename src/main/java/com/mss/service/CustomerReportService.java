package com.mss.service;

import com.mss.dto.CustomerReportCreateDto;
import com.mss.dto.CustomerReportDto;
import com.mss.dto.CustomerReportUpdateDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for managing customer report operations.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
public interface CustomerReportService {

    /**
     * Create a new customer report.
     *
     * @param customerReportCreateDto the customer report data
     * @return the created customer report
     */
    CustomerReportDto createCustomerReport(CustomerReportCreateDto customerReportCreateDto);

    /**
     * Get all customer reports for the authenticated user.
     *
     * @param page the page number
     * @param size the page size
     * @return page of customer reports for the authenticated user
     */
    Page<CustomerReportDto> getMyCustomerReports(int page, int size);

    /**
     * Get all customer reports (admin only).
     *
     * @param page the page number
     * @param size the page size
     * @return page of all customer reports
     */
    Page<CustomerReportDto> getAllCustomerReports(int page, int size);

    /**
     * Get all pending customer reports.
     *
     * @param page the page number
     * @param size the page size
     * @return page of pending customer reports
     */
    Page<CustomerReportDto> getPendingCustomerReports(int page, int size);

    /**
     * Get a customer report by id.
     *
     * @param reportId the id of the customer report
     * @return the customer report
     */
    CustomerReportDto getCustomerReportById(Long reportId);

    /**
     * Update a customer report (mechanic or admin only).
     *
     * @param reportId the id of the customer report
     * @param customerReportUpdateDto the update data
     * @return the updated customer report
     */
    CustomerReportDto updateCustomerReport(Long reportId, CustomerReportUpdateDto customerReportUpdateDto);

    /**
     * Delete a customer report.
     *
     * @param reportId the id of the customer report
     */
    void deleteCustomerReport(Long reportId);

    /**
     * Update photo URLs for a customer report.
     *
     * @param reportId the id of the customer report
     * @param photoUrls the comma-separated photo URLs
     */
    void updatePhotoUrls(Long reportId, String photoUrls);
}
