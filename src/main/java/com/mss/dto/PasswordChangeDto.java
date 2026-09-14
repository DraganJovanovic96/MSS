package com.mss.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for password change.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeDto {

    /**
     * The password of the user (optional for first-time setup).
     */
    private String password;

    /**
     * The new password of the user.
     */
    @Size(min = 6, message = "Password must be at least 6 characters long.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{6,}$", message = "Password must be at least 6 characters long and contain at least one letter and one number.")
    private String newPassword;

    /**
     * The repeat of the new password of the user.
     */
    private String repeatNewPassword;
}
