package com.mss.service.impl;

import com.mss.dto.ServiceTypeDto;
import com.mss.mapper.ServiceTypeMapper;
import com.mss.model.ServiceType;
import com.mss.repository.ServiceTypeRepository;
import com.mss.service.ServiceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
