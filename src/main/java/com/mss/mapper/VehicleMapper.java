package com.mss.mapper;

import com.mss.dto.VehicleCreateDto;
import com.mss.dto.VehicleDto;
import com.mss.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * VehicleMapper is a mapper interface that defines mapping methods between {@link com.mss.model.Vehicle} and{@link VehicleDto}
 * classes using MapStruct library. It also enables list to list mapping.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Mapper(uses = ServiceMapper.class)
public interface VehicleMapper {
    /**
     * Maps a Customer object to a CustomerDto object.
     *
     * @param vehicle the vehicle object to be mapped to a VehicleDto object
     * @return a VehicleDto object containing the vehicle's information
     */
    @Mapping(target = "customerDto", source = "customer")
    @Mapping(target = "serviceDtos", source = "services")
    VehicleDto vehicleToVehicleDto(Vehicle vehicle);

    /**
     * Maps a list of Vehicle objects to a list of VehicleDto objects.
     *
     * @param vehicles the List<Vehicle> to be mapped to a List<VehicleDto>
     * @return a List<VehicleDto> containing the vehicles information
     */
    List<VehicleDto> vehiclesToVehicleDtos(List<Vehicle> vehicles);

    /**
     * Maps a CustomerCreateDto object to a Customer object.
     *
     * @param vehicleCreateDto the VehicleCreateDto object to be mapped to a Vehicle object
     * @return a Vehicle object containing the VehicleCreateDto's information
     */
    Vehicle vehicleCreateDtoToVehicle(VehicleCreateDto vehicleCreateDto);
}
