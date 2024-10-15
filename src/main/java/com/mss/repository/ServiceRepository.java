package com.mss.repository;

import com.mss.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
     * Find a service by their id if they are not soft deleted.
     *
     * @param serviceId the id of the service
     * @return an Optional containing the service if found, or empty if not
     */
    Optional<Service> findOneById(Long serviceId);
}
