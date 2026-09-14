package com.mss.dto;

import lombok.Data;

/**
 * A Data Transfer Object (DTO) for transferring user data between layers of the application.
 * It extends the {@link BaseEntityDto} class.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
public class LocalStorageUserDto {
    /**
     * The user's first name.
     */
    private String firstname;

    /**
     * The user's last name.
     */
    private String lastname;

    /**
     * This variable stores a 'String' that contains the URL of an image file.
     * The URL can be used to retrieve the image and display it in an application or on webpage.
     */
    private String imageUrl;

    /**
     * Flag indicating if user has completed first-time setup (password change after Google OAuth).
     */
    private boolean firstTimeSetupCompleted;

    /**
     * The user's role.
     */
    private String role;

    /**
     * The number of children the user has (for extra vacation days calculation).
     */
    private int numberOfChildren;
}
