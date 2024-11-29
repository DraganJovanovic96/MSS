package com.mss.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * A Data Transfer Object (DTO) for transferring benefit data between layers of the application.
 * It extends the {@link BaseEntityDto} class.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class CustomerDto extends BaseEntityDto {
    /**
     * The customer's firstname.
     */
    private String firstname;

    /**
     * The customer's lastname.
     */
    private String lastname;

    /**
     * The customer's address.
     */
    private String address;

    /**
     * The customer's email.
     */
    private String email;


    /**
     * The customer's phone number.
     */
    private String phoneNumber;

    /**
     * The customer's vehicles.
     */
    @JsonIgnoreProperties({"customerDto", "serviceDtos"})
    private List<VehicleDto> vehicleDtos;
}
