package com.mss.controller;


import com.mss.dto.*;
import com.mss.service.impl.AuthenticationService;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

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
     * Authenticates a user.
     *
     * @param request the authentication request containing user credentials
     * @return the ResponseEntity containing the authentication response
     */
    @PostMapping("/authenticate")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully logged in.", response = VehicleDto.class),
            @ApiResponse(code = 403, message = "Account is not verified.")
    })
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody AuthenticationRequestDto request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    /**
     * Refreshes the authentication token.
     *
     * @param request  the HttpServletRequest containing the refresh token
     * @param response the HttpServletResponse for setting the new token in the response header
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
     * @param verifyUserDto the data transfer object containing the verification code and user details
     * @return a {@link ResponseEntity} containing an {@link AuthenticationResponseDto} with the verification status
     * @throws ResponseStatusException if the verification code is invalid, expired, or if the user is already verified
     */
    @PostMapping("/verification")
    public ResponseEntity<AuthenticationResponseDto> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.verifyUser(verifyUserDto));
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
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
