package com.example.demo.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VersionUpdateRequest {
    String colour;
    String storage;
    String material; // Thêm cái này cho đủ bộ trong Entity Version của ông
    BigDecimal price;
    Integer stock;   // Để Integer cho đồng bộ
    String image;
}