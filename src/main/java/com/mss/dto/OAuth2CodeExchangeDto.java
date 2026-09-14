package com.mss.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for OAuth2 code exchange request.
 */
@Data
public class OAuth2CodeExchangeDto {
    /**
     * The short-lived authorization code received from OAuth2 callback.
     */
    @NotBlank(message = "Authorization code is required")
    private String code;
}
