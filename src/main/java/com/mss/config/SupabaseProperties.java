package com.mss.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for Supabase Storage integration.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Data
@Validated
@ConfigurationProperties(prefix = "supabase")
public class SupabaseProperties {

    /**
     * Supabase Storage base URL for REST API operations.
     */
    @NotBlank(message = "Supabase bucket URL is required")
    private String bucketUrl;

    /**
     * Name of the Supabase Storage bucket.
     */
    @NotBlank(message = "Supabase bucket name is required")
    private String bucketName;

    /**
     * Supabase service role key for authentication.
     */
    @NotBlank(message = "Supabase service role key is required")
    private String serviceRoleKey;
}
