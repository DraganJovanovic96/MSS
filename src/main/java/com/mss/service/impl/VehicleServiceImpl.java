package com.mss.service.impl;

import com.mss.dto.VehicleCreateDto;
import com.mss.dto.VehicleDto;
import com.mss.mapper.VehicleMapper;
import com.mss.model.Customer;
import com.mss.model.Vehicle;
import com.mss.repository.CustomerRepository;
import com.mss.repository.VehicleRepository;
import com.mss.service.VehicleService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
     * The repository used to retrieve customer data.
     */
    private final CustomerRepository customerRepository;

    /**
     * The mapper used to convert vehicle data between VehicleDto and Vehicle entities.
     */
    private final VehicleMapper vehicleMapper;

    /**
     * Created VEHICLE_FILTER attribute, so we can change Filter easily if needed.
     */
    private static final String VEHICLE_FILTER = "deletedVehicleFilter";

    /**
     * An EntityManager instance is associated with a persistence context.
     * A persistence context is a set of entity instances in which for any
     * persistent entity identity there is a unique entity instance.
     */
    private final EntityManager entityManager;

    /**
     * Retrieves a list of all vehicles which are not deleted.
     *
     * @return A list of VehicleDto objects representing the vehicles.
     */
    @Override
    public List<VehicleDto> getAllVehicles(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(VEHICLE_FILTER);
        filter.setParameter("isDeleted", isDeleted);
        List<Vehicle> vehicles = vehicleRepository.findAll();
        session.disableFilter(VEHICLE_FILTER);

        return vehicleMapper.vehiclesToVehicleDtos(vehicles);
    }

    /**
     * A method for retrieving Vehicle entity from the database using id.
     * In case that vehicle doesn't exist we get ResponseStatusException.NOT_FOUND.
     *
     * @param vehicleId used to find Vehicle by id
     * @param isDeleted used to check if object is softly deleted
     * @return {@link VehicleDto} which contains info about specific vehicle
     */
    @Override
    public VehicleDto findVehicleById(Long vehicleId, boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(VEHICLE_FILTER);
        filter.setParameter("isDeleted", isDeleted);

        Vehicle vehicle = vehicleRepository.findOneById(vehicleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Vehicle with this id doesn't exist"));

        session.disableFilter(VEHICLE_FILTER);

        return vehicleMapper.vehicleToVehicleDto(vehicle);
    }

    /**
     * Saves a new vehicle based on the provided {@link VehicleCreateDto}.
     * Checks if a vehicle with the same VIN already exists. Throws a conflict
     * exception if it does, and if it's deleted, prompts to check deleted resources.
     * <p>
     * Validates the customer ID from the DTO. Throws a not found exception if the
     * customer doesn't exist or a conflict if it's deleted.
     * <p>
     * Maps and saves the vehicle, then returns a {@link VehicleDto}.
     *
     * @param vehicleCreateDto the DTO containing vehicle details
     * @return the saved {@link VehicleDto}
     * @throws ResponseStatusException if VIN or customer conflicts occur
     */
    @Override
    public VehicleDto saveVehicle(VehicleCreateDto vehicleCreateDto) {
        vehicleRepository.findByVin(vehicleCreateDto.getVin())
                .ifPresent(vehicle -> {
                    if (vehicle.getDeleted()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle with that vin already exists and is deleted,check your deleted resources.");
                    }
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle with that vin already exists.");
                });

        Customer customer = customerRepository.findOneById(vehicleCreateDto.getCustomerId())
                .map(customerPresent -> {
                    if (customerPresent.getDeleted()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Customer with that id already exists and is deleted, check your deleted resources.");
                    }
                    return customerPresent;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer doesn't exist"));

        Vehicle vehicle = vehicleMapper.vehicleCreateDtoToVehicle(vehicleCreateDto);
        vehicle.setCustomer(customer);
        vehicleRepository.save(vehicle);

        return vehicleMapper.vehicleToVehicleDto(vehicle);
    }

    /**
     * A method for performing soft delete of Vehicle entity. It is implemented in VehicleController class.
     *
     * @param vehicleId parameter that is unique to entity
     */
    @Override
    public void deleteVehicle(Long vehicleId) {
        vehicleRepository.findById(vehicleId)
                .map(vehicle -> {
                    if (Boolean.TRUE.equals(vehicle.getDeleted())) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle is already deleted.");
                    }

                    return vehicle;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle is not found."));

        vehicleRepository.deleteById(vehicleId);
    }
}
