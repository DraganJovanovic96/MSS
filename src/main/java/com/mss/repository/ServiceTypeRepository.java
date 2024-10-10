package com.mss.repository;

import com.mss.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The ServiceTypeRepository interface extends JpaRepository to inherit JPA-based CRUD methods and custom
 * methods for accessing and modifying ServiceType entities.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
}
