package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
@Data
@Builder @NoArgsConstructor @AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Integer orderID;
    BigDecimal total;
    String status;
    String receiverName;
    String phoneNumber;
    String shippingAddress;
    String note;
    java.util.Date createdAt;
    List<OrderDetailResponse> details;
}
