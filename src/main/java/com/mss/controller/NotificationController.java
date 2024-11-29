package com.mss.controller;

import com.mss.dto.EmailCustomerDto;
import com.mss.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * The NotificationController class is a REST controller which is responsible for handling HTTP requests related to customer email management.
 * It communicates with the email service to send emails to customers.
 * The RequiredArgsConstructor is used for fetching customerService from IoC container.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/email-customer")
@RequiredArgsConstructor
@CrossOrigin
public class NotificationController {
    /**
     * The service used to for customers.
     */
    private final NotificationService notificationService;

    /**
     * Resends the verification code to the user's email.
     *
     * @param emailCustomerDto the email address of the user to whom the verification code should be sent
     * @return a {@link ResponseEntity} containing a success message if the code was sent successfully,
     * or an error message if the operation fails
     * @throws RuntimeException if an error occurs while resending the verification code
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('admin:create')")
    @ApiOperation(value = "Send email.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Email successfully sent.")
    })
    public ResponseEntity<?> sendServiceOverEmail(@RequestBody EmailCustomerDto emailCustomerDto) {
        try {
            notificationService.sendServiceOverEmail(emailCustomerDto);
            return ResponseEntity.status(HttpStatus.OK).body("Service over, email sent");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }
    }
}
