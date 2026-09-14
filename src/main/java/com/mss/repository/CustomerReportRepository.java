package com.mss.repository;

import com.mss.enumeration.CustomerReportStatus;
import com.mss.model.CustomerReport;
import com.mss.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing customer reports.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface CustomerReportRepository extends JpaRepository<CustomerReport, Long> {

    /**
     * Find all customer reports created by a specific user.
     *
     * @param createdBy the id of the user who created the report
     * @return list of customer reports ordered by creation date descending
     */
    List<CustomerReport> findByCreatedByIdOrderByCreatedAtDesc(Long createdBy);

    /**
     * Find all customer reports created by a specific user (paginated).
     *
     * @param createdBy the id of the user who created the report
     * @param pageable the pagination information
     * @return page of customer reports ordered by creation date descending
     */
    Page<CustomerReport> findByCreatedByIdOrderByCreatedAtDesc(Long createdBy, Pageable pageable);

    /**
     * Find all customer reports with a specific status.
     *
     * @param status the status of the customer report
     * @return list of customer reports with the given status ordered by creation date descending
     */
    List<CustomerReport> findByStatusOrderByCreatedAtDesc(CustomerReportStatus status);

    /**
     * Finds all CustomerReports that are marked as deleted and have been deleted for longer than one week.
     *
     * @param oneWeekAgo The date and time representing one week ago.
     * @return A list of customer reports that have been deleted for longer than one week.
     */
    @Query("SELECT c FROM CustomerReport c WHERE c.deleted = true AND c.deletedAt <= :oneWeekAgo")
    List<CustomerReport> findCustomerReportsDeletedOlderThanOneWeek(@Param("oneWeekAgo") Instant oneWeekAgo);

    /**
     * Find all customer reports with a specific status (paginated).
     *
     * @param status the status of the customer report
     * @param pageable the pagination information
     * @return page of customer reports with the given status ordered by creation date descending
     */
    Page<CustomerReport> findByStatusOrderByCreatedAtDesc(CustomerReportStatus status, Pageable pageable);

    /**
     * Find all customer reports for a specific customer.
     *
     * @param customerId the id of the customer
     * @return list of customer reports for the customer ordered by creation date descending
     */
    List<CustomerReport> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    /**
     * Find all customer reports ordered by creation date descending.
     *
     * @return list of all customer reports ordered by creation date descending
     */
    List<CustomerReport> findAllByOrderByCreatedAtDesc();

    /**
     * Find all customer reports ordered by creation date descending (paginated).
     *
     * @param pageable the pagination information
     * @return page of all customer reports ordered by creation date descending
     */
    Page<CustomerReport> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find customer reports by status and assigned to.
     *
     * @param status the status of the customer report
     * @param assignedTo the id of the user the report is assigned to
     * @return list of customer reports
     */
    List<CustomerReport> findByStatusAndUserIdOrderByCreatedAtDesc(CustomerReportStatus status, Long assignedTo);

    /**
     * Find a customer report by id.
     *
     * @param reportId the id of the customer report
     * @return an Optional containing the customer report if found, or empty if not
     */
    Optional<CustomerReport> findOneById(Long reportId);

    /**
     * Count pending customer reports.
     *
     * @return the count of pending customer reports
     */
    @Query("SELECT COUNT(cr) FROM CustomerReport cr WHERE cr.status = :status AND cr.deleted = false")
    Long countPendingReports(@Param("status") CustomerReportStatus status);

    /**
     * Permanently deletes a CustomerReport entity from the database by its ID.
     *
     * <p>This method executes a DELETE operation on the CustomerReport entity,
     * removing the record with the specified ID from the database. This operation
     * is not reversible and will permanently remove the entity.</p>
     *
     * @param customerReportId the ID of the CustomerReport entity to be deleted
     */
    @Modifying
    @Query("DELETE FROM CustomerReport c WHERE c.id = :customerReportId")
    void permanentlyDeleteCustomerReportById(Long customerReportId);

    /**
     * Permanently deletes specific CustomerReport entities provided as a list.
     *
     * @param deletedCustomerReportIds The list of CustomerReport IDs to be permanently deleted.
     */
    @Modifying
    @Query("DELETE FROM CustomerReport c WHERE c.id IN :deletedCustomerReportIds")
    void permanentlyDeleteAllDeletedCustomerReports(List<Long> deletedCustomerReportIds);
}
