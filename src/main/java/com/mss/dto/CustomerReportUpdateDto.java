package com.mss.dto;

import com.mss.enumeration.CustomerReportStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for updating a customer report.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReportUpdateDto {

    private Long id;
    private Long customerId;
    private Long vehicleId;
    private String customerName;
    private String customerEmail;
    private String vehicleInfo;
    private String createdBy;
    private String issueDescription;
    private String photoUrls;
    private CustomerReportStatus status;
    private Instant createdAt;
    private Instant assignedAt;
    private Long userId;
    private Long serviceId;
}
