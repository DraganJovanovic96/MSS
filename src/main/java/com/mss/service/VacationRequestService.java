package com.mss.service;

import com.mss.dto.VacationRequestCreateDto;
import com.mss.dto.VacationRequestDto;
import com.mss.dto.VacationRequestUpdateDto;
import com.mss.model.VacationRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * The VacationRequestService interface contains methods that will be implemented in VacationRequestServiceImpl
 * and methods correlate to VacationRequest entity.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
public interface VacationRequestService {

    /**
     * Create a new vacation request for the authenticated user.
     *
     * @param vacationRequestCreateDto the vacation request data
     * @return the created vacation request
     */
    VacationRequestDto createVacationRequest(VacationRequestCreateDto vacationRequestCreateDto);

    /**
     * Get all vacation requests for the authenticated user.
     *
     * @return list of vacation requests for the authenticated user
     */
    List<VacationRequestDto> getMyVacationRequests();

    /**
     * Get all vacation requests (admin only).
     *
     * @return list of all vacation requests
     */
    List<VacationRequestDto> getAllVacationRequests();

    /**
     * Get all pending vacation requests (admin only).
     *
     * @return list of pending vacation requests
     */
    List<VacationRequestDto> getPendingVacationRequests();

    /**
     * Get a vacation request by id.
     *
     * @param requestId the id of the vacation request
     * @return the vacation request
     */
    VacationRequestDto getVacationRequestById(Long requestId);

    /**
     * Approve or reject a vacation request (admin only).
     *
     * @param requestId the id of the vacation request
     * @param vacationRequestUpdateDto the update data (status and rejection reason)
     * @return the updated vacation request
     */
    VacationRequestDto updateVacationRequest(Long requestId, VacationRequestUpdateDto vacationRequestUpdateDto);

    /**
     * Delete a vacation request (only if pending).
     *
     * @param requestId the id of the vacation request
     */
    void deleteVacationRequest(Long requestId);

    /**
     * Get the remaining vacation days for the authenticated user for a specific year.
     *
     * @param year the year
     * @return the number of remaining vacation days
     */
    Integer getRemainingVacationDays(Integer year);

    /**
     * Get the total available vacation days for the authenticated user.
     *
     * @return the number of remaining vacation days
     */
    Integer getTotalVacationDays();

    /**
     * Calculate the number of business days between two dates (excluding weekends).
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return the number of business days
     */
    Integer calculateBusinessDays(java.time.LocalDate startDate, java.time.LocalDate endDate);

    /**
     * Get the count of active vacation requests (approved requests where current date is within the vacation range).
     *
     * @return the count of active vacation requests
     */
    Integer getActiveVacationsCount();

    /**
     * Get the total count of approved vacation requests.
     *
     * @return the count of approved vacation requests
     */
    Integer getTotalApprovedCount();
}
