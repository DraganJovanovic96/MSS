package com.mss.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration representing customer report status.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@RequiredArgsConstructor
public enum CustomerReportStatus {
    /**
     * Report is pending review.
     */
    PENDING("PENDING"),

    /**
     * Report has been assigned to a mechanic.
     */
    ASSIGNED("ASSIGNED"),

    /**
     * Report work has been completed.
     */
    COMPLETED("COMPLETED");

    /**
     * The string representation of the status.
     */
    @Getter
    private final String status;
}
