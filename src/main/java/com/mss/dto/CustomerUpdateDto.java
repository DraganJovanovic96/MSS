package com.mss.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerUpdateDto extends BaseEntityDto {
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
