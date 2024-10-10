package com.mss.service.impl;

import com.mss.dto.ServiceDto;
import com.mss.mapper.ServiceMapper;
import com.mss.repository.ServiceRepository;
import com.mss.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {
    /**
     * The repository used to retrieve service data.
     */
    private final ServiceRepository serviceRepository;

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
}
