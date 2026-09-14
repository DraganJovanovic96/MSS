package com.mss.mapper;

import com.mss.dto.VacationRequestCreateDto;
import com.mss.dto.VacationRequestDto;
import com.mss.dto.VacationRequestUpdateDto;
import com.mss.model.VacationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * VacationRequestMapper is a mapper interface that defines mapping methods between {@link VacationRequest}
 * and DTO classes using MapStruct library. It also enables list to list mapping.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface VacationRequestMapper {
    /**
     * Maps a VacationRequest object to a VacationRequestDto object.
     *
     * @param vacationRequest the VacationRequest object to be mapped to a VacationRequestDto object
     * @return a VacationRequestDto object containing the vacation request's information
     */
    VacationRequestDto vacationRequestToVacationRequestDto(VacationRequest vacationRequest);

    /**
     * Maps a list of VacationRequest objects to a list of VacationRequestDto objects.
     *
     * @param vacationRequests the List<VacationRequest> to be mapped to a List<VacationRequestDto>
     * @return a List<VacationRequestDto> containing the vacation requests information
     */
    List<VacationRequestDto> vacationRequestsToVacationRequestDtos(List<VacationRequest> vacationRequests);
}
