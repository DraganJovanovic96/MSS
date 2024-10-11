package com.mss.mapper;

import com.mss.dto.ServiceCreateDto;
import com.mss.dto.ServiceDto;
import com.mss.model.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * ServiceMapper is a mapper interface that defines mapping methods between {@link Service} and{@link com.mss.dto.ServiceDto}
 * classes using MapStruct library. It also enables list to list mapping.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Mapper
public interface ServiceMapper {
    /**
     * Maps a Service object to a ServiceDto object.
     *
     * @param service the Service object to be mapped to a ServiceDto object
     * @return a ServiceDto object containing the service's information
     */
    @Mapping(target = "vehicleDto", source = "service.vehicle")
    @Mapping(target = "userDto", source = "service.user")
    @Mapping(target = "serviceTypeDtos", source = "service.serviceTypes")
    ServiceDto serviceToServiceDto(Service service);

    /**
     * Maps a list of Service objects to a list of ServiceDto objects.
     *
     * @param services the List<Service> to be mapped to a List<ServiceDto>
     * @return a List<ServiceDto> containing the ServiceDtos information
     */
    List<ServiceDto> serviceToServiceDtos(List<Service> services);

    /**
     * Maps a ServiceCreateDto object to a Service object.
     *
     * @param serviceCreateDto the ServiceCreateDto object to be mapped to a Service object
     * @return a Service object containing the ServiceCreateDto's information
     */
    Service serviceCreateDtoToService(ServiceCreateDto serviceCreateDto);
}
