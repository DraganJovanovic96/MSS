package com.mss.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * The ServiceFiltersQueryDto class represents a data transfer object that contains query attributes for filtering Service entities.
 * These attributes include the Service's invoice code, start and end date, vehicles, users.
 * <p>
 * The class also uses Jackson's @JsonProperty annotation to specify the JSON property names for each attribute.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class ServiceFiltersQueryDto {
    /**
     * Query attribute for Service's invoice code.
     */
    @JsonProperty("invoiceCode")
    private String invoiceCode;

    /**
     * The services deletion status.
     */
    @JsonProperty("isDeleted")
    private boolean isDeleted;


    /**
     * Query attribute for Service's start date.
     */
    @JsonProperty("startDate")
    private LocalDate startDate;

    /**
     * Query attribute for Service's end date.
     */
    @JsonProperty("endDate")
    private LocalDate endDate;

    /**
     * Query attribute for Service's vehicles.
     */
    @JsonProperty("vehicleId")
    private Long vehicleId;

    /**
     * Query attribute for Service's users.
     */
    @JsonProperty("userId")
    private Long userId;
}
