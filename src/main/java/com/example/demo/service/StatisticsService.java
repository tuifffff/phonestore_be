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