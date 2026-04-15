package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Query chính: lọc theo version.price (SP hiện nếu CÓ BẤT KỲ version nào thỏa khoảng giá)
    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.versions v " +
            "WHERE (:brandId IS NULL OR p.brand.brandID = :brandId) " +
            "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
            "AND (:inStock IS NULL OR (:inStock = true AND v.stock > 0) OR (:inStock = false AND v.stock = 0))")
    Page<Product> searchProducts(
            @Param("brandId") Integer brandId,
            @Param("keyword") String keyword,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable);

    // Query sort giá tăng dần
    @Query(value = "SELECT p FROM Product p " +
            "LEFT JOIN p.versions v " +
            "WHERE (:brandId IS NULL OR p.brand.brandID = :brandId) " +
            "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
            "AND (:inStock IS NULL OR (:inStock = true AND v.stock > 0) OR (:inStock = false AND v.stock = 0)) " +
            "GROUP BY p " +
            "ORDER BY MIN(v.price) ASC",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Product p " +
            "LEFT JOIN p.versions v " +
            "WHERE (:brandId IS NULL OR p.brand.brandID = :brandId) " +
            "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
            "AND (:inStock IS NULL OR (:inStock = true AND v.stock > 0) OR (:inStock = false AND v.stock = 0))")
    Page<Product> searchProductsSortByPriceAsc(
            @Param("brandId") Integer brandId,
            @Param("keyword") String keyword,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable);

    // Query sort giá giảm dần
    @Query(value = "SELECT p FROM Product p " +
            "LEFT JOIN p.versions v " +
            "WHERE (:brandId IS NULL OR p.brand.brandID = :brandId) " +
            "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
            "AND (:inStock IS NULL OR (:inStock = true AND v.stock > 0) OR (:inStock = false AND v.stock = 0)) " +
            "GROUP BY p " +
            "ORDER BY MIN(v.price) DESC",
           countQuery = "SELECT COUNT(DISTINCT p) FROM Product p " +
            "LEFT JOIN p.versions v " +
            "WHERE (:brandId IS NULL OR p.brand.brandID = :brandId) " +
            "AND (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:minPrice IS NULL OR v.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR v.price <= :maxPrice) " +
            "AND (:inStock IS NULL OR (:inStock = true AND v.stock > 0) OR (:inStock = false AND v.stock = 0))")
    Page<Product> searchProductsSortByPriceDesc(
            @Param("brandId") Integer brandId,
            @Param("keyword") String keyword,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStock") Boolean inStock,
            Pageable pageable);
}
