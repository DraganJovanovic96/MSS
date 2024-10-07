package com.mss.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * A Data Transfer Object (DTO) for transferring benefit data between layers of the application.
 * It extends the {@link BaseEntityDto} class.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class ServiceDto extends BaseEntityDto {
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
     * The vehicle service is provided on.
     */
    private VehicleDto vehicleDto;

    /**
     * The user who performed the service.
     */
    private UserDto userDto;

    /**
     * Service types connected to service.
     */
    private List<ServiceTypeDto> serviceTypeDtos;
}
