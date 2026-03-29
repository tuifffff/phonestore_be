package com.example.demo.controller;

import com.example.demo.dto.request.BrandRequest;
import com.example.demo.dto.response.ApiResponse; // Import hộp quà thần thánh

import com.example.demo.dto.response.BrandResponse;
import com.example.demo.service.BrandService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;
    // 1. API lấy toàn bộ danh sách hãng máy
    @GetMapping
    public ApiResponse<List<BrandResponse>> getAll() {
        return ApiResponse.<List<BrandResponse>>builder()
                .result(brandService.getAll())
                .build();
    }
    // 2. API thêm mới hãng máy (chỉ Admin mới được phép truy cập)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Chỉ Admin mới được thêm hãng máy
    public ApiResponse<BrandResponse> create(@RequestBody BrandRequest request) {
        return ApiResponse.<BrandResponse>builder()
                .result(brandService.create(request))
                .build();
    }
    // 3. API "khai tử" một hãng máy (chỉ Admin mới được phép truy cập)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        brandService.delete(id);
        return ApiResponse.<Void>builder()
                .message("Đã xóa hãng thành công!")
                .build();
    }
}