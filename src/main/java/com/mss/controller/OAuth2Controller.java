package com.mss.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for handling OAuth2 authentication endpoints.
 */
@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    /**
     * OAuth2 failure handler.
     *
     * @return error response
     */
    @GetMapping("/failure")
    public Map<String, String> oauth2Failure() {
        return Map.of(
            "error", "OAuth2 authentication failed",
            "message", "Authentication was cancelled or failed"
        );
    }
}
