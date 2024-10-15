package com.mss.repository;

import com.mss.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

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
     * Find a service type by their id if they are not soft deleted.
     *
     * @param serviceTypeId the id of the service type
     * @return an Optional containing the service type if found, or empty if not
     */
    Optional<ServiceType> findOneById(Long serviceTypeId);
}
