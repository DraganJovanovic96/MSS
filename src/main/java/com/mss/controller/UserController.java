package com.mss.controller;

import com.mss.dto.*;
import com.mss.mapper.UserMapper;
import com.mss.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The UserController class is a REST controller which is responsible for handling HTTP requests related to User management.
 * It communicates with the user service to perform CRUD operations on user resources.
 * The RequiredArgsConstructor is used for fetching userService from IoC container.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/users")
public class UserController {
    /**
     * The service used to for vehicles.
     */
    private final UserService userService;

    /**
     * The mapper used to for users.
     */
    private final UserMapper userMapper;

    /**
     * The endpoint accepts a DELETE request.
     *
     * @param userId the id of the User to delete
     * @return HTTP status
     */
    @DeleteMapping(value = "/{userId}")
    @PreAuthorize("hasAnyAuthority('admin:delete')")
    @ApiOperation(value = "Delete User")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "User successfully deleted."),
            @ApiResponse(code = 404, message = "User is not found."),
            @ApiResponse(code = 404, message = "User is already deleted.")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    /**
     * The endpoint accepts a GET request.
     * Retrieves the users data for authenticated user.
     *
     * @return ResponseEntity<LocalStorageUserDto> containing the users data for the authenticated user.
     */
    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get User's data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User's data successfully fetched.", response = LocalStorageUserDto.class),
            @ApiResponse(code = 404, message = "User isn't authenticated.")
    })
    public ResponseEntity<LocalStorageUserDto> getUser() {
        userService.getUserFromAuthentication();
        return ResponseEntity.ok(userService.getLocalStorageUserDtoFromAuthentication());
    }

    /**
     * The endpoint accepts a GET request.
     * Retrieves all users data which are not deleted.
     *
     * @return ResponseEntity {@link UserDto}  containing the users' data.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get all users")
    @ApiResponse(code = 200, message = "Users data successfully fetched.")
    public ResponseEntity<List<UserDto>> getVehicles() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.getAllUsers(false));
    }

    /**
     * The endpoint accepts a GET request.
     * Retrieves the users data for authenticated user.
     *
     * @return ResponseEntity<LocalStorageUserDto> containing the users data for the authenticated user.
     */
    @GetMapping(value = "/user-details", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:read', 'user:read')")
    @ApiOperation(value = "Get User's data")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User's data successfully fetched.", response = LocalStorageUserDto.class),
            @ApiResponse(code = 404, message = "User isn't authenticated.")
    })
    public ResponseEntity<UserUpdateDto> getUserInfo() {
        userService.getUserFromAuthentication();
        return ResponseEntity.ok(userService.getUserDtoFromAuthentication());
    }

    @PutMapping(value = "/user-details-update",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('admin:update', 'user:update')")
    @ApiOperation(value = "Update user through UserUpdateDto")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated user.", response = UserUpdateDto.class),
            @ApiResponse(code = 404, message = "User is not found.")
    })
    public ResponseEntity<UserUpdateDto> updateUser(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateUser(userUpdateDto));
    }
}
