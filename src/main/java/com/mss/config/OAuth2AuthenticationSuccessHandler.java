package com.mss.config;

import com.mss.model.User;
import com.mss.service.impl.AuthorizationCodeService;
import com.mss.service.impl.CustomOAuth2UserService;
import com.mss.service.impl.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom OAuth2 authentication success handler.
 * Generates short-lived authorization code and redirects to frontend.
 */
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtService jwtService;
    private final AuthorizationCodeService authorizationCodeService;

    @Value("${spring.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public OAuth2AuthenticationSuccessHandler(CustomOAuth2UserService customOAuth2UserService, 
                                              JwtService jwtService,
                                              AuthorizationCodeService authorizationCodeService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtService = jwtService;
        this.authorizationCodeService = authorizationCodeService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            User user = customOAuth2UserService.processOAuth2User(oAuth2User);

            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            String authCode = authorizationCodeService.generateAuthorizationCode(user, "google", accessToken, refreshToken);

            boolean needsFirstTimeSetup = !user.isFirstTimeSetupCompleted();

            String redirectUrl = frontendUrl + "/auth-callback?code=" + authCode +
                               "&firstTimeSetup=" + needsFirstTimeSetup;
            response.sendRedirect(redirectUrl);
        } catch (RuntimeException e) {
            String errorType;
            String errorMessage;

            if (e.getMessage().contains("User not found")) {
                errorType = "user_not_found";
                errorMessage = "No account found with this email. Please contact an administrator to create an account.";
            } else if (e.getMessage().contains("account")) {
                errorType = "account_error";
                errorMessage = "Account error: " + e.getMessage();
            } else {
                errorType = "authentication_failed";
                errorMessage = "Authentication failed: " + e.getMessage();
            }

            String redirectUrl = frontendUrl + "/auth-callback?error=" + errorType + "&message=" +
                                 java.net.URLEncoder.encode(errorMessage, "UTF-8");
            response.sendRedirect(redirectUrl);
        } catch (Exception e) {
            String errorMessage = "Unexpected error: " + e.getMessage();
            String redirectUrl = frontendUrl + "/auth-callback?error=unexpected_error&message=" +
                                 java.net.URLEncoder.encode(errorMessage, "UTF-8");
            response.sendRedirect(redirectUrl);
        }
    }
}
