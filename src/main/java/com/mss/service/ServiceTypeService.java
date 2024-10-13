package com.mss.service;

import com.mss.dto.ServiceTypeCreateDto;
import com.mss.dto.ServiceTypeDto;

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
     * @return a list of all ServiceTypeDtos
     */
    List<ServiceTypeDto> getAllServiceTypes();

    /**
     * A method for saving service type. It is implemented in ServiceTypeServiceImpl class.
     *
     * @param serviceTypeCreateDto the DTO containing the data to create the new service type
     * @return the newly created ServiceType
     */
    ServiceTypeDto saveServiceType(ServiceTypeCreateDto serviceTypeCreateDto);

}
