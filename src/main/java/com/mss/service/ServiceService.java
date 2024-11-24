package com.mss.service;

import com.mss.dto.*;
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
     * Counts the number of services based on their deletion status.
     *
     * @param isDeleted A boolean indicating the deletion status of services to be counted.
     *                  If {@code true}, counts only deleted services.
     *                  If {@code false}, counts only active services.
     * @return The total number of services matching the specified deletion status.
     */
    long getServiceCount(boolean isDeleted);

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
    ServiceDto updateCustomer(ServiceUpdateDto serviceUpdateDto);
}
