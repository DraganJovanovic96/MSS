package com.mss.service.impl;


import com.mss.repository.TokenRepository;
import com.mss.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * Service class for handling user logout.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    /**
     * The repository used to retrieve token data.
     */
    private final TokenRepository tokenRepository;

    /**
     * Performs the logout operation by invalidating the user's token and clearing the security context.
     *
     * @param request        the HTTP request
     * @param response       the HTTP response
     * @param authentication the authentication object representing the current user's authentication
     */
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String jwt = CookieUtil.getAccessTokenFromCookies(request.getCookies());
        final String refreshToken = CookieUtil.getRefreshTokenFromCookies(request.getCookies());
        
        if (jwt != null && !jwt.isEmpty()) {
            var storedToken = tokenRepository.findByToken(jwt)
                    .orElse(null);
            if (storedToken != null) {
                storedToken.setExpired(true);
                storedToken.setRevoked(true);
                tokenRepository.save(storedToken);
            }
        }
        
        if (refreshToken != null && !refreshToken.isEmpty()) {
            var storedRefreshToken = tokenRepository.findByToken(refreshToken)
                    .orElse(null);
            if (storedRefreshToken != null) {
                storedRefreshToken.setExpired(true);
                storedRefreshToken.setRevoked(true);
                tokenRepository.save(storedRefreshToken);
            }
        }
        
        CookieUtil.clearAuthCookies(response);
        SecurityContextHolder.clearContext();
    }
}
