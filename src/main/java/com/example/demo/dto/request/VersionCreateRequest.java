package com.example.demo.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VersionCreateRequest {
    String colour;
    String storage;
    String material;
    BigDecimal price;
    Integer stock;
    String imageURL;
}