package com.s2tv.sportshop.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

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

//    public boolean deleteFile(String publicId, String resourceType) {
//        try {
//            Map result = cloudinary.uploader().destroy(publicId,
//                    ObjectUtils.asMap("resource_type", resourceType));
//            return "ok".equals(result.get("result"));
//        } catch (Exception e) {
//            return false;
//        }
//    }
    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "feedback"));
            return uploadResult.get("secure_url").toString();
        } catch (Exception e) {
            throw new RuntimeException("Upload file failed: " + e.getMessage());
        }
    }
}

