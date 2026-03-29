package com.example.demo.service;
import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.request.ReviewRequest;
import com.example.demo.dto.request.UpdateMyInfoRequest;
import com.example.demo.dto.response.ReviewResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.*;
import com.example.demo.mapper.ReviewMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    ReviewRepository reviewRepository;
    UserService userService; // "Tiêm" UserService vào để dùng hàm getCurrentUserEntity
    ProductRepository productRepository;
    ReviewMapper reviewMapper;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        // 1. Lấy ông User đang đăng nhập
        User user = userService.getCurrentUserEntity();

        // 2. Tìm Product được đánh giá
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        // 3. Tạo Entity Review
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    public List<ReviewResponse> getReviewsByProduct(Integer productId) {
        return reviewRepository.findByProductProductID(productId).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }
}
