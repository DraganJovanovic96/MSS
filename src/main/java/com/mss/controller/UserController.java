package com.mss.controller;

import com.mss.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
