package com.mss.dto;

import jakarta.validation.constraints.NotNull;
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
public class EmailCustomerDto {
    /**
     * The id of the customer.
     */
    @NotNull
    private String customerName;

    /**
     * The email of the customer.
     */
    @NotNull
    private String customerEmail;

    /**
     * The id of the vehicle.
     */
    @NotNull
    private String vehicleManufacturerAndModel;

    /**
     * Invoice code of the service.
     */
    @NotNull
    private String invoiceCode;
}
