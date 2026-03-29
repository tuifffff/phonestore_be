package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID")
    private Integer productID;

    @Column(name = "ProductName", nullable = false)
    private String productName;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "Image")
    private String image;

    @ManyToOne
    @JoinColumn(name = "BrandID_FK")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "CategoryID_FK")
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<Version> versions;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> gallery; // Tạo thêm Entity ProductImage nhé
    // Trong class Product Entity
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private ProductSpecification specification;
}