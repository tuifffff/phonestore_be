package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "brands")
@Data
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BrandID")
    private Integer brandID;

    @Column(name = "BrandName", nullable = false)
    private String brandName;
    @Column(name = "BrandLogo") // THÊM CỘT NÀY
    private String brandLogo;
}