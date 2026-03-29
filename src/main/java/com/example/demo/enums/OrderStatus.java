package com.example.demo.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Chờ duyệt"),
    PAID("Đã thanh toán"),
    REJECTED("Đã từ chối"),
    SHIPPING("Đang giao hàng"),
    DELIVERED("Đã giao hàng"),
    CANCELLED("Đã hủy");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}