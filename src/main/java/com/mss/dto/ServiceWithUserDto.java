package com.mss.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

/**
 * A Data Transfer Object (DTO) for transferring benefit data between layers of the application.
 * It extends the {@link BaseEntityDto} class.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class ServiceWithUserDto extends BaseEntityDto {
    /**
     * The invoice code for the service.
     */
    private String invoiceCode;

    /**
     * The start date of the service.
     */
    private LocalDate startDate;

    /**
     * The vehicle service is provided on.
     */
    @JsonIgnoreProperties({"serviceDtos"})
    private VehicleDto vehicleDto;

    /**
     * The user who performed the service.
     */
    @JsonIgnoreProperties("serviceDtos")
    private UserDto userDto;
}
