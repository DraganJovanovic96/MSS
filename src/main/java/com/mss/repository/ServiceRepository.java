package com.mss.repository;

import com.mss.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The ServiceRepository interface extends JpaRepository to inherit JPA-based CRUD methods and custom
 * methods for accessing and modifying Service entities.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    /**
     * Counts the number of services based on their deletion status.
     *
     * @param isDeleted A boolean indicating the deletion status of services to be counted.
     *                  If {@code true}, counts only deleted services.
     *                  If {@code false}, counts only active services.
     * @return The total number of services matching the specified deletion status.
     */
    @Query("SELECT COUNT(s) FROM Service s WHERE s.deleted = :isDeleted")
    long countServicesByDeleted(@Param("isDeleted") boolean isDeleted);
    /**
     * Find a service by their id if they are not soft deleted.
     *
     * @param serviceId the id of the service
     * @return an Optional containing the service if found, or empty if not
     */
    Optional<Service> findOneById(Long serviceId);

    /**
     * Finds all services that are marked as deleted.
     *
     * @return A list of services that are marked as deleted.
     */
    @Query("SELECT s FROM Service s WHERE s.deleted = true")
    List<Service> findAllDeletedServices();

    /**
     * Permanently deletes a Service entity from the database by its ID.
     *
     * <p>This method executes a DELETE operation on the Service entity,
     * removing the record with the specified ID from the database. This operation
     * is not reversible and will permanently remove the entity.</p>
     *
     * @param serviceId the ID of the Service entity to be deleted
     */
    @Modifying
    @Query("DELETE FROM Service s WHERE s.id = :serviceId")
    void permanentlyDeleteServiceById(Long serviceId);
}
