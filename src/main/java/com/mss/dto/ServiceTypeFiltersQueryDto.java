package com.mss.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The ServiceTypeFiltersQueryDto class represents a data transfer object that contains query attributes for filtering Service Types entities.
 * These attributes include the ServiceType's price, description,serviceType and service.
 * <p>
 * The class also uses Jackson's @JsonProperty annotation to specify the JSON property names for each attribute.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class ServiceTypeFiltersQueryDto {
    /**
     * The type of service provided.
     */
    @JsonProperty("typeOfService")
    private String typeOfService;

    /**
     * The service types deletion status.
     */
    @JsonProperty("isDeleted")
    private boolean isDeleted;

    /**
     * The description of service provided.
     */
    @JsonProperty("description")
    private String description;

    /**
     * The min price of service provided.
     */
    @JsonProperty("priceMin")
    private Double priceMin;

    /**
     * The max price of service provided.
     */
    @JsonProperty("priceMax")
    private Double priceMax;

    /**
     * The code of part provided.
     */
    @JsonProperty("partCode")
    private String partCode;

    /**
     * Id of service connected to service type.
     */
    @JsonProperty("serviceId")
    private Long serviceId;
}
