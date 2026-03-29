package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.*;

import java.math.BigDecimal;


@Data
@Builder @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VersionResponse {
    Integer versionID;
    String colour;
    String storage;
    String material;
    BigDecimal price;
    Integer stock;
    String imageURL;
}
