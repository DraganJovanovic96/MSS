package com.mss.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * A Data Transfer Object (DTO) for creating vacation requests.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class VacationRequestCreateDto {
    /**
     * The start date of the vacation.
     */
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    /**
     * The end date of the vacation.
     */
    @NotNull(message = "End date is required")
    private LocalDate endDate;
}
