package com.example.demo.service;

import com.example.demo.dto.request.BannerRequest;
import com.example.demo.dto.response.BannerResponse;
import com.example.demo.entity.Banner;
import com.example.demo.mapper.BannerMapper;
import com.example.demo.repository.BannerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BannerService {
    BannerRepository bannerRepository;
    BannerMapper bannerMapper;

    // Public: Chỉ lấy banner đang hiển thị (mới nhất lên đầu)
    public List<BannerResponse> getActiveBanners() {
        return bannerRepository.findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(bannerMapper::toBannerResponse)
                .collect(Collectors.toList());
    }

    // Admin: Xem toàn bộ
    public List<BannerResponse> getAllBanners() {
        return bannerRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(bannerMapper::toBannerResponse)
                .collect(Collectors.toList());
    }

    // Admin: Thêm mới
    public BannerResponse createBanner(BannerRequest request) {
        Banner banner = Banner.builder()
                .imageUrl(request.getImageUrl())
                .linkUrl(request.getLinkUrl())
                .build();
        
        // Mặc định luôn active khi tạo mới, hoặc dùng giá trị từ request nếu có
        if (request.getIsActive() != null) {
            banner.setActive(request.getIsActive());
        }

        return bannerMapper.toBannerResponse(bannerRepository.save(banner));
    }

    // Admin: Cập nhật
    public BannerResponse updateBanner(Integer id, BannerRequest request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner không tồn tại"));

        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            banner.setImageUrl(request.getImageUrl());
        }
        if (request.getLinkUrl() != null) {
            banner.setLinkUrl(request.getLinkUrl());
        }
        if (request.getIsActive() != null) {
            banner.setActive(request.getIsActive());
        }

        return bannerMapper.toBannerResponse(bannerRepository.save(banner));
    }

    // Admin: Chuyển đổi trạng thái (On/Off) nhanh
    public BannerResponse toggleBannerActive(Integer id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner không tồn tại"));
        banner.setActive(!banner.isActive());
        return bannerMapper.toBannerResponse(bannerRepository.save(banner));
    }

    // Admin: Xóa cứng
    public void deleteBanner(Integer id) {
        bannerRepository.deleteById(id);
    }
}
