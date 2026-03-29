package com.example.demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary; // Tự động lấy Bean từ CloudinaryConfig của ông
    public String uploadFile(MultipartFile file) {
        try {
            // Đẩy ảnh lên mây
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("url").toString(); // Trả về link ảnh
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi up ảnh lên mây rồi Boboi ơi!");
        }
    }
}