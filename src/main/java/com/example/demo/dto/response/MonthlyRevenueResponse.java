package com.example.demo.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
public class MonthlyRevenueResponse {
    int month;
    int year;
    BigDecimal totalRevenue;
}