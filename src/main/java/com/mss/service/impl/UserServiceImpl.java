package com.mss.service.impl;

import com.mss.model.User;
import com.mss.repository.UserRepository;
import com.mss.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementation of the User interface.
 * <p>
 * Provides methods to manage user-related operations.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    /**
     * The repository used to retrieve user data.
     */
    private final UserRepository userRepository;

    /**
     * Retrieves a user entity by their email address.
     *
     * @param email The email address of the user.
     * @return The User entity with the specified email address.
     * @throws ResponseStatusException If a user with the specified email address is not found.
     */
    @Override
    public User findOneByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with that email doesn't exist"));
    }

    /**
     * Retrieves the username of the currently authenticated user from the Spring Security context.
     *
     * @return The username of the currently authenticated user.
     * @throws RuntimeException If the authentication object does not contain user details.
     */
    @Override
    public User getUserFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            return findOneByEmail(email);
        } else {
            throw new RuntimeException("Authentication object does not contain user details");
        }
    }
}
