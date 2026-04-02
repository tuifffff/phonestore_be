package com.example.demo.controller;

import com.example.demo.dto.request.BannerRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.BannerResponse;
import com.example.demo.service.BannerService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BannerController {

    BannerService bannerService;

    // 1. Lấy danh sách Banner dùng cho trang ngoài (Public)
    @GetMapping("/active")
    public ApiResponse<List<BannerResponse>> getActiveBanners() {
        return ApiResponse.<List<BannerResponse>>builder()
                .result(bannerService.getActiveBanners())
                .build();
    }

    // 2. Admin: Xem tất cả Banner
    @GetMapping("/all")
    // Tạm bỏ PreAuthorize để tiện test, nếu dự án dùng hasRole("ADMIN") thì bật lại
    // @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<BannerResponse>> getAllBanners() {
        return ApiResponse.<List<BannerResponse>>builder()
                .result(bannerService.getAllBanners())
                .build();
    }

    // 3. Admin: Thêm Banner mới
    @PostMapping
    public ApiResponse<BannerResponse> createBanner(@Valid @RequestBody BannerRequest request) {
        return ApiResponse.<BannerResponse>builder()
                .result(bannerService.createBanner(request))
                .message("Tạo banner thành công")
                .build();
    }

    // 4. Admin: Sửa Banner
    @PutMapping("/{id}")
    public ApiResponse<BannerResponse> updateBanner(@PathVariable Integer id, @Valid @RequestBody BannerRequest request) {
        return ApiResponse.<BannerResponse>builder()
                .result(bannerService.updateBanner(id, request))
                .message("Cập nhật banner thành công")
                .build();
    }

    // 5. Admin: Bật/Tắt hiển thị nhanh
    @PatchMapping("/{id}/toggle")
    public ApiResponse<BannerResponse> toggleBannerActive(@PathVariable Integer id) {
        return ApiResponse.<BannerResponse>builder()
                .result(bannerService.toggleBannerActive(id))
                .message("Đã thay đổi trạng thái hiển thị")
                .build();
    }

    // 6. Admin: Xóa Banner
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBanner(@PathVariable Integer id) {
        bannerService.deleteBanner(id);
        return ApiResponse.<Void>builder()
                .message("Xóa banner thành công")
                .build();
    }
}
