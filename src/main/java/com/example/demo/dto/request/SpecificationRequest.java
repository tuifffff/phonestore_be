package com.example.demo.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SpecificationRequest {
    String screenSize;
    String screenTech;
    String rearCamera;
    String frontCamera;
    String chipset;
    String ram;
    String rom;
    String battery;
    String os;
    String screenFeatures;
}