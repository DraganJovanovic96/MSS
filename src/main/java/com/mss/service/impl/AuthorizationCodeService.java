package com.mss.service.impl;

import com.mss.model.AuthorizationCode;
import com.mss.model.User;
import com.mss.repository.AuthorizationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Service for managing OAuth2 authorization codes.
 * Generates, validates, and consumes short-lived, single-use authorization codes.
 */
@Service
@RequiredArgsConstructor
public class AuthorizationCodeService {

    private final AuthorizationCodeRepository authorizationCodeRepository;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int CODE_LENGTH = 32;
    private static final int CODE_EXPIRATION_MINUTES = 2;

    /**
     * Generates a cryptographically secure authorization code.
     *
     * @param user the authenticated user
     * @param provider the OAuth2 provider (e.g., "google")
     * @param accessToken the JWT access token
     * @param refreshToken the JWT refresh token
     * @return the generated authorization code
     */
    @Transactional
    public String generateAuthorizationCode(User user, String provider, String accessToken, String refreshToken) {
        byte[] randomBytes = new byte[CODE_LENGTH];
        secureRandom.nextBytes(randomBytes);
        String code = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES);

        AuthorizationCode authCode = AuthorizationCode.builder()
                .code(code)
                .userId(user.getId())
                .provider(provider)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .used(false)
                .build();

        authorizationCodeRepository.save(authCode);

        cleanupExpiredCodes();

        return code;
    }

    /**
     * Validates and consumes an authorization code.
     *
     * @param code the authorization code to validate
     * @return the AuthorizationCode if valid, null otherwise
     */
    @Transactional
    public AuthorizationCode validateAndConsumeCode(String code) {
        AuthorizationCode authCode = authorizationCodeRepository.findByCode(code).orElse(null);

        if (authCode == null) {
            return null;
        }

        if (authCode.isUsed()) {
            return null;
        }

        if (authCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            return null;
        }

        authCode.setUsed(true);
        authCode.setUsedAt(LocalDateTime.now());
        authorizationCodeRepository.save(authCode);

        return authCode;
    }

    /**
     * Cleans up expired authorization codes.
     */
    @Transactional
    public void cleanupExpiredCodes() {
        authorizationCodeRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    /**
     * Cleans up used authorization codes.
     */
    @Transactional
    public void cleanupUsedCodes() {
        authorizationCodeRepository.deleteByUsedTrue();
    }
}
