package com.mss.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity for storing OAuth2 authorization codes.
 * Authorization codes are short-lived, single-use codes generated after successful OAuth2 authentication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authorization_codes")
public class AuthorizationCode extends BaseEntity<Long> {

    /**
     * The cryptographically secure authorization code.
     */
    @Column(unique = true, nullable = false, length = 255)
    private String code;

    /**
     * The user ID associated with this authorization code.
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * The OAuth2 provider (e.g., "google").
     */
    @Column(nullable = false, length = 50)
    private String provider;

    /**
     * The access token to be returned after successful code exchange.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String accessToken;

    /**
     * The refresh token to be returned after successful code exchange.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String refreshToken;

    /**
     * Expiration time of the authorization code (short-lived, typically 1-2 minutes).
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Whether the code has been used/consumed.
     */
    @Column(nullable = false)
    private boolean used = false;

    /**
     * When the code was used.
     */
    private LocalDateTime usedAt;
}
