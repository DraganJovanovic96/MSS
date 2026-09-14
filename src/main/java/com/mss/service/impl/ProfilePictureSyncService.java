package com.mss.service.impl;

import com.mss.config.SupabaseProperties;
import com.mss.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Service for syncing user profile pictures from Google to Supabase Storage.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfilePictureSyncService {

    private final UserRepository userRepository;
    private final SupabaseProperties supabaseProperties;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    /**
     * Syncs a user's profile picture from Google to Supabase Storage.
     *
     * @param googleAvatarUrl the Google avatar URL
     * @param userId the user ID
     * @return the Supabase public URL, or null if sync failed
     */
    public String syncProfilePicture(String googleAvatarUrl, Long userId) {
        if (googleAvatarUrl == null || googleAvatarUrl.isBlank()) {
            log.warn("Google avatar URL is null or blank for user ID: {}", userId);
            return null;
        }

        byte[] imageBytes = downloadImage(googleAvatarUrl);
        if (imageBytes == null || imageBytes.length == 0) {
            log.warn("Failed to download image from Google for user ID: {}", userId);
            return null;
        }

        String supabasePath = uploadToSupabase(imageBytes, userId);
        if (supabasePath == null) {
            log.warn("Failed to upload image to Supabase for user ID: {}", userId);
            return null;
        }

        String publicUrl = generatePublicUrl(supabasePath);

        updateUserProfilePicture(userId, publicUrl);

        log.info("Successfully synced profile picture for user ID: {}", userId);
        return publicUrl;
    }

    /**
     * Downloads an image from the given URL.
     *
     * @param imageUrl the image URL
     * @return the image bytes, or null if download failed
     */
    private byte[] downloadImage(String imageUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200 && response.body() != null) {
                return response.body();
            } else {
                log.warn("Failed to download image from Google. Status code: {}", response.statusCode());
                return null;
            }
        } catch (IOException e) {
            log.error("IO error downloading image", e);
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Image download interrupted for URL: {}", imageUrl);
            return null;
        }
    }

    /**
     * Uploads image bytes to Supabase Storage using Supabase REST API.
     *
     * @param imageBytes the image bytes
     * @param userId the user ID
     * @return the Supabase storage path, or null if upload failed
     */
    private String uploadToSupabase(byte[] imageBytes, Long userId) {
        try {
            String objectPath = "avatars/user_" + userId + ".jpg";
            String uploadUrl = buildUploadUrl(objectPath);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + supabaseProperties.getServiceRoleKey())
                    .header("apikey", supabaseProperties.getServiceRoleKey())
                    .header("Content-Type", "image/jpeg")
                    .header("x-upsert", "true")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(imageBytes))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return objectPath;
            } else {
                log.warn("Failed to upload image to Supabase. Status code: {}, Response: {}", 
                        response.statusCode(), response.body());
                return null;
            }
        } catch (IOException e) {
            log.error("IO error uploading image to Supabase for user ID: {}", userId, e);
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Image upload interrupted for user ID: {}", userId);
            return null;
        }
    }

    /**
     * Builds the upload URL.
     *
     * @param objectPath the object path in Supabase Storage
     * @return the complete upload URL
     */
    private String buildUploadUrl(String objectPath) {
        String baseUrl = supabaseProperties.getBucketUrl();
        String bucketName = supabaseProperties.getBucketName();
        
        return baseUrl + "/" + bucketName + "/" + objectPath;
    }

    /**
     * Generates the public URL for a Supabase storage object.
     *
     * @param objectPath the object path in Supabase Storage
     * @return the public URL
     */
    private String generatePublicUrl(String objectPath) {
        String baseUrl = supabaseProperties.getBucketUrl();
        String bucketName = supabaseProperties.getBucketName();
        
        return baseUrl + "/public/" + bucketName + "/" + objectPath;
    }

    /**
     * Updates the user's profile picture URL in the database.
     *
     * @param userId the user ID
     * @param profilePictureUrl the new profile picture URL
     */
    private void updateUserProfilePicture(Long userId, String profilePictureUrl) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setImageUrl(profilePictureUrl);
            userRepository.save(user);
        });
    }
}
