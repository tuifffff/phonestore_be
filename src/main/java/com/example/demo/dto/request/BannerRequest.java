package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerRequest {
    @NotBlank(message = "URL ảnh không được để trống")
    String imageUrl;
    
    String linkUrl;
    
    Boolean isActive;
}
