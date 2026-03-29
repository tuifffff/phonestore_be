package com.example.demo.controller;

import com.example.demo.dto.request.ProductCreateRequest;
import com.example.demo.dto.request.ProductUpdateRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductDetailResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.entity.Product;
import com.example.demo.service.CloudinaryService;
import com.example.demo.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;
    CloudinaryService cloudinaryService;
    // 1. API lấy toàn bộ danh sách sản phẩm với phân trang, lọc, tìm kiếm, sắp xếp
    @GetMapping
    public ApiResponse<PageResponse<ProductResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer brandId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productID") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Pageable pageable;

        // Nếu sort theo giá → dùng query riêng (không cần Spring Sort)
        if ("priceAsc".equals(sortBy) || "priceDesc".equals(sortBy)) {
            pageable = PageRequest.of(page, size); // Không gắn Sort vì query đã ORDER BY
        } else {
            Sort sort = sortDir.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            pageable = PageRequest.of(page, size, sort);
        }

        return ApiResponse.<PageResponse<ProductResponse>>builder()
                .result(productService.getAllProducts(keyword, brandId, minPrice, maxPrice, inStock, sortBy, pageable))
                .build();
    }
    // 2. API thêm mới sản phẩm (chỉ Admin mới được phép truy cập)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Chặn quyền ở mức API
    public ApiResponse<ProductResponse> create(@RequestBody ProductCreateRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(request))
                .message("Thêm sản phẩm thành công!")
                .build();
    }
    // 3. API cập nhật sản phẩm (chỉ Admin mới được phép truy cập)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductResponse> update(@PathVariable Integer id, @RequestBody ProductUpdateRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProduct(id, request))
                .message("Cập nhật sản phẩm thành công!")
                .build();
    }
    // 4. API xóa sản phẩm (chỉ Admin mới được phép truy cập)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ApiResponse.<Void>builder()
                .message("Đã xóa sản phẩm thành công!")
                .build();
    }
    // 5. API lấy chi tiết sản phẩm theo ID (cả Admin và User đều có thể truy cập)
    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> getProductById(@PathVariable Integer id) {
        return ApiResponse.<ProductDetailResponse>builder()
                .result(productService.getProductById(id))
                .build();
    }


    @PostMapping("/{id}/gallery")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<String>> uploadGallery(
            @PathVariable Integer id,
            @RequestParam("files") MultipartFile[] files) { // Nhận mảng nhiều file ảnh

        List<String> imageUrls = new ArrayList<>();

        // 1. Duyệt qua từng file ảnh để đẩy lên mây
        for (MultipartFile file : files) {
            String url = cloudinaryService.uploadFile(file);
            imageUrls.add(url);
        }

        // 2. Lưu đống link này vào bảng product_images trong DB
        productService.uploadGallery(id, imageUrls);

        return ApiResponse.<List<String>>builder()
                .result(imageUrls)
                .message("Đã thêm bộ sưu tập ảnh 360 độ cho máy!")
                .build();
    }
}