package com.mss.service;


import com.mss.dto.LocalStorageUserDto;
import com.mss.dto.UserDto;
import com.mss.model.User;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * The UserService interface contains methods that will be implemented is UserServiceImpl and methods correlate
 * to User entity.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
public interface UserService {

    /**
     * Retrieves a user entity by their email address.
     *
     * @param email The email address of the user.
     * @return The User entity with the specified email address.
     * @throws ResponseStatusException If a user with the specified email address is not found.
     */
    User findOneByEmail(String email);

    /**
     * A method for retrieving all users implemented in UserServiceImpl class.
     *
     * @param isDeleted parameter that checks if object is soft deleted
     * @return a list of all UserDtos
     */
    List<UserDto> getAllUsers(boolean isDeleted);

    /**
     * Retrieves the user associated with the current authentication context.
     *
     * @return The User object representing the authenticated user.
     */
    User getUserFromAuthentication();

    /**
     * Retrieves the user associated with the current authentication context.
     *
     *
     * @return The LocalStorageUserDto object representing the authenticated user with fewer details.
     */
    LocalStorageUserDto getLocalStorageUserDtoFromAuthentication();

    /**
     * A method for deleting user. It is implemented in UserServiceImpl class.
     *
     * @param userId parameter that is unique to entity
     */
    void deleteUser(Long userId);
}
