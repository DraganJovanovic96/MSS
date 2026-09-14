package com.mss.model;

import com.mss.enumeration.VacationRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;

/**
 * This class represents the VacationRequest entity.
 * It extends the {@link BaseEntity} class, which contains fields for creation
 * and update timestamps as well as a boolean flag for deletion status.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vacation_requests")
@SQLDelete(sql = "UPDATE vacation_requests SET deleted = true WHERE id=?")
@FilterDef(name = "deletedVacationRequestFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedVacationRequestFilter", condition = "deleted = :isDeleted")
public class VacationRequest extends BaseEntity<Long> {

    /**
     * The employee who requested the vacation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull(message = "Employee is required")
    private User employee;

    /**
     * The start date of the vacation.
     */
    @Column(nullable = false)
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    /**
     * The end date of the vacation.
     */
    @Column(nullable = false)
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    /**
     * The number of vacation days used (excluding weekends).
     */
    @Column(nullable = false)
    private Integer daysUsed;

    /**
     * The status of the vacation request.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VacationRequestStatus status = VacationRequestStatus.PENDING;

    /**
     * The rejection reason (if rejected).
     */
    @Column(length = 500)
    private String rejectionReason;

    /**
     * The year for which this vacation is allocated.
     * Vacation days must be used by June 30 of the following year.
     */
    @Column(nullable = false)
    private Integer vacationYear;

    /**
     * The admin who approved/rejected the request.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;
}
