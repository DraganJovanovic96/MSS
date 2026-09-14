package com.mss.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Utility class for managing HTTP cookies with security settings.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
public class CookieUtil {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60;

    private static final boolean IS_DEVELOPMENT = isDevelopmentEnvironment();
    
    private static boolean isDevelopmentEnvironment() {
        String env = System.getenv("SPRING_PROFILES_ACTIVE");
        boolean isDev = env == null || env.toLowerCase().contains("dev") || env.toLowerCase().contains("local");
        return isDev;
    }

    /**
     * Creates a secure HttpOnly cookie.
     *
     * @param name the cookie name
     * @param value the cookie value
     * @param maxAge the cookie max age in seconds
     * @return the configured Cookie object
     */
    public static Cookie createSecureCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Lax");
        cookie.setDomain("localhost");
        return cookie;
    }

    /**
     * Adds access token cookie to the response.
     *
     * @param response the HttpServletResponse
     * @param accessToken the access token value
     */
    public static void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = createSecureCookie(ACCESS_TOKEN_COOKIE_NAME, accessToken, COOKIE_MAX_AGE);
        response.addCookie(cookie);
    }

    /**
     * Adds refresh token cookie to the response.
     *
     * @param response the HttpServletResponse
     * @param refreshToken the refresh token value
     */
    public static void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = createSecureCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken, COOKIE_MAX_AGE);
        response.addCookie(cookie);
    }

    /**
     * Adds both access and refresh token cookies to the response.
     *
     * @param response the HttpServletResponse
     * @param accessToken the access token value
     * @param refreshToken the refresh token value
     */
    public static void addAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        addAccessTokenCookie(response, accessToken);
        addRefreshTokenCookie(response, refreshToken);
    }

    /**
     * Clears authentication cookies from the response.
     *
     * @param response the HttpServletResponse
     */
    public static void clearAuthCookies(HttpServletResponse response) {
        Cookie accessCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, "");
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        accessCookie.setAttribute("SameSite", "Lax");
        accessCookie.setDomain("localhost");
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        refreshCookie.setAttribute("SameSite", "Lax");
        refreshCookie.setDomain("localhost");
        response.addCookie(refreshCookie);
    }

    /**
     * Gets the access token cookie value from the request.
     *
     * @param cookies the cookies array from the request
     * @return the access token value or null if not found
     */
    public static String getAccessTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * Gets the refresh token cookie value from the request.
     *
     * @param cookies the cookies array from the request
     * @return the refresh token value or null if not found
     */
    public static String getRefreshTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) return null;
        for (Cookie cookie : cookies) {
            if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
