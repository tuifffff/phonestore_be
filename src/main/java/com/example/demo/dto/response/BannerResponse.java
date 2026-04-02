package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerResponse {
    Integer id;
    String imageUrl;
    String linkUrl;
    boolean isActive;
    LocalDateTime createdAt;
}
