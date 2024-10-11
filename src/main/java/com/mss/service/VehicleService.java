package com.mss.service;

import com.mss.dto.VehicleCreateDto;
import com.mss.dto.VehicleDto;

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
     * @return a list of all VehicleDtos
     */
    List<VehicleDto> getAllVehicles();

    /**
     * A method for saving vehicles. It is implemented in VehicleServiceImpl class.
     *
     * @param vehicleCreateDto the DTO containing the data to create the new vehicle
     * @return the newly created VehicleDto
     */
    VehicleDto saveVehicle(VehicleCreateDto vehicleCreateDto);
}
