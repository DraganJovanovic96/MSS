package com.mss.service;

import com.mss.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * The ServiceTypeService interface contains methods that will be implemented is ServiceTypeServiceImpl and methods correlate
 * to ServiceType entity.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
public interface ServiceTypeService {
    /**
     * A method for retrieving all Service types implemented in ServiceTypeServiceImpl class.
     *
     * @param isDeleted parameter that checks if object is soft deleted
     * @return a list of all ServiceTypeDtos
     */
    List<ServiceTypeDto> getAllServiceTypes(boolean isDeleted);

    /**
     * A method for saving service type. It is implemented in ServiceTypeServiceImpl class.
     *
     * @param serviceTypeCreateDto the DTO containing the data to create the new service type
     * @return the newly created ServiceType
     */
    ServiceTypeDto saveServiceType(ServiceTypeCreateDto serviceTypeCreateDto);

    /**
     * A method for updating service type. It is implemented in ServiceTypeServiceImpl class.
     *
     * @param serviceTypeUpdateDto the DTO containing the data to update the service type
     * @return the newly updates ServiceType
     */
    ServiceTypeDto updateServiceType(ServiceTypeUpdateDto serviceTypeUpdateDto);

    /**
     * Finds a service type by their unique identifier.
     *
     * @param serviceTypeId the unique identifier of the service type to retrieve
     * @return a {@link ServiceTypeDto} representing the found  service type
     */
    ServiceTypeDto findServiceTypeById(Long serviceTypeId, boolean isDeleted);

    /**
     * A method for deleting service type. It is implemented in ServiceTypeServiceImpl class.
     *
     * @param serviceTypeId parameter that is unique to entity
     */
    void deleteServiceType(Long serviceTypeId);

    /**
     * This method first calls the serviceTypeRepository's findFilteredServiceTypes method
     * to retrieve a Page of Service Types objects that match the query.
     * It then iterates over the Service Types objects and retrieves the associated Services objects.
     *
     * @param serviceTypeFiltersQueryDto {@link ServiceTypeFiltersQueryDto} object which contains query parameters
     * @param isDeleted              boolean representing deleted objects
     * @param page                   int number of wanted page
     * @param pageSize               number of results per page
     * @return a Page of ServiceDto objects that match the specified query
     */
    Page<ServiceTypeDto> findFilteredServiceTypes(boolean isDeleted, ServiceTypeFiltersQueryDto serviceTypeFiltersQueryDto, Integer page, Integer pageSize);
}
