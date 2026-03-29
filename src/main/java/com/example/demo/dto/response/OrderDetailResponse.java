package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailResponse {
    String productName;   // Lấy từ Version -> Product
    String colour;        // Lấy từ Version
    String storage;       // Lấy từ Version
    BigDecimal price;         // Giá tại thời điểm mua
    Integer quantity;     // Số lượng mua
    String imageURL;      // Ảnh của phiên bản đó
}
