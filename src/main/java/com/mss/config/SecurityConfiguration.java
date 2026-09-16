package com.mss.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.mss.enumeration.Permission.*;
import static com.mss.enumeration.Role.ADMIN;
import static com.mss.enumeration.Role.USER;
import static com.mss.enumeration.Role.RECEPTIONIST;
import static com.mss.enumeration.Role.MECHANIC;
import static org.springframework.http.HttpMethod.*;

/**
 * SecurityConfiguration is a configuration class that defines the security settings and filters for the application.
 * It enables web security, method security, and configures the security filter chain.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    /**
     * Endpoint for customer operations.
     */
    private static final String CUSTOMERS = "/api/v1/customers";

    /**
     * Endpoint for service operations.
     */
    private static final String SERVICES = "/api/v1/services";

    /**
     * Endpoint for service type operations.
     */
    private static final String SERVICE_TYPES = "/api/v1/service-types";

    /**
     * Endpoint for vehicle operations.
     */
    private static final String VEHICLES = "/api/v1/vehicles";

    /**
     * Endpoint for vehicle operations.
     */
    private static final String REVENUE = "/api/v1/revenue";

    /**
     * Endpoint for customer report operations.
     */
    private static final String CUSTOMER_REPORTS = "/api/v1/customer-reports";

    /**
     * JWT authentication filter used for authentication.
     */
    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Authentication provider for authenticating users.
     */
    private final AuthenticationProvider authenticationProvider;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Value("${spring.frontend.url}")
    private String frontendUrl;


    /**
     * Configures the security filter chain for the application.
     *
     * @param http The HttpSecurity object used to configure the security filters.
     * @return A SecurityFilterChain instance representing the configured security filter chain.
     * @throws Exception If an error occurs during the configuration process.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/ping",
                                "/error",
                                "/oauth2/**"
                        )
                        .permitAll()

                        .requestMatchers(CUSTOMERS).hasAnyRole(ADMIN.name(), USER.name(), RECEPTIONIST.name())
                        .requestMatchers(GET, CUSTOMERS).hasAnyAuthority(USER_READ.name())
                        .requestMatchers(POST, CUSTOMERS).hasAnyAuthority(USER_CREATE.name())
                        .requestMatchers(DELETE, CUSTOMERS).hasAnyAuthority(USER_DELETE.name())

                        .requestMatchers(SERVICES).hasAnyRole(ADMIN.name(), USER.name(), RECEPTIONIST.name())
                        .requestMatchers(GET, SERVICES).hasAnyAuthority(USER_READ.name())
                        .requestMatchers(POST, SERVICES).hasAnyAuthority(USER_CREATE.name())
                        .requestMatchers(DELETE, SERVICES).hasAnyAuthority(USER_DELETE.name())

                        .requestMatchers("/api/v1/register").hasAnyRole(ADMIN.name())
                        .requestMatchers(POST,"/api/v1/register").hasAnyAuthority(ADMIN_CREATE.name())

                        .requestMatchers(SERVICE_TYPES).hasAnyRole(ADMIN.name(), USER.name(), RECEPTIONIST.name())
                        .requestMatchers(GET, SERVICE_TYPES).hasAnyAuthority(USER_READ.name())
                        .requestMatchers(POST, SERVICE_TYPES).hasAnyAuthority(USER_CREATE.name())
                        .requestMatchers(DELETE, SERVICE_TYPES).hasAnyAuthority(USER_DELETE.name())
                        .requestMatchers(PUT, SERVICE_TYPES).hasAnyAuthority(USER_UPDATE.name())

                        .requestMatchers(VEHICLES).hasAnyRole(ADMIN.name(), USER.name(), RECEPTIONIST.name())
                        .requestMatchers(GET, VEHICLES).hasAnyAuthority(USER_READ.name())
                        .requestMatchers(POST, VEHICLES).hasAnyAuthority(USER_CREATE.name())
                        .requestMatchers(DELETE, VEHICLES).hasAnyAuthority(USER_DELETE.name())
                        .requestMatchers(PUT, VEHICLES).hasAnyAuthority(USER_UPDATE.name())

                        .requestMatchers(DELETE, "/api/v1/users").hasAnyAuthority(USER_DELETE.name())
                        .requestMatchers(PUT, "/api/v1/users").hasAnyAuthority(USER_UPDATE.name())

                        .requestMatchers("/api/v1/download-invoice/*").hasAnyRole(ADMIN.name(), USER.name(), RECEPTIONIST.name())
                        .requestMatchers(GET, "/api/v1/download-invoice/*").hasAnyAuthority(USER_READ.name())

                        .requestMatchers("/api/v1/dashboard").hasAnyRole(ADMIN.name(), USER.name(), RECEPTIONIST.name())
                        .requestMatchers(GET, "/api/v1/dashboard").hasAnyAuthority(USER_READ.name())

                        .requestMatchers(REVENUE).hasAnyRole(ADMIN.name(), USER.name(), RECEPTIONIST.name())
                        .requestMatchers(POST, REVENUE).hasAnyAuthority(USER_CREATE.name())

                        .requestMatchers("/api/v1/email").hasAnyRole(ADMIN.name(), USER.name(), RECEPTIONIST.name())
                        .requestMatchers(POST, "/api/v1/email").hasAnyAuthority(USER_CREATE.name())

                        .requestMatchers(CUSTOMER_REPORTS).hasAnyRole(ADMIN.name(), RECEPTIONIST.name(), USER.name(), MECHANIC.name())
                        .requestMatchers(GET, CUSTOMER_REPORTS).hasAnyAuthority(CUSTOMER_REPORT_READ.name())
                        .requestMatchers(POST, CUSTOMER_REPORTS).hasAnyAuthority(CUSTOMER_REPORT_CREATE.name())
                        .requestMatchers(PUT, CUSTOMER_REPORTS).hasAnyRole(ADMIN.name(), RECEPTIONIST.name())
                        .requestMatchers(DELETE, CUSTOMER_REPORTS).hasAnyAuthority(CUSTOMER_REPORT_DELETE.name())

                        .anyRequest()
                        .authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureUrl("/oauth2/failure")
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );

        return http.build();
    }

    /**
     * Configures CORS settings for the application.
     *
     * <p>This method defines the allowed origins, HTTP methods, and headers for CORS requests.
     * It also specifies whether credentials (such as cookies or authorization headers) are allowed.</p>
     *
     * @return a {@link CorsConfigurationSource} that provides the CORS configuration for the application.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontendUrl));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type","Refresh"));
        configuration.setExposedHeaders(List.of("X-Total-Items", "X-Total-Pages", "X-Current-Page", "Authorization", "Refresh"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
