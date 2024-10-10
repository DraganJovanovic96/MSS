package com.mss.service.impl;

import com.mss.dto.VehicleDto;
import com.mss.mapper.VehicleMapper;
import com.mss.model.Vehicle;
import com.mss.repository.VehicleRepository;
import com.mss.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The VehicleServiceImpl implements VehicleService and
 * all methods that are in VehicleRepository.
 * Dependency injection was used to get beans of VehicleRepository and VehicleMapper.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    /**
     * The repository used to retrieve vehicle data.
     */
    private final VehicleRepository vehicleRepository;

    /**
     * The mapper used to convert vehicle data between VehicleDto and Vehicle entities.
     */
    private final VehicleMapper vehicleMapper;

    /**
     * Retrieves a list of all vehicles.
     *
     * @return A list of VehicleDto objects representing the vehicles.
     */
    @Override
    public List<VehicleDto> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();

        return vehicleMapper.vehiclesToVehicleDtos(vehicles);
    }
}
