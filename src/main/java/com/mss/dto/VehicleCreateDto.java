package com.mss.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * A Data Transfer Object (DTO) for saving vehicle data.
 * <p>
 * This DTO does not include services to keep the vehicle creation process focused.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class VehicleCreateDto {
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
    @NotNull
    private String vin;

    /**
     * Year when the vehicle was manufactured.
     */
    private int yearOfManufacture;

    /**
     * The customer id of owner of the vehicle.
     */
    @NotNull
    private Long customerId;
}
