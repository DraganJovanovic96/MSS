package com.mss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for OAuth2 user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserInfoDto {
    private String email;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;
    private String sub; // Google user ID
}
