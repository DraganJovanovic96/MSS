package com.mss.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service for uploading damage report photos to Supabase Storage.
 *
 * @author Dragan Jovanovic
 * @version 1.0
 * @since 1.0
 */
public interface DamageReportPhotoService {

    /**
     * Upload damage report photos to Supabase Storage.
     *
     * @param photos the list of photo files to upload
     * @param reportId the customer report ID
     * @return list of public URLs for the uploaded photos
     */
    List<String> uploadDamageReportPhotos(List<MultipartFile> photos, Long reportId);
}
