package com.mss.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration representing the status of a vacation request.
 */
@RequiredArgsConstructor
public enum VacationRequestStatus {
    /**
     * Vacation request is pending approval.
     */
    PENDING("PENDING"),

    /**
     * Vacation request has been approved.
     */
    APPROVED("APPROVED"),

    /**
     * Vacation request has been rejected.
     */
    REJECTED("REJECTED");

    /**
     * The string representation of the status.
     */
    @Getter
    private final String status;
}
