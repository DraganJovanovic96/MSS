package com.mss.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * The CustomerFiltersQueryDto class represents a data transfer object that contains query attributes for filtering Customer entities.
 * These attributes include the Customer's first and last name, address,phoneNumber and vehicles.
 * <p>
 * The class also uses Jackson's @JsonProperty annotation to specify the JSON property names for each attribute.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class CustomerFiltersQueryDto {
    /**
     * The customer's full name.
     */
    @JsonProperty("fullName")
    private String fullName;

    /**
     * The customer's address.
     */
    @JsonProperty("address")
    private String address;

    /**
     * The customer's phoneNumber.
     */
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    /**
     * The customer deletion status.
     */
    @JsonProperty("isDeleted")
    private boolean isDeleted;

    /**
     * The customer's vehicles.
     */
    @JsonProperty("vehicleIds")
    private List<Long> vehicleIds;
}
