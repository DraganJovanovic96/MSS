package com.mss.service.impl;

import com.mss.dto.ServiceCreateDto;
import com.mss.dto.ServiceDto;
import com.mss.mapper.ServiceMapper;
import com.mss.model.Service;
import com.mss.model.User;
import com.mss.model.Vehicle;
import com.mss.repository.ServiceRepository;
import com.mss.repository.UserRepository;
import com.mss.repository.VehicleRepository;
import com.mss.service.ServiceService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
     * Created SERVICE_FILTER attribute, so we can change Filter easily if needed.
     */
    private static final String SERVICE_FILTER = "deletedServiceFilter";

    /**
     * An EntityManager instance is associated with a persistence context.
     * A persistence context is a set of entity instances in which for any
     * persistent entity identity there is a unique entity instance.
     */
    private final EntityManager entityManager;

    /**
     * Retrieves a list of all services that are not deleted.
     *
     * @return A list of ServiceDto objects representing the services.
     */
    @Override
    public List<ServiceDto> getAllServices(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(SERVICE_FILTER);
        filter.setParameter("isDeleted", isDeleted);
        List<Service> services = serviceRepository.findAll();
        session.disableFilter(SERVICE_FILTER);

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
        User user = userRepository.findById(serviceCreateDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist"));

        Vehicle vehicle = vehicleRepository.findById(serviceCreateDto.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle doesn't exist"));

        com.mss.model.Service service = serviceMapper.serviceCreateDtoToService(serviceCreateDto);
        service.setVehicle(vehicle);
        service.setUser(user);

        return serviceMapper.serviceToServiceDto(serviceRepository.save(service));
    }

    /**
     * @param serviceId the unique identifier of the service to retrieve
     * @param isDeleted
     * @return
     */
    @Override
    public ServiceDto findServiceById(Long serviceId, boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(SERVICE_FILTER);
        filter.setParameter("isDeleted", isDeleted);

        Service service = serviceRepository.findOneById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service with this id doesn't exist"));

        session.disableFilter(SERVICE_FILTER);

        return serviceMapper.serviceToServiceDto(service);
    }

    /**
     * @param serviceId parameter that is unique to entity
     */
    @Override
    public void deleteService(Long serviceId) {
        serviceRepository.findById(serviceId)
                .map(service -> {
                    if (Boolean.TRUE.equals(service.getDeleted())) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service is already deleted.");
                    }

                    return service;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service is not found."));

        serviceRepository.deleteById(serviceId);
    }
}
