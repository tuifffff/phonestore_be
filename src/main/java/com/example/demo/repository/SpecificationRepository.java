package com.example.demo.repository;

import com.example.demo.entity.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecificationRepository extends JpaRepository<ProductSpecification, Integer> {
    // JpaRepository đã có sẵn hàm .save() nên ông không cần viết thêm gì ở đây cả!
}