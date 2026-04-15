package com.example.demo.service;

import com.example.demo.dto.response.MonthlyRevenueResponse;
import com.example.demo.dto.response.TopSellingResponse;
import com.example.demo.enums.OrderStatus;
import com.example.demo.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final OrderRepository orderRepository;

    public MonthlyRevenueResponse getRevenue(int month, int year) {
        BigDecimal total = orderRepository.calculateMonthlyRevenue(month, year, OrderStatus.DELIVERED);
        return MonthlyRevenueResponse.builder()
                .month(month)
                .year(year)
                .totalRevenue(total != null ? total : BigDecimal.ZERO)
                .build();
    }

    public Map<Integer, BigDecimal> getYearlyRevenue(int year) {
        List<Object[]> rows = orderRepository.getYearlyRevenue(year, OrderStatus.DELIVERED);
        Map<Integer, BigDecimal> result = new java.util.LinkedHashMap<>();
        // Khởi tạo 12 tháng với giá trị 0
        for (int m = 1; m <= 12; m++) result.put(m, BigDecimal.ZERO);
        for (Object[] row : rows) {
            int m = ((Number) row[0]).intValue();
            BigDecimal rev = (BigDecimal) row[1];
            result.put(m, rev != null ? rev : BigDecimal.ZERO);
        }
        return result;
    }

    public Map<Integer, BigDecimal> getDailyRevenue(int month, int year) {
        List<Object[]> rows = orderRepository.getDailyRevenue(month, year, OrderStatus.DELIVERED);
        Map<Integer, BigDecimal> result = new java.util.LinkedHashMap<>();
        // Xác định số ngày trong tháng
        int daysInMonth = java.time.YearMonth.of(year, month).lengthOfMonth();
        for (int d = 1; d <= daysInMonth; d++) result.put(d, BigDecimal.ZERO);
        for (Object[] row : rows) {
            int d = ((Number) row[0]).intValue();
            BigDecimal rev = (BigDecimal) row[1];
            result.put(d, rev != null ? rev : BigDecimal.ZERO);
        }
        return result;
    }

    public List<TopSellingResponse> getTopSelling() {
        List<Object[]> results = orderRepository.findTopSellingProducts();
        // Chuyển từ Object[] sang DTO
        return results.stream()
                .map(result -> new TopSellingResponse(
                        (String) result[0],
                        (Long) result[1]
                ))
                .toList();
    }
}