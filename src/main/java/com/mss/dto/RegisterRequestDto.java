package com.mss.dto;


import com.mss.enumeration.Role;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) representing a register request.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {
    /**
     * The first name of the user.
     */
    private String firstname;

    /**
     * The last name of the user.
     */
    private String lastname;

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The role of the user.
     */
    private Role role ;

    /**
     * The mobile number of the user.
     */
    @NotNull
    private String mobileNumber;

    /**
     * The date of birth of the user.
     */
    private LocalDate dateOfBirth;

    /**
     * The address of the user.
     */
    private String address;

    /**
     * The URL of the user's profile image.
     */
    private String imageUrl;

    /**
     * The number of children the user has which increases his personal time off.
     */
    private int numberOfChildren;
}
