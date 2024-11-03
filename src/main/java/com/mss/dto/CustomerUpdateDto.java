package com.mss.dto;

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
     * The customer's phone number.
     */
    private String phoneNumber;
}
