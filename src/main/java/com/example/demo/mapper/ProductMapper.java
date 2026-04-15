package com.example.demo.mapper;

import com.example.demo.dto.request.ProductCreateRequest;
import com.example.demo.dto.request.ProductUpdateRequest;
import com.example.demo.dto.request.SpecificationRequest;
import com.example.demo.dto.request.VersionCreateRequest;
import com.example.demo.dto.response.ProductDetailResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.dto.response.SpecificationResponse;
import com.example.demo.dto.response.VersionResponse;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductSpecification;
import com.example.demo.entity.Version;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // --- 1. Request -> Entity ---
    @Mapping(target = "productName", source = "name")
    Product toProduct(ProductCreateRequest request);
    ProductSpecification toSpecEntity(SpecificationRequest request);
    Version toVersionEntity(VersionCreateRequest request);
    @Mapping(target = "productName", source = "name")
    void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);

    // --- 2. Entity -> ProductResponse (Trang danh sách) ---
    @Mapping(target = "id", source = "productID")
    @Mapping(target = "name", source = "productName") // Map productName (Entity) -> name (DTO)
    @Mapping(target = "brandName", source = "brand.brandName")
    @Mapping(target = "categoryName", source = "category.categoryName")
    @Mapping(target = "image", source = "image")
    @Mapping(target = "minPrice", expression = "java(calculateMinPrice(product))") // Tính giá nhỏ nhất từ List<Version>
    ProductResponse toProductResponse(Product product);

    // --- 3. Entity -> ProductDetailResponse (Trang chi tiết) ---
    @Mapping(target = "id", source = "productID")
    @Mapping(target = "name", source = "productName") // SỬA TẠI ĐÂY: target phải là "name" để khớp với DTO của bạn
    @Mapping(target = "image", source = "image")
    @Mapping(target = "minPrice", expression = "java(calculateMinPrice(product))")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "brandName", source = "brand.brandName")
    @Mapping(target = "imageUrls", expression = "java(product.getGallery().stream().map(img -> img.getImageUrl()).toList())")
    @Mapping(target = "specs", source = "specification")
    // MapStruct sẽ tự động map list versions vì có hàm toVersionResponse bên dưới
    ProductDetailResponse toProductDetailResponse(Product product);

    // --- 4. Các hàm map phụ ---
    SpecificationResponse toSpecResponse(ProductSpecification specification);

    VersionResponse toVersionResponse(Version version);
    default BigDecimal calculateMinPrice(Product product) {
        if (product.getVersions() == null || product.getVersions().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return product.getVersions().stream()
                .map(Version::getPrice) // Bây giờ getPrice đã là BigDecimal rồi
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}