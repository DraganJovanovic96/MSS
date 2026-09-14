package com.mss.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mss.enumeration.VacationRequestStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * A Data Transfer Object (DTO) for transferring vacation request data between layers of the application.
 * It extends the {@link BaseEntityDto} class.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VacationRequestDto extends BaseEntityDto {
    /**
     * The employee who requested the vacation.
     */
    @JsonIgnoreProperties({"password", "tokens", "services", "verificationCode", "verificationExpiration", "passwordCode", "passwordCodeExpiration"})
    private UserDto employee;

    /**
     * The start date of the vacation.
     */
    private LocalDate startDate;

    /**
     * The end date of the vacation.
     */
    private LocalDate endDate;

    /**
     * The number of vacation days used (excluding weekends).
     */
    private Integer daysUsed;

    /**
     * The status of the vacation request.
     */
    private VacationRequestStatus status;

    /**
     * The rejection reason (if rejected).
     */
    private String rejectionReason;

    /**
     * The year for which this vacation is allocated.
     */
    private Integer vacationYear;

    /**
     * The admin who approved/rejected the request.
     */
    @JsonIgnoreProperties({"password", "tokens", "services", "verificationCode", "verificationExpiration", "passwordCode", "passwordCodeExpiration"})
    private UserDto processedBy;
}
