package com.example.demo.repository;
import com.example.demo.entity.Product;
import com.example.demo.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    // Tìm tất cả đánh giá của một sản phẩm
    List<Review> findByProductProductID(Integer productId);
}
