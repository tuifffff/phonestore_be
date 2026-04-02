package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.CloudinaryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadController {

    CloudinaryService cloudinaryService;

    @PostMapping("/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.uploadFile(file);
        return ApiResponse.<String>builder()
                .result(url)
                .message("Tải ảnh lên thành công!")
                .build();
    }
}
