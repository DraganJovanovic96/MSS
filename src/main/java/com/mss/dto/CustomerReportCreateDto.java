package com.mss.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a customer report.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReportCreateDto {

    private Long customerId;
    private Long vehicleId;
    private String issueDescription;
    private Long serviceId;
}
