package com.mss.dto;

import lombok.Data;

/**
 * Data Transfer Object (DTO) for representing counts and metrics related to services.
 * This class encapsulates the revenue and parts data for reporting or API responses.
 */
@Data
public class CountsDto {

    /**
     * The total revenue generated.
     * Represented as a {@code Double} to support monetary values with decimals.
     */
    private Double revenue;

    /**
     * The total count of parts used or available.
     * Represented as an {@code Integer} since parts are typically whole numbers.
     */
    private Integer parts;
}
