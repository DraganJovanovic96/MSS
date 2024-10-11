package com.mss.service.impl;

import com.mss.dto.ServiceCreateDto;
import com.mss.dto.ServiceDto;
import com.mss.mapper.ServiceMapper;
import com.mss.model.User;
import com.mss.model.Vehicle;
import com.mss.repository.ServiceRepository;
import com.mss.repository.UserRepository;
import com.mss.repository.VehicleRepository;
import com.mss.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * The ServiceServiceImpl implements ServiceService and
 * all methods that are in ServiceRepository.
 * Dependency injection was used to get beans of ServiceRepository and ServiceMapper.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {
    /**
     * The repository used to retrieve service data.
     */
    private final ServiceRepository serviceRepository;

    /**
     * The repository used to retrieve user data.
     */
    private final UserRepository userRepository;

    /**
     * The repository used to retrieve user data.
     */
    private final VehicleRepository vehicleRepository;

    /**
     * The mapper used to convert service data between ServiceDto and Service entities.
     */
    private final ServiceMapper serviceMapper;

    /**
     * Retrieves a list of all services.
     *
     * @return A list of ServiceDto objects representing the services.
     */
    @Override
    public List<ServiceDto> getAllServices() {
        List<com.mss.model.Service> services = serviceRepository.findAll();

        return serviceMapper.serviceToServiceDtos(services);
    }

    /**
     * Saves a new service based on the provided ServiceCreateDto.
     * <p>
     * This method retrieves the user and vehicle associated with the provided user ID and vehicle ID
     * from the ServiceCreateDto. If both the user and vehicle exist, the service is mapped from the DTO,
     * associated with the user and vehicle, and then saved to the repository.
     * The saved service is then converted to a ServiceDto and returned.
     * If either the user or vehicle is not found, a ResponseStatusException is thrown with a 404 status code.
     *
     * @param serviceCreateDto the data transfer object containing service information to be saved.
     * @return the ServiceDto representing the saved service.
     * @throws ResponseStatusException if the user or vehicle with the provided IDs does not exist.
     */
    @Override
    public ServiceDto saveService(ServiceCreateDto serviceCreateDto) {
        Optional<User> user = userRepository.findById(serviceCreateDto.getUserId());

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist");
        }

        Optional<Vehicle> vehicle = vehicleRepository.findById(serviceCreateDto.getVehicleId());
        if (vehicle.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle doesn't exist");
        }

        com.mss.model.Service service = serviceMapper.serviceCreateDtoToService(serviceCreateDto);
        service.setVehicle(vehicle.get());
        service.setUser(user.get());

        return serviceMapper.serviceToServiceDto(serviceRepository.save(service));
    }
}
