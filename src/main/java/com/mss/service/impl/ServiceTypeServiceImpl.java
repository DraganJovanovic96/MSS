package com.mss.service.impl;

import com.mss.dto.ServiceTypeCreateDto;
import com.mss.dto.ServiceTypeDto;
import com.mss.mapper.ServiceTypeMapper;
import com.mss.model.ServiceType;
import com.mss.repository.ServiceRepository;
import com.mss.repository.ServiceTypeRepository;
import com.mss.service.ServiceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
@Service
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
     * Retrieves a list of all services types.
     *
     * @return A list of ServiceTypeDto objects representing the services.
     */
    @Override
    public List<ServiceTypeDto> getAllServiceTypes() {
        List<ServiceType> serviceTypes = serviceTypeRepository.findAll();

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
        com.mss.model.Service service = serviceRepository.findById(serviceTypeCreateDto.getServiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service is not found"));

        ServiceType serviceType = serviceTypeMapper.serviceTypeCreateDtoToServiceType(serviceTypeCreateDto);
        serviceType.setService(service);
        serviceTypeRepository.save(serviceType);

        return serviceTypeMapper.serviceTypeToServiceTypeDto(serviceType);
    }
}
