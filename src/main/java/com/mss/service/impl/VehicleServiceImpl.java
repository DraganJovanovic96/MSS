package com.mss.service.impl;

import com.mss.dto.VehicleCreateDto;
import com.mss.dto.VehicleDto;
import com.mss.dto.VehicleFiltersQueryDto;
import com.mss.dto.VehicleUpdateDto;
import com.mss.mapper.VehicleMapper;
import com.mss.model.Customer;
import com.mss.model.ServiceType;
import com.mss.model.Vehicle;
import com.mss.repository.*;
import com.mss.service.VehicleService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
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
     * The repository used to retrieve service type data.
     */
    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * The repository used to retrieve vehicle data.
     */
    private final ServiceRepository serviceRepository;

    /**
     * The repository used to retrieve vehicle data.
     */
    private final VehicleCustomRepository vehicleCustomRepository;

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
     * Counts the number of vehicles based on their deletion status.
     *
     * @param isDeleted A boolean indicating the deletion status of vehicles to be counted.
     *                  If {@code true}, counts only deleted vehicles.
     *                  If {@code false}, counts only active vehicles.
     * @return The total number of vehicles matching the specified deletion status.
     */
    @Override
    public long getVehicleCount(boolean isDeleted) {
        return vehicleRepository.countVehiclesByDeleted(isDeleted);
    }

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
     * @return {@link VehicleDto} which contains info about specific vehicle
     */
    @Override
    public VehicleDto findVehicleById(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findOneById(vehicleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Vehicle with this id doesn't exist"));

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
        vehicle.setVin(vehicle.getVin().toUpperCase());
        vehicleRepository.save(vehicle);

        return vehicleMapper.vehicleToVehicleDto(vehicle);
    }

    /**
     * A method for performing soft delete of Vehicle entity. It is implemented in VehicleController class.
     *
     * @param vehicleId parameter that is unique to entity
     */
    @Override
    @Transactional
    public void deleteVehicle(Long vehicleId) {
        vehicleRepository.findById(vehicleId)
                .map(vehicle -> {
                    if (Boolean.TRUE.equals(vehicle.getDeleted())) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle is already deleted.");
                    }

                    Instant now = Instant.now();

                    for (com.mss.model.Service service : vehicle.getServices()) {
                        if (Boolean.FALSE.equals(service.getDeleted())) {

                            for (ServiceType serviceType : service.getServiceTypes()) {
                                if (Boolean.FALSE.equals(serviceType.getDeleted()) && Boolean.FALSE.equals(serviceType.getDeletedByCascade())) {
                                    serviceType.setDeletedByCascade(true);
                                    serviceType.setDeletedAt(now);
                                    serviceTypeRepository.save(serviceType);
                                }
                            }
                            service.setDeletedByCascade(true);
                            service.setDeletedAt(now);
                            serviceRepository.save(service);
                        }
                    }
                    vehicle.setDeletedAt(now);
                    entityManager.flush();
                    return vehicle;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle is not found."));

        vehicleRepository.deleteById(vehicleId);
    }

    /**
     * Updates the details of an existing vehicle based on the provided {@link VehicleUpdateDto}.
     *
     * <p>This method retrieves a vehicle by its ID from the repository, applies updates to
     * fields such as the year of manufacture, model, license plate, and manufacturer,
     * and updates its deletion status as specified in the DTO. After modification,
     * the updated vehicle is saved back to the repository, and a {@link VehicleDto}
     * representation of the updated vehicle is returned.</p>
     *
     * @param vehicleUpdateDto a DTO containing the new details for the vehicle update
     * @return {@link VehicleDto} the updated vehicle data, encapsulated in a DTO format for response
     * @throws ResponseStatusException with {@code HttpStatus.NOT_FOUND} if no vehicle exists with the specified ID
     */
    @Override
    @Transactional
    public VehicleDto updateVehicle(VehicleUpdateDto vehicleUpdateDto) {
        Vehicle vehicle = vehicleRepository.findOneById(vehicleUpdateDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Vehicle with this id doesn't exist"));

        Customer customer = customerRepository.findOneById(vehicleUpdateDto.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Customer with this id doesn't exist"));

        vehicle.setUpdatedAt(Instant.now());
        vehicle.setYearOfManufacture(vehicleUpdateDto.getYearOfManufacture());
        vehicle.setModel(vehicleUpdateDto.getModel());
        vehicle.setVehiclePlate(vehicleUpdateDto.getVehiclePlate());
        vehicle.setManufacturer(vehicleUpdateDto.getManufacturer());
        vehicle.setDeleted(vehicleUpdateDto.getDeleted());
        vehicle.setVin(vehicleUpdateDto.getVin().toUpperCase());
        vehicle.setCustomer(customer);

        if (!vehicleUpdateDto.getDeleted()) {
            vehicle.setDeletedAt(null);
        }

        for (com.mss.model.Service service : vehicle.getServices()) {
            if (Boolean.TRUE.equals(service.getDeletedByCascade()) && Boolean.TRUE.equals(service.getDeleted())) {
                service.setDeleted(false);
                service.setDeletedByCascade(false);
                service.setDeletedAt(null);

                for (ServiceType serviceType : service.getServiceTypes()) {
                    if (Boolean.TRUE.equals(serviceType.getDeletedByCascade()) && Boolean.TRUE.equals(serviceType.getDeleted())) {
                        serviceType.setDeleted(false);
                        serviceType.setDeletedByCascade(false);
                        serviceType.setDeletedAt(null);
                        serviceTypeRepository.save(serviceType);
                    }
                }
                serviceRepository.save(service);
            }
        }
        vehicleRepository.save(vehicle);
        entityManager.flush();
        return vehicleMapper.vehicleToVehicleDto(vehicle);
    }

    /**
     * This method first calls the vehicleRepository's findFilteredVehicles method
     * to retrieve a Page of Vehicle objects that match the query.
     * It then iterates over the Vehicle objects and retrieves the associated Customer objects.
     *
     * @param isDeleted              boolean representing deleted objects
     * @param vehicleFiltersQueryDto {@link VehicleFiltersQueryDto} object which contains query parameters
     * @param page                   int number of wanted page
     * @param pageSize               number of results per page
     * @return a Page of ServiceDto objects that match the specified query
     */
    @Override
    public Page<VehicleDto> findFilteredVehicles(boolean isDeleted, VehicleFiltersQueryDto vehicleFiltersQueryDto, Integer page, Integer pageSize) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(VEHICLE_FILTER);
        filter.setParameter("isDeleted", isDeleted);

        Page<Vehicle> resultPage = vehicleCustomRepository.findFilteredVehicles(vehicleFiltersQueryDto, PageRequest.of(page, pageSize));
        List<Vehicle> vehicles = resultPage.getContent();

        session.disableFilter(VEHICLE_FILTER);

        List<VehicleDto> vehicleDtos = vehicleMapper.vehiclesToVehicleDtos(vehicles);

        return new PageImpl<>(vehicleDtos, resultPage.getPageable(), resultPage.getTotalElements());
    }
}
