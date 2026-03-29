package com.example.demo.controller;
import com.example.demo.dto.response.ApiResponse; // Import hộp quà thần thánh

import com.example.demo.dto.response.MonthlyRevenueResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.TopSellingResponse;
import com.example.demo.service.StatisticsService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {
    private final StatisticsService statisticsService;
    // 1. API doanh thu theo tháng
    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MonthlyRevenueResponse> getRevenue(@RequestParam int month, @RequestParam int year) {
        return ApiResponse.<MonthlyRevenueResponse>builder()
                .result(statisticsService.getRevenue(month, year))
                .build();
    }
    // 2. API top-selling products
    @GetMapping("/top-selling")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<TopSellingResponse>> getTopSelling() {
        return ApiResponse.<List<TopSellingResponse>>builder()
                .result(statisticsService.getTopSelling())
                .build();
    }
}
