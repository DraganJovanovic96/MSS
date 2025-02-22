package com.mss.mapper;

import com.mss.dto.ServiceTypeCreateDto;
import com.mss.dto.ServiceTypeDto;
import com.mss.model.ServiceType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
     * @return a ServiceTypeDto object containing the service types' information
     */
    @Mapping(target = "serviceDto", source = "service")
    ServiceTypeDto serviceTypeToServiceTypeDto(ServiceType serviceType);

    /**
     * Maps a ServiceTypeCreateDto object to a ServiceType object.
     *
     * @param serviceTypeCreateDto the ServiceTypeCreateDto object to be mapped to a ServiceType object
     * @return a ServiceType object containing the service types' information
     */
    ServiceType serviceTypeCreateDtoToServiceType(ServiceTypeCreateDto serviceTypeCreateDto);

    /**
     * Maps a list of ServiceType objects to a list of ServiceTypeDto objects.
     *
     * @param serviceTypes the List<ServiceType> to be mapped to a List<ServiceTypeDto>
     * @return a List<ServiceTypeDto> containing the ServiceTypeDtos information
     */
    List<ServiceTypeDto> serviceTypesToServiceTypeDtos(List<ServiceType> serviceTypes);
}
