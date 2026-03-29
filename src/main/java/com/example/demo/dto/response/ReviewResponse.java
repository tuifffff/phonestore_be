package com.example.demo.dto.response;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Set;
@Data @Builder @FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewResponse {
    Integer id;
    String username; // Để hiển thị tên người đánh giá
    Integer rating;
    String comment;
    LocalDateTime createdAt;
}
