package com.mss.service;

import com.mss.dto.ServiceCreateDto;
import com.mss.dto.ServiceDto;

import java.util.List;

/**
 * The ServiceService interface contains methods that will be implemented is ServiceServiceImpl and methods correlate
 * to Service entity.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
public interface ServiceService {
    /**
     * A method for retrieving all services implemented in ServiceServiceImpl class.
     *
     * @return a list of all ServicesDtos
     */
    List<ServiceDto> getAllServices(boolean isDeleted);

    /**
     * A method for saving service. It is implemented in ServiceServiceImpl class.
     *
     * @param serviceCreateDto the DTO containing the data to create the new service
     * @return the newly created ServiceDto
     */
    ServiceDto saveService(ServiceCreateDto serviceCreateDto);

    /**
     * Finds a service by their unique identifier.
     *
     * @param serviceId the unique identifier of the service to retrieve
     * @return a {@link ServiceDto} representing the found service
     */
    ServiceDto findServiceById(Long serviceId, boolean isDeleted);

    /**
     * A method for deleting service. It is implemented in ServiceServiceImpl class.
     *
     * @param serviceId parameter that is unique to entity
     */
    void deleteService(Long serviceId);
}
