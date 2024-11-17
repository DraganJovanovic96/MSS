package com.mss.service.impl;

import com.mss.dto.ServiceTypeCreateDto;
import com.mss.dto.ServiceTypeDto;
import com.mss.dto.ServiceTypeUpdateDto;
import com.mss.mapper.ServiceTypeMapper;
import com.mss.model.Service;
import com.mss.model.ServiceType;
import com.mss.repository.ServiceRepository;
import com.mss.repository.ServiceTypeRepository;
import com.mss.service.ServiceTypeService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

/**
 * The ServiceTypeServiceImpl implements ServiceTypeService and
 * all methods that are in ServiceTypeRepository.
 * Dependency injection was used to get beans of ServiceTypeRepository and ServiceTypesMapper.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceTypeServiceImpl implements ServiceTypeService {
    /**
     * The repository used to retrieve service type data.
     */
    private final ServiceTypeRepository serviceTypeRepository;

    /**
     * The repository used to retrieve service data.
     */
    private final ServiceRepository serviceRepository;

    /**
     * The mapper used to convert service type data between ServiceTypeDto and ServiceType entities.
     */
    private final ServiceTypeMapper serviceTypeMapper;

    /**
     * Created SERVICE_TYPE_FILTER attribute, so we can change Filter easily if needed.
     */
    private static final String SERVICE_TYPE_FILTER = "deletedServiceTypeFilter";

    /**
     * An EntityManager instance is associated with a persistence context.
     * A persistence context is a set of entity instances in which for any
     * persistent entity identity there is a unique entity instance.
     */
    private final EntityManager entityManager;

    /**
     * Retrieves a list of all services types.
     *
     * @return A list of ServiceTypeDto objects representing the services.
     */
    @Override
    public List<ServiceTypeDto> getAllServiceTypes(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(SERVICE_TYPE_FILTER);
        filter.setParameter("isDeleted", isDeleted);
        List<ServiceType> serviceTypes = serviceTypeRepository.findAll();
        session.disableFilter(SERVICE_TYPE_FILTER);

        return serviceTypeMapper.serviceTypesToServiceTypeDtos(serviceTypes);
    }

    /**
     * Saves a new service type based on the provided ServiceTypeCreateDto.
     *
     * @param serviceTypeCreateDto the data transfer object containing information for creating a new service type.
     * @return a ServiceTypeDto representing the saved service type.
     * @throws ResponseStatusException if the service with the specified ID does not exist.
     */
    @Override
    public ServiceTypeDto saveServiceType(ServiceTypeCreateDto serviceTypeCreateDto) {
        Service service = serviceRepository.findOneById(serviceTypeCreateDto.getServiceId())
                .map(servicePresent -> {
                    if (servicePresent.getDeleted()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Service with that id already exists and is deleted, check your deleted resources.");
                    }
                    return servicePresent;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service doesn't exist"));

        ServiceType serviceType = serviceTypeMapper.serviceTypeCreateDtoToServiceType(serviceTypeCreateDto);
        serviceType.setService(service);
        serviceTypeRepository.save(serviceType);

        return serviceTypeMapper.serviceTypeToServiceTypeDto(serviceType);
    }

    /**
     * A method for updating service type. It is implemented in ServiceTypeServiceImpl class.
     *
     * @param serviceTypeUpdateDto the DTO containing the data to update the service type
     * @return the newly updates ServiceType
     */
    @Override
    @Transactional
    public ServiceTypeDto updateServiceType(ServiceTypeUpdateDto serviceTypeUpdateDto) {
        ServiceType serviceType = serviceTypeRepository.findOneById(serviceTypeUpdateDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Service type with this id doesn't exist"));

        Service service = serviceRepository.findOneById(serviceTypeUpdateDto.getServiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " Service with this id doesn't exist"));

        serviceType.setUpdatedAt(Instant.now());
        serviceType.setTypeOfService(serviceTypeUpdateDto.getTypeOfService());
        serviceType.setDeleted(serviceTypeUpdateDto.getDeleted());
        serviceType.setDescription(serviceTypeUpdateDto.getDescription());
        serviceType.setPrice(serviceTypeUpdateDto.getPrice());
        serviceType.setService(service);

        serviceTypeRepository.save(serviceType);
        entityManager.flush();
        return serviceTypeMapper.serviceTypeToServiceTypeDto(serviceType);
    }

    /**
     * A method for retrieving Service Type entity from the database using id.
     * In case that service type doesn't exist we get ResponseStatusException.NOT_FOUND.
     *
     * @param serviceTypeId used to find Service Type by id
     * @param isDeleted     used to check if object is softly deleted
     * @return {@link ServiceTypeDto} which contains info about specific service type
     */
    @Override
    public ServiceTypeDto findServiceTypeById(Long serviceTypeId, boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(SERVICE_TYPE_FILTER);
        filter.setParameter("isDeleted", isDeleted);

        ServiceType serviceType = serviceTypeRepository.findOneById(serviceTypeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service type with this id doesn't exist"));

        session.disableFilter(SERVICE_TYPE_FILTER);

        return serviceTypeMapper.serviceTypeToServiceTypeDto(serviceType);
    }

    /**
     * A method for performing soft delete of service type entity. It is implemented in ServiceTypeController class.
     *
     * @param serviceTypeId parameter that is unique to entity
     */
    @Override
    public void deleteServiceType(Long serviceTypeId) {
        serviceTypeRepository.findById(serviceTypeId)
                .map(service -> {
                    if (Boolean.TRUE.equals(service.getDeleted())) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Type is already deleted.");
                    }

                    return service;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service Type is not found."));

        serviceTypeRepository.deleteById(serviceTypeId);
    }
}
