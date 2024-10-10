package com.mss.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

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
public class VehicleDto extends BaseEntityDto {
    /**
     * Manufacturer name.
     */
    private String manufacturer;

    /**
     * Model name.
     */
    private String model;

    /**
     * Vehicle plate.
     */
    private String vehiclePlate;

    /**
     * Vin (vehicle identification number).
     */
    private String vin;

    /**
     * Year when the vehicle was manufactured.
     */
    private int yearOfManufacture;

    /**
     * Owner of vehicles.
     */
    @JsonIgnoreProperties("vehicleDtos")
    private CustomerDto customerDto;

    /**
     * Vehicle's services.
     */
    @JsonIgnoreProperties("vehicleDtos")
    private List<ServiceDto> serviceDtos;
}
