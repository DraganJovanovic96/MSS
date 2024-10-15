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
     * Created CUSTOMER_FILTER attribute, so we can change Filter easily if needed.
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
     * In case that customer doesn't exist we get ResponseStatusException.NOT_FOUND.
     *
     * @param vehicleId used to find Vehicle by id
     * @param isDeleted  used to check if object is softly deleted
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
        vehicleRepository.findByVin(vehicleCreateDto.getVin())
                .ifPresent(vehicle -> {
                    if (vehicle.getDeleted()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle with that vin already exists and is deleted,check your deleted resources.");
                    }
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle with that vin already exists.");
                });
        Customer customer = customerRepository.findById(vehicleCreateDto.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer with that id doesn't exist"));

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
