package com.example.demo.controller;
import com.example.demo.dto.response.ApiResponse; // Import hộp quà thần thánh

import com.example.demo.dto.response.MonthlyRevenueResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.TopSellingResponse;
import com.example.demo.service.StatisticsService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    // 3. API doanh thu theo từng tháng trong cả 1 năm (cho biểu đồ)
    @GetMapping("/yearly-revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<Integer, BigDecimal>> getYearlyRevenue(@RequestParam int year) {
        return ApiResponse.<Map<Integer, BigDecimal>>builder()
                .result(statisticsService.getYearlyRevenue(year))
                .build();
    }

    // 4. API doanh thu từng ngày trong 1 tháng (cho biểu đồ ngày)
    @GetMapping("/daily-revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<Integer, BigDecimal>> getDailyRevenue(
            @RequestParam int month,
            @RequestParam int year) {
        return ApiResponse.<Map<Integer, BigDecimal>>builder()
                .result(statisticsService.getDailyRevenue(month, year))
                .build();
    }
}
