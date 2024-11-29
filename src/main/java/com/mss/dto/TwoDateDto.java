package com.mss.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for encapsulating a range of dates.
 * This class is used to represent a start date and an end date for various operations
 * such as filtering data within a specific time range.
 */
@Data
public class TwoDateDto {

    /**
     * The start date of the range.
     * Represented as a {@code LocalDate}.
     */
    private LocalDate startDate;

    /**
     * The end date of the range.
     * Represented as a {@code LocalDate}.
     */
    private LocalDate endDate;
}
