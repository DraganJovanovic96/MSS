package com.mss.dto;

import com.mss.enumeration.VacationRequestStatus;
import com.mss.validation.RejectionReasonRequired;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * A Data Transfer Object (DTO) for updating vacation requests (admin approval/rejection).
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
@RejectionReasonRequired
public class VacationRequestUpdateDto {
    /**
     * The status of the vacation request (APPROVED or REJECTED).
     */
    @NotNull(message = "Status is required")
    private VacationRequestStatus status;

    /**
     * The rejection reason (required if status is REJECTED).
     */
    private String rejectionReason;
}
