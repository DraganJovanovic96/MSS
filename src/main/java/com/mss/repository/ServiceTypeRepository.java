package com.mss.repository;

import com.mss.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * The ServiceTypeRepository interface extends JpaRepository to inherit JPA-based CRUD methods and custom
 * methods for accessing and modifying ServiceType entities.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
    /**
     * Find a service type by their id if they are not softly deleted.
     *
     * @param serviceTypeId the id of the service type
     * @return an Optional containing the service type if found, or empty if not
     */
    Optional<ServiceType> findOneById(Long serviceTypeId);

    /**
     * Find all service types by their service id if they are not softly deleted.
     *
     * @param serviceId the id of the service
     * @return a List containing service types
     */
    List<ServiceType> findAllByServiceId(Long serviceId);

    /**
     * Finds all service types that are marked as deleted.
     *
     * @return A list of service types that are marked as deleted.
     */
    @Query("SELECT s FROM ServiceType s WHERE s.deleted = true")
    List<ServiceType> findAllDeletedServiceTypes();

    /**
     * Permanently deletes a ServiceType entity from the database by its ID.
     *
     * <p>This method executes a DELETE operation on the ServiceType entity,
     * removing the record with the specified ID from the database. This operation
     * is not reversible and will permanently remove the entity.</p>
     *
     * @param serviceTypeId the ID of the ServiceType entity to be deleted
     */
    @Modifying
    @Query("DELETE FROM ServiceType s WHERE s.id = :serviceTypeId")
    void permanentlyDeleteServiceTypeById(Long serviceTypeId);
}
