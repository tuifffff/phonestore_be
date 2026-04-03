package com.example.demo.repository;

import com.example.demo.entity.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecificationRepository extends JpaRepository<ProductSpecification, Integer> {
}