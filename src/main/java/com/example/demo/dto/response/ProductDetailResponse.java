package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailResponse {
    Integer id;
    String name;         // Dùng 'name' cho đồng bộ với bản List
    String image;        // Thêm ảnh đại diện duy nhất
    BigDecimal minPrice; // Giá thấp nhất
    String brandName;
    String description;
    List<String> imageUrls;
    SpecificationResponse specs;
    List<VersionResponse> versions; // BẮT BUỘC phải có để FE chọn cấu hình
}
