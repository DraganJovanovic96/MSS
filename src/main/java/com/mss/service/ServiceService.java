package com.mss.service;

import com.mss.dto.ServiceCreateDto;
import com.mss.dto.ServiceDto;
import com.mss.dto.ServiceFiltersQueryDto;
import org.springframework.data.domain.Page;

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
     * @param isDeleted parameter that checks if object is soft deleted
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

    /**
     * This method first calls the serviceRepository's findFilteredServices method
     * to retrieve a Page of Service objects that match the query.
     * It then iterates over the Service objects and retrieves the associated Vehicle and User objects
     * using the getVehicle and getUser methods.
     *
     * @param serviceFiltersQueryDto {@link ServiceFiltersQueryDto} object which contains query parameters
     * @param isDeleted              boolean representing deleted objects
     * @param page                   int number of wanted page
     * @param pageSize               number of results per page
     * @return a Page of ServiceDto objects that match the specified query
     */
    Page<ServiceDto> findFilteredServices(boolean isDeleted, ServiceFiltersQueryDto serviceFiltersQueryDto, Integer page, Integer pageSize);
}
