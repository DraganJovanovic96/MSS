package com.mss.service;

import com.mss.dto.VehicleCreateDto;
import com.mss.dto.VehicleDto;
import com.mss.dto.VehicleUpdateDto;

import java.util.List;

/**
 * The VehicleService interface contains methods that will be implemented is VehicleServiceImpl and methods correlate
 * to Vehicle entity.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
public interface VehicleService {

    /**
     * A method for retrieving all vehicles implemented in VehicleServiceImpl class.
     *
     * @param isDeleted parameter that checks if object is soft deleted
     * @return a list of all VehicleDtos
     */
    List<VehicleDto> getAllVehicles(boolean isDeleted);

    /**
     * Finds a vehicle by their unique identifier.
     *
     * @param vehicleId the unique identifier of the vehicle to retrieve
     * @return a {@link VehicleDto} representing the found vehicle
     */
    VehicleDto findVehicleById(Long vehicleId);

    /**
     * A method for saving vehicles. It is implemented in VehicleServiceImpl class.
     *
     * @param vehicleCreateDto the DTO containing the data to create the new vehicle
     * @return the newly created VehicleDto
     */
    VehicleDto saveVehicle(VehicleCreateDto vehicleCreateDto);

    /**
     * A method for deleting vehicle. It is implemented in VehicleServiceImpl class.
     *
     * @param vehicleId parameter that is unique to entity
     */
    void deleteVehicle(Long vehicleId);

    /**
     * Updates an existing vehicle with the provided details.
     *
     * <p>This method accepts a {@link VehicleUpdateDto} containing updated information for a
     * specific vehicle, modifies the vehicle's properties accordingly, and returns the updated
     * {@link VehicleDto} object. This operation typically includes updating vehicle attributes like
     * make, model, year, and other relevant fields.</p>
     *
     * @param vehicleUpdateDto a DTO containing the updated details of the vehicle
     * @return {@link VehicleDto} the updated vehicle data, encapsulated in a DTO for response
     */
    VehicleDto updateVehicle(VehicleUpdateDto vehicleUpdateDto);
}
