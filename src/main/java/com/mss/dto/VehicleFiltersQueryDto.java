package com.mss.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The VehicleFiltersQueryDto class represents a data transfer object that contains query attributes for filtering Vehicle entities.
 * These attributes include the Vehicle's manufacturer, model,vehiclePlate,vin,year gf manufacture and customer.
 * <p>
 * The class also uses Jackson's @JsonProperty annotation to specify the JSON property names for each attribute.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class VehicleFiltersQueryDto {
    /**
     * The vehicle's manufacturer.
     */
    @JsonProperty("manufacturer")
    private String manufacturer;

    /**
     * The vehicle's model.
     */
    @JsonProperty("model")
    private String model;

    /**
     * The vehicle's vehicle plate.
     */
    @JsonProperty("vehiclePlate")
    private String vehiclePlate;

    /**
     * The vehicle's vin.
     */
    @JsonProperty("vin")
    private String vin;

    /**
     * The vehicle's year of manufacture.
     */
    @JsonProperty("yearOfManufacture")
    private Integer yearOfManufacture;

    /**
     * The vehicle's customer.
     */
    @JsonProperty("customerId")
    private Long customerId;
}
