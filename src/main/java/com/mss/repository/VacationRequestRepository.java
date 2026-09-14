package com.mss.repository;

import com.mss.enumeration.VacationRequestStatus;
import com.mss.model.VacationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing vacation requests.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface VacationRequestRepository extends JpaRepository<VacationRequest, Long> {

    /**
     * Find all vacation requests for a specific employee.
     *
     * @param employeeId the id of the employee
     * @return list of vacation requests for the employee ordered by start date ascending
     */
    List<VacationRequest> findByEmployeeIdOrderByStartDateAsc(Long employeeId);

    /**
     * Find all vacation requests with a specific status.
     *
     * @param status the status of the vacation request
     * @return list of vacation requests with the given status ordered by start date ascending
     */
    List<VacationRequest> findByStatusOrderByStartDateAsc(VacationRequestStatus status);

    /**
     * Find all vacation requests ordered by start date ascending.
     *
     * @return list of all vacation requests ordered by start date ascending
     */
    List<VacationRequest> findAllByOrderByStartDateAsc();

    /**
     * Find all vacation requests for a specific employee in a specific year.
     *
     * @param employeeId the id of the employee
     * @param vacationYear the vacation year
     * @return list of vacation requests for the employee in the given year ordered by start date ascending
     */
    List<VacationRequest> findByEmployeeIdAndVacationYearOrderByStartDateAsc(Long employeeId, Integer vacationYear);

    /**
     * Find all approved vacation requests for a specific employee in a specific year.
     *
     * @param employeeId the id of the employee
     * @param vacationYear the vacation year
     * @return list of approved vacation requests for the employee in the given year ordered by start date ascending
     */
    List<VacationRequest> findByEmployeeIdAndVacationYearAndStatusOrderByStartDateAsc(Long employeeId, Integer vacationYear, VacationRequestStatus status);

    /**
     * Calculate total vacation days used by an employee in a specific year (excluding weekends).
     *
     * @param employeeId the id of the employee
     * @param vacationYear the vacation year
     * @return total days used
     */
    @Query("SELECT COALESCE(SUM(vr.daysUsed), 0) FROM VacationRequest vr WHERE vr.employee.id = :employeeId AND vr.vacationYear = :vacationYear AND vr.status = 'APPROVED' AND vr.deleted = false")
    Integer getTotalDaysUsedByEmployeeAndYear(@Param("employeeId") Long employeeId, @Param("vacationYear") Integer vacationYear);

    /**
     * Calculate total pending vacation days for an employee in a specific year (excluding weekends).
     *
     * @param employeeId the id of the employee
     * @param vacationYear the vacation year
     * @return total pending days
     */
    @Query("SELECT COALESCE(SUM(vr.daysUsed), 0) FROM VacationRequest vr WHERE vr.employee.id = :employeeId AND vr.vacationYear = :vacationYear AND vr.status = 'PENDING' AND vr.deleted = false")
    Integer getTotalPendingDaysByEmployeeAndYear(@Param("employeeId") Long employeeId, @Param("vacationYear") Integer vacationYear);

    /**
     * Check if there are any overlapping vacation requests for an employee.
     *
     * @param employeeId the id of the employee
     * @param startDate the start date
     * @param endDate the end date
     * @param excludeRequestId the request id to exclude (for updates)
     * @return true if there are overlapping requests
     */
    @Query("SELECT CASE WHEN COUNT(vr) > 0 THEN true ELSE false END FROM VacationRequest vr " +
           "WHERE vr.employee.id = :employeeId " +
           "AND vr.status IN ('PENDING', 'APPROVED') " +
           "AND vr.deleted = false " +
           "AND ((vr.startDate <= :endDate AND vr.endDate >= :startDate)) " +
           "AND (:excludeRequestId IS NULL OR vr.id != :excludeRequestId)")
    boolean hasOverlappingVacationRequests(@Param("employeeId") Long employeeId,
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate,
                                           @Param("excludeRequestId") Long excludeRequestId);

    /**
     * Find a vacation request by id.
     *
     * @param requestId the id of the vacation request
     * @return an Optional containing the vacation request if found, or empty if not
     */
    Optional<VacationRequest> findOneById(Long requestId);

    /**
     * Count active vacation requests (approved requests where current date is within the vacation range).
     *
     * @return the count of active vacation requests
     */
    @Query("SELECT COUNT(vr) FROM VacationRequest vr WHERE vr.status = 'APPROVED' AND vr.startDate <= CURRENT_DATE AND vr.endDate >= CURRENT_DATE AND vr.deleted = false")
    Long countActiveVacations();

    /**
     * Count total approved vacation requests.
     *
     * @return the count of approved vacation requests
     */
    @Query("SELECT COUNT(vr) FROM VacationRequest vr WHERE vr.status = 'APPROVED' AND vr.deleted = false")
    Long countTotalApproved();

    /**
     * Find pending vacation requests where the start date has passed or is today.
     *
     * @return list of pending vacation requests with expired start dates
     */
    @Query("SELECT vr FROM VacationRequest vr WHERE vr.status = 'PENDING' AND vr.startDate <= CURRENT_DATE AND vr.deleted = false")
    List<VacationRequest> findPendingRequestsWithExpiredStartDate();
}
