package com.mss.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ServiceUpdateDto extends BaseEntityDto {
    /**
     * The start date of the service.
     */
    private LocalDate startDate;

    /**
     * The end date of the service.
     */
    private LocalDate endDate;

    /**
     * Current mileage on the vehicle.
     */
    private int currentMileage;

    /**
     * Recommended mileage for next service.
     */
    private int nextServiceMileage;

    /**
     * The id of vehicle on which service is performed.
     */
    @NotNull
    private Long vehicleId;

    /**
     * The user who performed the service.
     */
    @NotNull
    private Long userId;
}
