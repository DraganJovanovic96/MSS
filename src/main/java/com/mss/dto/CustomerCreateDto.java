package com.mss.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * A Data Transfer Object (DTO) for saving customer data.
 * <p>
 * This DTO does not include vehicles to keep the customer creation process focused.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class CustomerCreateDto {
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
    @Size(max = 320)
    private String email;

    /**
     * The customer's phone number.
     */
    @NotNull
    private String phoneNumber;
}
