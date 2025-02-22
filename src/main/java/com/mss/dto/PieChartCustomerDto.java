package com.mss.dto;

import lombok.Data;

/**
 * A Data Transfer Object (DTO) for transferring benefit data between layers of the application.
 * It extends the {@link BaseEntityDto} class.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class PieChartCustomerDto {
    /**
     * The service's invoiceCode.
     */
    private String customerName;

    /**
     * The service's revenue.
     */
    private double revenue;
}
