package com.mss.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleUpdateDto extends BaseEntityDto {
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
     * Id of the customer who is vehicle owner.
     */
    private Long customerId;
}
