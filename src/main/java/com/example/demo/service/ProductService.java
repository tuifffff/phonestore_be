package com.example.demo.service;

import com.example.demo.dto.request.ProductCreateRequest;
import com.example.demo.dto.request.ProductUpdateRequest;
import com.example.demo.dto.request.VersionUpdateRequest;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.ProductDetailResponse;
import com.example.demo.dto.response.ProductResponse;
import com.example.demo.entity.*;
import com.example.demo.mapper.PageMapper;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductImageRepository productImageRepository;
    ProductRepository productRepository;
    VersionRepository versionRepository;
    ProductMapper productMapper;
    BrandRepository brandRepository;      // Tiêm thêm cái này
    CategoryRepository categoryRepository; // Tiêm thêm cái này
    SpecificationRepository specRepository;
    // 1. Lấy toàn bộ sản phẩm
    public PageResponse<ProductResponse> getAllProducts(String keyword, Integer brandId, BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock, String sortBy, Pageable pageable) {
        org.springframework.data.domain.Page<Product> productPage;

        // Nếu sort theo giá thì dùng query riêng (vì DISTINCT + ORDER BY join field không được)
        if ("priceAsc".equals(sortBy)) {
            productPage = productRepository.searchProductsSortByPriceAsc(brandId, keyword, minPrice, maxPrice, inStock, pageable);
        } else if ("priceDesc".equals(sortBy)) {
            productPage = productRepository.searchProductsSortByPriceDesc(brandId, keyword, minPrice, maxPrice, inStock, pageable);
        } else {
            productPage = productRepository.searchProducts(brandId, keyword, minPrice, maxPrice, inStock, pageable);
        }

        return PageMapper.toPageResponse(productPage, productMapper::toProductResponse);
    }
    // 2. Thêm sản phẩm mới
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        // 1. Chuyển Request -> Entity
        Product product = productMapper.toProduct(request);
        // 2. TÌM VÀ GÁN BRAND
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hãng này!"));
            product.setBrand(brand); // Gán nguyên cái Object Brand vào Product
        }
        // 3. TÌM VÀ GÁN CATEGORY
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục này!"));
            product.setCategory(category); // Gán nguyên cái Object Category vào Product
        }
        // 2. Lưu xuống DB
        Product savedProduct = productRepository.save(product);
        if (request.getSpecifications() != null) {
            ProductSpecification spec = productMapper.toSpecEntity(request.getSpecifications());
            spec.setProduct(savedProduct); // Thiết lập mối quan hệ ngược lại
            specRepository.save(spec);
        }
        // 3. Lưu danh sách các phiên bản (Gắn kết 1-N)
        if (request.getVersions() != null && !request.getVersions().isEmpty()) {
            List<Version> versions = request.getVersions().stream().map(vReq -> {
                Version v = productMapper.toVersionEntity(vReq);
                v.setProduct(savedProduct); // Gán để biết version này thuộc máy nào
                return v;
            }).toList();
            versionRepository.saveAll(versions);
        }
        // 3. Chuyển Entity đã lưu -> Response DTO và trả về
        return productMapper.toProductResponse(savedProduct);
    }
    @Transactional
    public ProductDetailResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

        // Chuyển đổi sang Response (Nếu dùng Mapper thì gọi mapper, không thì build tay)
        return productMapper.toProductDetailResponse(product);
    }
    // 3. Cập nhật thông tin chung
    @Transactional
    public ProductResponse updateProduct(Integer id, ProductUpdateRequest request) { // 1. Đổi kiểu trả về thành ProductResponse
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy máy!"));

        productMapper.updateProduct(product, request);

        // 2. Lưu xong phải map sang Response rồi mới return
        return productMapper.toProductResponse(productRepository.save(product));
    }

    // 4. Xóa sản phẩm
    @Transactional
    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy sản phẩm để xóa!");
        }
        productRepository.deleteById(id);
    }

    // 5. Cập nhật giá và kho của từng phiên bản
    @Transactional
    public Version updateVersion(Integer versionId, VersionUpdateRequest request) {
        Version version = versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Cấu hình này không tồn tại!"));

        version.setPrice(request.getPrice());
        version.setStock(request.getStock());
        version.setColour(request.getColour());
        version.setStorage(request.getStorage());
        version.setMaterial(request.getMaterial());
        version.setImageURL(request.getImage());

        return versionRepository.save(version);
    }
    @Transactional
    public List<String> uploadGallery(Integer productId, List<String> urls) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Máy không tồn tại"));

        // Biến mớ URL (từ Cloudinary) thành Entity ProductImage
        List<ProductImage> images = urls.stream().map(url ->
                ProductImage.builder()
                        .imageUrl(url)
                        .product(product)
                        .build()
        ).toList();
        productImageRepository.saveAll(images);
        return urls;
    }
}