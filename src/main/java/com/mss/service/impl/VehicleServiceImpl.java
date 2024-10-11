package com.mss.service.impl;

import com.mss.dto.VehicleCreateDto;
import com.mss.dto.VehicleDto;
import com.mss.mapper.VehicleMapper;
import com.mss.model.Customer;
import com.mss.model.Vehicle;
import com.mss.repository.CustomerRepository;
import com.mss.repository.VehicleRepository;
import com.mss.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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
     * The repository used to retrieve customer data.
     */
    private final CustomerRepository customerRepository;

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

    /**
     * Saves a new vehicle based on the provided VehicleCreateDto.
     * <p>
     * This method retrieves the customer associated with the provided customer ID from the VehicleCreateDto.
     * If the customer exists, the vehicle is mapped from the DTO, associated with the customer,
     * and then saved to the repository. The saved vehicle is then converted to a VehicleDto and returned.
     * If the customer is not found, a ResponseStatusException is thrown with a 404 status code.
     *
     * @param vehicleCreateDto the data transfer object containing vehicle information to be saved.
     * @return the VehicleDto representing the saved vehicle.
     * @throws ResponseStatusException if the customer with the provided ID does not exist.
     */
    @Override
    public VehicleDto saveVehicle(VehicleCreateDto vehicleCreateDto) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findByVin(vehicleCreateDto.getVin());

        if (optionalVehicle.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle with that vin already exists");
        }

        Optional<Customer> optionalCustomer = customerRepository.findById(vehicleCreateDto.getCustomerId());

        if (optionalCustomer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer with that id doesn't exist");
        }

        Customer customer = optionalCustomer.get();
        Vehicle vehicle = vehicleMapper.vehicleCreateDtoToVehicle(vehicleCreateDto);
        vehicle.setCustomer(customer);
        vehicleRepository.save(vehicle);

        return vehicleMapper.vehicleToVehicleDto(vehicle);
    }
}


