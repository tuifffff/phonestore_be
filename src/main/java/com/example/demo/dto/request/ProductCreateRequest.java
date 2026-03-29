package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreateRequest {
    @NotBlank(message = "Tên máy không được để trống")
    String name;         // mapping vào productName
    String description;
    String image;
    Integer categoryId;
    Integer brandId;
    SpecificationRequest specifications; // Chứa Chip, RAM, Pin...
    List<VersionCreateRequest> versions; // Chứa danh sách Màu, Dung lượng, Giá...
}