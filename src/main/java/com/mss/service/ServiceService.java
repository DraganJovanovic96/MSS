package com.mss.service;

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
    List<ServiceDto> getAllServices();
}
