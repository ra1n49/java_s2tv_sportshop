package com.s2tv.sportshop.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.s2tv.sportshop.model.Color;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folder, String resourceType) throws IOException {
        // Kiểm tra file hợp lệ
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        String contentType = file.getContentType();
        if (contentType == null || !(contentType.startsWith("image/") || contentType.startsWith("video/"))) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh hoặc video");
        }

        String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename().replaceAll("\\s+", "_");

        Map<String, Object> uploadOptions = new HashMap<>();
        uploadOptions.put("folder", folder);
        uploadOptions.put("public_id", fileName);
        uploadOptions.put("resource_type", resourceType);

        if ("image".equals(resourceType)) {
            uploadOptions.put("transformation", new Transformation()
                    .width(1000).crop("limit")
                    .quality("auto").fetchFormat("auto"));
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            throw new IOException("Lỗi khi upload file lên Cloudinary: " + e.getMessage(), e);
        }
    }

    public String uploadImageFeedback(MultipartFile file) throws IOException {
        return uploadFile(file, "feedbacks", "image");
    }

    public String uploadVideoFeedback(MultipartFile file) throws IOException {
        return uploadFile(file, "feedbacks", "video");
    }

    public String uploadAvatar(MultipartFile file) throws IOException {
        return uploadFile(file, "avatars", "image");
    }

    public boolean deleteFile(String imageUrl, String resourceType) {
        try {
            // Trích xuất public_id từ URL
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId == null) {
                return false;
            }

            Map<String, Object> params = new HashMap<>();
            params.put("resource_type", resourceType);

            Map result = cloudinary.uploader().destroy(publicId, params);
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            return false;
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
                return null;
            }

            // Dạng URL thường là: https://res.cloudinary.com/yourcloud/image/upload/v1234567890/folder/filename.jpg
            // Cần tách để lấy: folder/filename
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) return null;

            String afterUpload = parts[1];
            // Loại bỏ phiên bản v1234567890/ nếu có
            if (afterUpload.matches("v\\d+/.*")) {
                afterUpload = afterUpload.replaceFirst("v\\d+/", "");
            }

            // Loại bỏ phần extension file
            int lastDotIndex = afterUpload.lastIndexOf(".");
            if (lastDotIndex > 0) {
                afterUpload = afterUpload.substring(0, lastDotIndex);
            }

            return afterUpload;
        } catch (Exception e) {
            return null;

        }
    }
}

