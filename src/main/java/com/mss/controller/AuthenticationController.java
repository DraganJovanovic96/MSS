package com.mss.controller;


import com.mss.dto.*;
import com.mss.model.AuthorizationCode;
import com.mss.model.User;
import com.mss.repository.UserRepository;
import com.mss.service.impl.AuthenticationService;
import com.mss.service.impl.AuthorizationCodeService;
import com.mss.util.CookieUtil;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Controller class for handling authentication-related API endpoints.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    /**
     * The service used to for authentication.
     */
    private final AuthenticationService service;

    /**
     * The service used for OAuth2 authorization code management.
     */
    private final AuthorizationCodeService authorizationCodeService;

    /**
     * Repository for user data access.
     */
    private final UserRepository userRepository;

    /**
     * Authenticates a user.
     *
     * @param request the authentication request containing user credentials
     * @param response the HttpServletResponse for setting cookies
     * @return the ResponseEntity containing the authentication response
     */
    @PostMapping("/authenticate")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully logged in.", response = VehicleDto.class),
            @ApiResponse(code = 403, message = "Account is not verified.")
    })
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody AuthenticationRequestDto request,
            HttpServletResponse response
    ) {
        AuthenticationResponseDto authResponse = service.authenticate(request);

        CookieUtil.addAuthCookies(response, authResponse.getAccessToken(), authResponse.getRefreshToken());

        AuthenticationResponseDto responseWithoutTokens = AuthenticationResponseDto.builder()
                .accessToken(null)
                .refreshToken(null)
                .build();
        
        return ResponseEntity.ok(responseWithoutTokens);
    }

    /**
     * Refreshes the authentication token.
     *
     * @param request  the HttpServletRequest containing the refresh token
     * @param response the HttpServletResponse for setting the new token in cookies
     * @throws IOException if an I/O error occurs while refreshing the token
     */
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

    /**
     * Verifies a user's account using a verification code.
     *
     * @param token verification code of the user.
     * @param response the HttpServletResponse for setting cookies
     * @return a {@link ResponseEntity} containing an {@link AuthenticationResponseDto} with the verification status
     * @throws ResponseStatusException if the verification code is invalid, expired, or if the user is already verified
     */
    @PostMapping("/verification")
    public ResponseEntity<AuthenticationResponseDto> verifyUser(@RequestParam String token,
                                                                @RequestParam String email,
                                                                HttpServletResponse response) {
        AuthenticationResponseDto authResponse = service.verifyUser(token, email);

        CookieUtil.addAuthCookies(response, authResponse.getAccessToken(), authResponse.getRefreshToken());

        AuthenticationResponseDto responseWithoutTokens = AuthenticationResponseDto.builder()
                .accessToken(null)
                .refreshToken(null)
                .build();
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseWithoutTokens);
    }

    /**
     * Resends the verification code to the user's email.
     *
     * @param emailRequestDto the email address of the user to whom the verification code should be sent
     * @return a {@link ResponseEntity} containing a success message if the code was sent successfully,
     * or an error message if the operation fails
     * @throws RuntimeException if an error occurs while resending the verification code
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<?> reVerifyUser(@RequestBody EmailRequestDto emailRequestDto) {
        try {
            service.resendVerificationCode(emailRequestDto.getEmail());
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Verification code sent");
        } catch (RuntimeException | UnsupportedEncodingException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Resends the verification code to the user's email.
     *
     * @param emailRequestDto the email address of the user to whom the verification code should be sent
     * @return a {@link ResponseEntity} containing a success message if the code was sent successfully,
     * or an error message if the operation fails
     * @throws RuntimeException if an error occurs while resending the verification code
     */
    @PostMapping("/send-reset-password")
    public ResponseEntity<?> sendPasswordResetCode(@RequestBody EmailRequestDto emailRequestDto) {
        try {
            service.setPasswordResetCode(emailRequestDto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Password reset code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Handles the password reset request.
     * <p>
     * This endpoint is used when a user has forgotten their password. The user provides
     * the reset token via the query parameter and submits the new password along with its confirmation.
     *
     * @param token            The password reset token sent to the user's email.
     * @param passwordResetDto Data Transfer Object containing the new password and confirmation.
     * @param response the HttpServletResponse for setting cookies
     * @return A response entity containing the authentication response if the password reset is successful.
     * @throws ResponseStatusException if the token is invalid or the password reset fails.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<AuthenticationResponseDto> resetPassword(
            @RequestParam String token,
            @RequestParam String email,
            @RequestBody PasswordResetDto passwordResetDto,
            HttpServletResponse response) {

        AuthenticationResponseDto authResponse = service.resetPassword(token, email, passwordResetDto);

        CookieUtil.addAuthCookies(response, authResponse.getAccessToken(), authResponse.getRefreshToken());

        AuthenticationResponseDto responseWithoutTokens = AuthenticationResponseDto.builder()
                .accessToken(null)
                .refreshToken(null)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseWithoutTokens);
    }

    /**
     * Exchanges OAuth2 authorization code for JWT tokens.
     *
     * @param codeExchangeDto the authorization code exchange request
     * @param response the HttpServletResponse for setting cookies
     * @return the authentication response with success message
     */
    @PostMapping("/oauth2/exchange")
    public ResponseEntity<?> exchangeOAuth2Code(@Valid @RequestBody OAuth2CodeExchangeDto codeExchangeDto,
                                               HttpServletResponse response) {
        try {
            AuthorizationCode authCode = authorizationCodeService.validateAndConsumeCode(codeExchangeDto.getCode());

            if (authCode == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "invalid_code",
                    "message", "Invalid or expired authorization code. Please try logging in again."
                ));
            }

            User user = userRepository.findById(authCode.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            service.saveUserToken(user, authCode.getAccessToken());
            service.saveUserToken(user, authCode.getRefreshToken());

            CookieUtil.addAuthCookies(response, authCode.getAccessToken(), authCode.getRefreshToken());

            return ResponseEntity.ok(Map.of(
                "message", "Successfully authenticated via OAuth2",
                "firstTimeSetup", false
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "exchange_failed",
                "message", "OAuth2 code exchange failed: " + e.getMessage()
            ));
        }
    }

    /**
     * Logs out the user by clearing authentication cookies.
     *
     * @param response the HttpServletResponse for clearing cookies
     * @return a success message
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        CookieUtil.clearAuthCookies(response);
        return ResponseEntity.ok(Map.of("message", "Successfully logged out"));
    }
}
