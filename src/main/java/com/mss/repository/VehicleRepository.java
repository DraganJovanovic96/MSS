package com.mss.repository;

import com.mss.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
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
     * Counts the number of vehicles based on their deletion status.
     *
     * @param isDeleted A boolean indicating the deletion status of vehicles to be counted.
     *                  If {@code true}, counts only deleted vehicles.
     *                  If {@code false}, counts only active vehicles.
     * @return The total number of vehicles matching the specified deletion status.
     */
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.deleted = :isDeleted")
    long countVehiclesByDeleted(@Param("isDeleted") boolean isDeleted);

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

    /**
     * Finds all vehicles that are marked as deleted.
     *
     * @return A list of vehicles that are marked as deleted.
     */
    @Query("SELECT v FROM Vehicle v WHERE v.deleted = true")
    List<Vehicle> findAllDeletedVehicles();

    /**
     * Finds all vehicles that are marked as deleted and have been deleted for longer than one week.
     *
     * @param oneWeekAgo The date and time representing one week ago.
     * @return A list of vehicles that have been deleted for longer than one week.
     */
    @Query("SELECT v FROM Vehicle v WHERE v.deleted = true AND v.deletedAt <= :oneWeekAgo")
    List<Vehicle> findVehiclesDeletedOlderThanOneWeek(@Param("oneWeekAgo") Instant oneWeekAgo);

    /**
     * Permanently deletes a Vehicle entity from the database by its ID.
     *
     * <p>This method executes a DELETE operation on the Vehicle entity,
     * removing the record with the specified ID from the database. This operation
     * is not reversible and will permanently remove the entity.</p>
     *
     * @param vehicleId the ID of the User entity to be deleted
     */
    @Modifying
    @Query("DELETE FROM Vehicle v WHERE v.id = :vehicleId")
    void permanentlyDeleteVehicleById(Long vehicleId);

    /**
     * Permanently deletes specific Vehicle entities provided as a list.
     *
     * @param deletedVehicleIds The list of Vehicle IDs to be permanently deleted.
     */
    @Modifying
    @Query("DELETE FROM Vehicle v WHERE v.id IN :deletedVehicleIds")
    void permanentlyDeleteAllDeletedVehicles(List<Long> deletedVehicleIds);

}
