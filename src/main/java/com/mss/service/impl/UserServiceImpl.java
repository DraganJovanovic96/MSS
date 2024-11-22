package com.mss.service.impl;

import com.mss.dto.LocalStorageUserDto;
import com.mss.dto.PasswordChangeDto;
import com.mss.dto.UserDto;
import com.mss.dto.UserUpdateDto;
import com.mss.enumeration.Role;
import com.mss.mapper.UserMapper;
import com.mss.model.User;
import com.mss.repository.TokenRepository;
import com.mss.repository.UserRepository;
import com.mss.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

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
     * The repository used to retrieve token data.
     */
    private final TokenRepository tokenRepository;

    /**
     * The mapper used to map user data.
     */
    private final UserMapper userMapper;

    /**
     * Service interface for encoding passwords. The preferred implementation is BCryptPasswordEncoder.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Created USER_FILTER attribute, so we can change Filter easily if needed.
     */
    private static final String USER_FILTER = "deletedUserFilter";

    /**
     * An EntityManager instance is associated with a persistence context.
     * A persistence context is a set of entity instances in which for any
     * persistent entity identity there is a unique entity instance.
     */
    private final EntityManager entityManager;

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
     * A method for retrieving all users implemented in UserServiceImpl class.
     *
     * @param isDeleted parameter that checks if object is soft deleted
     * @return a list of all UserDtos
     */
    @Override
    public List<UserDto> getAllUsers(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter(USER_FILTER);
        filter.setParameter("isDeleted", isDeleted);
        List<User> users = userRepository.findAll();
        session.disableFilter(USER_FILTER);

        return userMapper.usersToUserDtos(users);
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

    /**
     * Retrieves the username of the currently authenticated user from the Spring Security context.
     *
     * @return The username of the currently authenticated user.
     * @throws RuntimeException If the authentication object does not contain user details.
     */
    @Override
    public LocalStorageUserDto getLocalStorageUserDtoFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = findOneByEmail(email);
            return userMapper.userToLocalStorageUserDto(user);
        } else {
            throw new RuntimeException("Authentication object does not contain user details");
        }
    }

    /**
     * Retrieves the user associated with the current authentication context.
     *
     * @return The UserDto object representing the authenticated user.
     */
    @Override
    public UserUpdateDto getUserDtoFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = findOneByEmail(email);
            return userMapper.userToUserUpdateDto(user);
        } else {
            throw new RuntimeException("Authentication object does not contain user details");
        }
    }

    /**
     * Update the user associated with the current authentication context.
     *
     * @return The UserUpdateDto object representing the authenticated user with fewer details.
     */
    @Override
    public UserUpdateDto updateUser(UserUpdateDto userUpdateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = findOneByEmail(email);
            user.setUpdatedAt(Instant.now());
            user.setFirstname(userUpdateDto.getFirstname());
            user.setLastname(userUpdateDto.getLastname());
            user.setEmail(userUpdateDto.getEmail());
            user.setImageUrl(userUpdateDto.getImageUrl());
            user.setMobileNumber(userUpdateDto.getMobileNumber());
            user.setDateOfBirth(userUpdateDto.getDateOfBirth());
            user.setAddress(userUpdateDto.getAddress());
            userRepository.save(user);
            return userMapper.userToUserUpdateDto(user);
        } else {
            throw new RuntimeException("Authentication object does not contain user details");
        }
    }

    /**
     * Update the user associated with the current authentication context.
     *
     * @param passwordChangeDto
     */
    @Override
    public void changePassword(PasswordChangeDto passwordChangeDto) {
        if (!passwordChangeDto.getNewPassword().equals(passwordChangeDto.getRepeatNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New passwords don't match");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = findOneByEmail(email);

            if (!passwordEncoder.matches(passwordChangeDto.getPassword(), user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect password");
            }
            user.setPassword(passwordEncoder.encode(passwordChangeDto.getNewPassword()));
            userRepository.save(user);
        } else {
            throw new RuntimeException("Authentication object does not contain user details");
        }
    }

    /**
     * A method for performing soft delete of User entity. It is implemented in UserController class.
     *
     * @param userId parameter that is unique to entity
     */
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .map(user -> {
                    if (Boolean.TRUE.equals(user.getDeleted())) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is already deleted.");
                    }

                    if (user.getRole() == Role.ADMIN) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin cannot be deleted");
                    }

                    user.setRole(null);
                    user.setEmail("DELETED" + user.getEmail());
                    user.getTokens().forEach(token -> {
                        tokenRepository.permanentlyDeleteTokenById(token.getId());
                    });

                    userRepository.save(user);
                    userRepository.flush();

                    return user;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not found."));

        userRepository.deleteById(userId);
    }
}
