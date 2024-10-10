package com.mss.repository;

import com.mss.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The VehicleRepository interface extends JpaRepository to inherit JPA-based CRUD methods and custom
 * methods for accessing and modifying Vehicle entities.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

}
