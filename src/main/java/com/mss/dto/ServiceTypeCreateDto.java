package com.mss.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * A Data Transfer Object (DTO) for transferring benefit data between layers of the application.
 * It extends the {@link BaseEntityDto} class.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class ServiceTypeCreateDto extends BaseEntityDto {
    /**
     * The type of service provided.
     */
    private String typeOfService;

    /**
     * The description of service provided.
     */
    private String description;

    /**
     * The price of service provided.
     */
    private double price;

    /**
     * Id of service connected to service type.
     */
    @NotNull
    private Long serviceId;
}
