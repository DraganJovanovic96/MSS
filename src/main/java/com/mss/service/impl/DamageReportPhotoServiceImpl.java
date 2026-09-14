package com.mss.service.impl;

import com.mss.config.SupabaseProperties;
import com.mss.service.DamageReportPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for uploading damage report photos to Supabase Storage.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DamageReportPhotoServiceImpl implements DamageReportPhotoService {

    private final SupabaseProperties supabaseProperties;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    @Override
    public List<String> uploadDamageReportPhotos(List<MultipartFile> photos, Long reportId) {
        List<String> uploadedUrls = new ArrayList<>();

        if (photos == null || photos.isEmpty()) {
            return uploadedUrls;
        }

        for (MultipartFile photo : photos) {
            try {
                String photoUrl = uploadSinglePhoto(photo, reportId);
                if (photoUrl != null) {
                    uploadedUrls.add(photoUrl);
                }
            } catch (Exception e) {
                log.error("Failed to upload photo for report ID: {}", reportId, e);
            }
        }

        return uploadedUrls;
    }

    private String uploadSinglePhoto(MultipartFile photo, Long reportId) {
        try {
            byte[] photoBytes = photo.getBytes();
            if (photoBytes.length == 0) {
                log.warn("Empty photo file received for report ID: {}", reportId);
                return null;
            }

            String originalFilename = photo.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String objectPath = "damage-reports/report_" + reportId + "_" + UUID.randomUUID() + extension;

            String uploadUrl = buildUploadUrl(objectPath);

            String contentType = photo.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                contentType = "image/jpeg";
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .timeout(Duration.ofSeconds(30))
                    .header("Authorization", "Bearer " + supabaseProperties.getServiceRoleKey())
                    .header("apikey", supabaseProperties.getServiceRoleKey())
                    .header("Content-Type", contentType)
                    .header("x-upsert", "true")
                    .PUT(HttpRequest.BodyPublishers.ofByteArray(photoBytes))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return generatePublicUrl(objectPath);
            } else {
                log.warn("Failed to upload photo to Supabase. Status code: {}, Response: {}", 
                        response.statusCode(), response.body());
                return null;
            }
        } catch (IOException e) {
            log.error("IO error uploading photo for report ID: {}", reportId, e);
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Photo upload interrupted for report ID: {}", reportId);
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
}
