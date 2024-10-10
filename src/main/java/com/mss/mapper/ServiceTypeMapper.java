package com.mss.mapper;

import com.mss.dto.ServiceTypeDto;
import com.mss.model.ServiceType;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * ServiceTypeMapper is a mapper interface that defines mapping methods between {@link ServiceType} and{@link ServiceTypeDto}
 * classes using MapStruct library. It also enables list to list mapping.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Mapper
public interface ServiceTypeMapper {
    /**
     * Maps a ServiceType object to a ServiceTypeDto object.
     *
     * @param serviceType the ServiceType object to be mapped to a ServiceTypeDto object
     * @return a ServiceTypeDto object containing the service types's information
     */
    ServiceTypeDto serviceTypeToServiceTypeDto(ServiceType serviceType);

    /**
     * Maps a list of ServiceType objects to a list of ServiceTypeDto objects.
     *
     * @param serviceTypes the List<ServiceType> to be mapped to a List<ServiceTypeDto>
     * @return a List<ServiceTypeDto> containing the ServiceTypeDtos information
     */
    List<ServiceTypeDto> serviceTypesToServiceTypeDtos(List<ServiceType> serviceTypes);
}
