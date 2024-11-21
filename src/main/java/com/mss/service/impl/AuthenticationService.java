package com.mss.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mss.dto.AuthenticationRequestDto;
import com.mss.dto.AuthenticationResponseDto;
import com.mss.dto.RegisterRequestDto;
import com.mss.dto.VerifyUserDto;
import com.mss.enumeration.TokenType;
import com.mss.model.Token;
import com.mss.model.User;
import com.mss.repository.TokenRepository;
import com.mss.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Service class for handling authentication-related operations.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    /**
     * The repository used to retrieve user data.
     */
    private final UserRepository repository;

    /**
     * The repository used to retrieve token data.
     */
    private final TokenRepository tokenRepository;

    /**
     * Service interface for encoding passwords. The preferred implementation is BCryptPasswordEncoder.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * The Service used to retrieve token data.
     */
    private final com.mss.service.impl.JwtService jwtService;

    /**
     * Processes an Authentication request.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * The Service used to send email.
     */
    private final EmailServiceImpl emailService;

    /**
     * Registers a new user.
     *
     * @param request the registration request data
     * @return the authentication response containing the access token and refresh token
     */
    public AuthenticationResponseDto register(RegisterRequestDto request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .imageUrl(request.getImageUrl())
                .mobileNumber(request.getMobileNumber())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpiration(LocalDateTime.now().plusHours(3));
        user.setEnabled(false);
        sendVerificationEmail(user);
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponseDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Authenticates a user.
     *
     * @param request the authentication request data
     * @return the authentication response containing the access token and refresh token
     */
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, " Account is not verified");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponseDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Saves a user token.
     *
     * @param user     the user associated with the token
     * @param jwtToken the JWT token to be saved
     */
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    /**
     * Revokes all tokens associated with a user.
     *
     * @param user the user whose tokens should be revoked
     */
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    /**
     * Refreshes the access token.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws IOException if an I/O error occurs
     */
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String refreshHeader = request.getHeader("Refresh");
        final String refreshToken;
        final String userEmail;
        if (refreshHeader == null || !refreshHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = refreshHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponseDto.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public AuthenticationResponseDto verifyUser(VerifyUserDto verifyUserDto) {
        User user = repository.findByEmail(verifyUserDto.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " User doesn't exist"));

        if (user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, " User is already verified");
        }

        if (user.getVerificationExpiration().isBefore(LocalDateTime.now())) {
            if (user.getVerificationExpiration().isBefore(LocalDateTime.now())) {
                throw new ResponseStatusException(HttpStatus.GONE, " Verification code has expired");
            }
        }

        if (user.getVerificationCode().equals(verifyUserDto.getVerificationCode())) {
            user.setEnabled(true);
            user.setVerificationCode(null);
            user.setVerificationExpiration(null);
            repository.save(user);

            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            return AuthenticationResponseDto.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } else {
            throw new RuntimeException("Invalid verification code");
        }
    }

    public void resendVerificationCode(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, " User doesn't exist"));

        if (user.isEnabled()) {
            throw new RuntimeException("User is already verified");
        }

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationExpiration(LocalDateTime.now().plusHours(3));
        sendVerificationEmail(user);
        repository.save(user);
    }

    public void sendVerificationEmail(User user) {
        String subject = "MSS Account Verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>User Verification</title>
                  <style>
                    body {
                      font-family: Arial, sans-serif;
                      margin: 0;
                      padding: 0;
                      background-color: #f4f4f9;
                    }
                    .email-container {
                      max-width: 600px;
                      margin: 20px auto;
                      background: #ffffff;
                      border: 1px solid #dddddd;
                      border-radius: 8px;
                      overflow: hidden;
                      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    }
                    .email-header {
                      background-color: #2c2b29;
                      padding: 20px;
                      text-align: center;
                    }
                    .email-header img {
                      max-width: 150px;
                    }
                    .email-body {
                      padding: 20px;
                    }
                    .email-body h1 {
                      color: #333333;
                      font-size: 24px;
                    }
                    .email-body p {
                      color: #555555;
                      font-size: 16px;
                      line-height: 1.5;
                    }
                    .verification-code {
                      display: flex;
                      justify-content: center;
                      align-items: center;
                      margin: 20px 0;
                      padding: 20px;
                      background-color: #e9ecef;
                      border: 1px dashed #6c757d;
                      font-size: 18px;
                      font-weight: bold;
                      color: #004085;
                      text-align: center;
                    }
                    .email-footer {
                      padding: 20px;
                      text-align: center;
                      background-color: #f8f9fa;
                      color: #6c757d;
                      font-size: 14px;
                    }
                    .email-footer a {
                      color: #007bff;
                      text-decoration: none;
                    }
                    .email-footer a:hover {
                      text-decoration: underline;
                    }
                  </style>
                </head>
                <body>
                  <div class="email-container">
                    <div class="email-header">
                      <img src="https://i.imghippo.com/files/pQi9349bTk.png" alt="Logo">
                    </div>
                    <div class="email-body">
                      <h1>Verify Your Email Address</h1>
                      <p>Thank you for signing up! Please use the verification code below to complete your registration:</p>
                      <div class="verification-code">
                """ + verificationCode + """
                      </div>
                      <p>If you did not sign up for this account, please ignore this email or contact support if you have questions.</p>
                    </div>
                    <div class="email-footer">
                      <p>Need help? Contact us at <a href="mailto:support@mss.com">support@mss.com</a></p>
                      <p>&copy; 2024 MSS. All rights reserved.</p>
                    </div>
                  </div>
                </body>
                </html>
                """;

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
