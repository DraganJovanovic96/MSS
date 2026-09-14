package com.mss.controller;

import com.mss.dto.VacationRequestCreateDto;
import com.mss.dto.VacationRequestDto;
import com.mss.dto.VacationRequestUpdateDto;
import com.mss.service.VacationRequestService;
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
 * The VacationRequestController class is a REST controller which is responsible for handling HTTP requests related to vacation request management.
 * It communicates with the vacation request service to perform CRUD operations on vacation request resources.
 * The RequiredArgsConstructor is used for fetching vacationRequestService from IoC container.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/vacation-requests")
public class VacationRequestController {

    /**
     * The service used for vacation requests.
     */
    private final VacationRequestService vacationRequestService;

    /**
     * Create a new vacation request for the authenticated user.
     *
     * @param vacationRequestCreateDto the vacation request data
     * @return the created vacation request
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('vacation:create')")
    @ApiOperation(value = "Create vacation request")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Vacation request successfully created.", response = VacationRequestDto.class),
            @ApiResponse(code = 400, message = "Bad request - Invalid dates or insufficient vacation days."),
            @ApiResponse(code = 409, message = "Conflict - Overlapping vacation request.")
    })
    public ResponseEntity<VacationRequestDto> createVacationRequest(@Valid @RequestBody VacationRequestCreateDto vacationRequestCreateDto) {
        VacationRequestDto createdRequest = vacationRequestService.createVacationRequest(vacationRequestCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
    }

    /**
     * Get all vacation requests for the authenticated user.
     *
     * @return list of vacation requests for the authenticated user
     */
    @GetMapping(value = "/my-requests", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('vacation:read')")
    @ApiOperation(value = "Get my vacation requests")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Vacation requests successfully fetched.", response = VacationRequestDto.class, responseContainer = "List")
    })
    public ResponseEntity<List<VacationRequestDto>> getMyVacationRequests() {
        List<VacationRequestDto> requests = vacationRequestService.getMyVacationRequests();
        return ResponseEntity.ok(requests);
    }

    /**
     * Get all vacation requests (admin only).
     *
     * @return list of all vacation requests
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('vacation:read')")
    @ApiOperation(value = "Get all vacation requests (admin only)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Vacation requests successfully fetched.", response = VacationRequestDto.class, responseContainer = "List"),
            @ApiResponse(code = 403, message = "Forbidden - Only admins can view all vacation requests.")
    })
    public ResponseEntity<List<VacationRequestDto>> getAllVacationRequests() {
        List<VacationRequestDto> requests = vacationRequestService.getAllVacationRequests();
        return ResponseEntity.ok(requests);
    }

    /**
     * Get all pending vacation requests (admin only).
     *
     * @return list of pending vacation requests
     */
    @GetMapping(value = "/pending", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('vacation:read')")
    @ApiOperation(value = "Get pending vacation requests (admin only)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pending vacation requests successfully fetched.", response = VacationRequestDto.class, responseContainer = "List"),
            @ApiResponse(code = 403, message = "Forbidden - Only admins can view pending vacation requests.")
    })
    public ResponseEntity<List<VacationRequestDto>> getPendingVacationRequests() {
        List<VacationRequestDto> requests = vacationRequestService.getPendingVacationRequests();
        return ResponseEntity.ok(requests);
    }

    /**
     * Get a vacation request by id.
     *
     * @param requestId the id of the vacation request
     * @return the vacation request
     */
    @GetMapping(value = "/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('vacation:read')")
    @ApiOperation(value = "Get vacation request by id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Vacation request successfully fetched.", response = VacationRequestDto.class),
            @ApiResponse(code = 404, message = "Vacation request not found."),
            @ApiResponse(code = 403, message = "Forbidden - You can only view your own vacation requests.")
    })
    public ResponseEntity<VacationRequestDto> getVacationRequestById(@PathVariable Long requestId) {
        VacationRequestDto request = vacationRequestService.getVacationRequestById(requestId);
        return ResponseEntity.ok(request);
    }

    /**
     * Approve or reject a vacation request (admin only).
     *
     * @param requestId the id of the vacation request
     * @param vacationRequestUpdateDto the update data (status and rejection reason)
     * @return the updated vacation request
     */
    @PutMapping(value = "/{requestId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('vacation:update')")
    @ApiOperation(value = "Approve or reject vacation request (admin only)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Vacation request successfully updated.", response = VacationRequestDto.class),
            @ApiResponse(code = 404, message = "Vacation request not found."),
            @ApiResponse(code = 400, message = "Bad request - Can only update pending requests or missing rejection reason."),
            @ApiResponse(code = 403, message = "Forbidden - Only admins can approve/reject vacation requests.")
    })
    public ResponseEntity<VacationRequestDto> updateVacationRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody VacationRequestUpdateDto vacationRequestUpdateDto) {
        VacationRequestDto updatedRequest = vacationRequestService.updateVacationRequest(requestId, vacationRequestUpdateDto);
        return ResponseEntity.ok(updatedRequest);
    }

    /**
     * Delete a vacation request (only if pending).
     *
     * @param requestId the id of the vacation request
     * @return HTTP status
     */
    @DeleteMapping(value = "/{requestId}")
    @PreAuthorize("hasAuthority('vacation:delete')")
    @ApiOperation(value = "Delete vacation request")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Vacation request successfully deleted."),
            @ApiResponse(code = 404, message = "Vacation request not found."),
            @ApiResponse(code = 400, message = "Bad request - Can only delete pending requests."),
            @ApiResponse(code = 403, message = "Forbidden - You can only delete your own vacation requests.")
    })
    public ResponseEntity<Void> deleteVacationRequest(@PathVariable Long requestId) {
        vacationRequestService.deleteVacationRequest(requestId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Get the remaining vacation days for the authenticated user for a specific year.
     *
     * @param year the year
     * @return the number of remaining vacation days
     */
    @GetMapping(value = "/remaining-days", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('vacation:read')")
    @ApiOperation(value = "Get remaining vacation days")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Remaining vacation days successfully fetched.")
    })
    public ResponseEntity<Integer> getRemainingVacationDays(@RequestParam Integer year) {
        Integer remainingDays = vacationRequestService.getRemainingVacationDays(year);
        return ResponseEntity.ok(remainingDays);
    }

    /**
     * Get the total available vacation days for the authenticated user.
     *
     * @return the number of remaining vacation days
     */
    @GetMapping(value = "/total-available", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('vacation:read')")
    @ApiOperation(value = "Get total available vacation days")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Total available vacation days successfully fetched.")
    })
    public ResponseEntity<Integer> getTotalVacationDays() {
        Integer totalDays = vacationRequestService.getTotalVacationDays();
        return ResponseEntity.ok(totalDays);
    }

    /**
     * Get the count of active vacation requests (approved requests where current date is within the vacation range).
     *
     * @return the count of active vacation requests
     */
    @GetMapping(value = "/active-count", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('vacation:read')")
    @ApiOperation(value = "Get active vacations count")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Active vacations count successfully fetched.")
    })
    public ResponseEntity<Integer> getActiveVacationsCount() {
        Integer activeCount = vacationRequestService.getActiveVacationsCount();
        return ResponseEntity.ok(activeCount);
    }

    /**
     * Get the total count of approved vacation requests.
     *
     * @return the count of approved vacation requests
     */
    @GetMapping(value = "/total-approved", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('vacation:read')")
    @ApiOperation(value = "Get total approved vacations count")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Total approved vacations count successfully fetched.")
    })
    public ResponseEntity<Integer> getTotalApprovedCount() {
        Integer totalApproved = vacationRequestService.getTotalApprovedCount();
        return ResponseEntity.ok(totalApproved);
    }
}
