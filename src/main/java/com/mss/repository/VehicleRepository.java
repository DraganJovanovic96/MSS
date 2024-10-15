package com.mss.repository;

import com.mss.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    /**
     * Find a vehicle by vin number.
     *
     * @param vin the vin number of the vehicle
     * @return an Optional containing the Vehicle if found, or empty if not
     */
    Optional<Vehicle> findByVin(String vin);

    /**
     * Find a vehicle by their id.
     *
     * @param vehicleId the id of the vehicle
     * @return an Optional containing the vehicle if found, or empty if not
     */
    Optional<Vehicle> findOneById(Long vehicleId);

}
