package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_specifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpecification {

    @Id
    @Column(name = "product_id")
    private Integer productId;
    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(name = "screen_size")
    private String screenSize;

    @Column(name = "screen_tech")
    private String screenTech;

    @Column(name = "rear_camera", columnDefinition = "TEXT")
    private String rearCamera;

    @Column(name = "front_camera")
    private String frontCamera;

    @Column(name = "chipset")
    private String chipset;

    @Column(name = "ram")
    private String ram;

    @Column(name = "rom")
    private String rom;

    @Column(name = "battery")
    private String battery;

    @Column(name = "os")
    private String os;

    @Column(name = "screen_features", columnDefinition = "TEXT")
    private String screenFeatures;

}