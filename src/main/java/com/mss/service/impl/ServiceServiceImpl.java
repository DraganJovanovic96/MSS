package com.mss.service.impl;

import com.mss.dto.ServiceCreateDto;
import com.mss.dto.ServiceDto;
import com.mss.dto.ServiceFiltersQueryDto;
import com.mss.dto.ServiceUpdateDto;
import com.mss.mapper.ServiceMapper;
import com.mss.mapper.UserMapper;
import com.mss.mapper.VehicleMapper;
import com.mss.model.Service;
import com.mss.model.ServiceType;
import com.mss.model.User;
import com.mss.model.Vehicle;
import com.mss.repository.*;
import com.mss.service.ServiceService;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
     * The repository used to retrieve service data.
     */
    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * The custom repository used to retrieve service data.
     */
    private final ServiceCustomRepository serviceCustomRepository;

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
     * The mapper used to convert service data between ServiceDto and Service entities.
     */
    private final UserMapper userMapper;

    /**
     * The mapper used to convert service data between ServiceDto and Service entities.
     */
    private final VehicleMapper vehicleMapper;

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
        User user = userRepository.findOneById(serviceCreateDto.getUserId())
                .map(userPresent -> {
                    if (userPresent.getDeleted()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "User with that id already exists and is deleted, check your deleted resources.");
                    }
                    return userPresent;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist"));

        Vehicle vehicle = vehicleRepository.findOneById(serviceCreateDto.getVehicleId())
                .map(vehiclePresent -> {
                    if (vehiclePresent.getDeleted()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle with that id already exists and is deleted, check your deleted resources.");
                    }
                    return vehiclePresent;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle doesn't exist"));

        Service service = serviceMapper.serviceCreateDtoToService(serviceCreateDto);
        service.setVehicle(vehicle);
        service.setUser(user);
        service.setInvoiceCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        vehicleRepository.save(vehicle);

        return serviceMapper.serviceToServiceDto(serviceRepository.save(service));
    }

    /**
     * @param serviceId the unique identifier of the service to retrieve
     * @param isDeleted boolean that represents if service is deleted or now
     * @return ServiceDto that contains service data
     */
    @Override
    public ServiceDto findServiceById(Long serviceId, boolean isDeleted) {
        Service service = serviceRepository.findOneById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service with this id doesn't exist"));

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

                    for (ServiceType serviceType : service.getServiceTypes()) {
                        if (Boolean.FALSE.equals(serviceType.getDeleted()) && Boolean.FALSE.equals(serviceType.getDeletedByCascade())) {
                            serviceType.setDeletedByCascade(true);
                            serviceTypeRepository.save(serviceType);
                        }
                    }
                    return service;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service is not found."));

        serviceRepository.deleteById(serviceId);
    }

    /**
     * Retrieves a paginated list of services based on the provided filters and deletion status.
     * The method applies a filter to include or exclude deleted services and then retrieves
     * services that match the criteria specified in the {@link ServiceFiltersQueryDto}.
     * The services are mapped to {@link ServiceDto} objects before being returned as a paginated result.
     *
     * @param isDeleted              a boolean indicating whether to include deleted services in the results.
     * @param serviceFiltersQueryDto the {@link ServiceFiltersQueryDto} containing the filter criteria for services.
     *                               If any field is null, it will be ignored in the query.
     * @param page                   the page number for pagination.
     * @param pageSize               the size of each page for pagination.
     * @return a {@link Page} of {@link ServiceDto} objects representing the services that match the filter criteria.
     * The page contains the list of services, pagination details, and total number of rows.
     */
    @Override
    public Page<ServiceDto> findFilteredServices(boolean isDeleted, ServiceFiltersQueryDto serviceFiltersQueryDto, Integer page, Integer pageSize) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(SERVICE_FILTER);
        filter.setParameter("isDeleted", isDeleted);

        Page<Service> resultPage = serviceCustomRepository.findFilteredServices(serviceFiltersQueryDto, PageRequest.of(page, pageSize));
        List<Service> services = resultPage.getContent();

        session.disableFilter(SERVICE_FILTER);

        List<ServiceDto> serviceDtos = serviceMapper.serviceToServiceDtos(services);

        return new PageImpl<>(serviceDtos, resultPage.getPageable(), resultPage.getTotalElements());
    }

    /**
     * Updates an existing service with the provided details.
     *
     * <p>This method accepts a {@link ServiceUpdateDto} containing updated information for a
     * specific service, modifies the service's properties accordingly, and returns the updated
     * {@link ServiceDto} object. This operation typically includes updating service attributes.</p>
     *
     * @param serviceUpdateDto a DTO containing the updated details of the service
     * @return {@link ServiceDto} the updated service data, encapsulated in a DTO for response
     */
    @Override
    @Transactional
    public ServiceDto updateCustomer(ServiceUpdateDto serviceUpdateDto) {
        Service service = serviceRepository.findOneById(serviceUpdateDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Service with this id doesn't exist"));

        Vehicle vehicle = vehicleRepository.findOneById(serviceUpdateDto.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Vehicle with this id doesn't exist"));

        User user = userRepository.findOneById(serviceUpdateDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " User with this id doesn't exist"));

        service.setUpdatedAt(Instant.now());
        service.setDeleted(serviceUpdateDto.getDeleted());
        service.setStartDate(serviceUpdateDto.getStartDate());
        service.setEndDate(serviceUpdateDto.getEndDate());
        service.setCurrentMileage(serviceUpdateDto.getCurrentMileage());
        service.setNextServiceMileage(serviceUpdateDto.getNextServiceMileage());
        service.setVehicle(vehicle);
        service.setUser(user);

        for (ServiceType serviceType : service.getServiceTypes()) {
            if (Boolean.TRUE.equals(serviceType.getDeletedByCascade()) && Boolean.TRUE.equals(serviceType.getDeleted())) {
                serviceType.setDeleted(false);
                serviceType.setDeletedByCascade(false);
                serviceTypeRepository.save(serviceType);
            }
        }

        serviceRepository.save(service);
        entityManager.flush();

        return serviceMapper.serviceToServiceDto(service);
    }
}
