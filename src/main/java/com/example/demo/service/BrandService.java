package com.example.demo.service;

import com.example.demo.dto.request.BrandRequest;
import com.example.demo.dto.response.BrandResponse;
import com.example.demo.entity.Brand;
import com.example.demo.mapper.BrandMapper;
import com.example.demo.repository.BrandRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandService {
    BrandRepository brandRepository;
    BrandMapper brandMapper;

    public BrandResponse create(BrandRequest request) {
        Brand brand = brandMapper.toEntity(request);
        return brandMapper.toResponse(brandRepository.save(brand));
    }

    public List<BrandResponse> getAll() {
        return brandRepository.findAll().stream()
                .map(brandMapper::toResponse)
                .toList();
    }
    @Transactional
    public void delete(Integer id) {
        // 1. Kiểm tra tồn tại
        if (!brandRepository.existsById(id)) {
            throw new RuntimeException("Hãng không tồn tại để xóa!");
        }

        try {
            // 2. Thực hiện xóa
            brandRepository.deleteById(id);
        } catch (Exception e) {
            // 3. Nếu lỗi (do dính khóa ngoại với bảng Product), báo cho Admin biết
            throw new RuntimeException("Không thể xóa hãng này vì vẫn còn sản phẩm thuộc hãng này trong kho!");
        }
    }
}
