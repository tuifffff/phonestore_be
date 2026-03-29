package com.example.demo.dto.request;

import lombok.Data;

@Data
public class UpdateCartItemRequest {
    private Integer versionID; // ID cái điện thoại/biến thể
    private Integer quantity;  // Số lượng mới (ví dụ đang 1 bấm thành 2)
}