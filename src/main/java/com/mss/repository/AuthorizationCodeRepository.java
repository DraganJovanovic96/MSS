package com.mss.repository;

import com.mss.model.AuthorizationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for AuthorizationCode entity.
 */
@Repository
public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, Long> {

    /**
     * Find an authorization code by code string.
     *
     * @param code the authorization code
     * @return Optional containing the authorization code if found
     */
    Optional<AuthorizationCode> findByCode(String code);

    /**
     * Delete expired authorization codes.
     *
     * @param now the current time
     */
    void deleteByExpiresAtBefore(LocalDateTime now);

    /**
     * Delete used authorization codes.
     */
    void deleteByUsedTrue();
}
