package com.mss.dto;

import lombok.Data;

/**
 * The VerifyUserDto class represents a data transfer object that contains query attributes for verifying user entities.
 * These attributes include the user's email address and verification code.
 * <p>
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class VerifyUserDto {
    /**
     * User's email address.
     */
    private String email;

    /**
     * User's verification code.
     */
    private String verificationCode;
}
