package com.example.demo.dto.response;

import lombok.*;

@Data
@Builder @AllArgsConstructor
public class TopSellingResponse {
    String productName;
    Long totalSold;
}
