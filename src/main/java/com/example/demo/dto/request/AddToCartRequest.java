package com.example.demo.dto.request;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Integer versionID;
    private Integer quantity;
}
