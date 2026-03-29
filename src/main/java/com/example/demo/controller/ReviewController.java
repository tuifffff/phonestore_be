package com.example.demo.controller;
import com.example.demo.dto.request.ReviewRequest;
import com.example.demo.dto.response.ApiResponse; // Import hộp quà thần thánh

import com.example.demo.dto.response.ReviewResponse;
import com.example.demo.service.ReviewService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    // 1. API tạo đánh giá mới
    @PostMapping
    @PreAuthorize("hasRole('USER')") // Phải đăng nhập mới được đánh giá
    public ApiResponse<ReviewResponse> create(@RequestBody ReviewRequest request) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.createReview(request))
                .build();
    }
    // 2. API lấy đánh giá theo sản phẩm
    @GetMapping("/product/{productId}")
    public ApiResponse<List<ReviewResponse>> getByProduct(@PathVariable Integer productId) {
        return ApiResponse.<List<ReviewResponse>>builder()
                .result(reviewService.getReviewsByProduct(productId))
                .build();
    }
}
