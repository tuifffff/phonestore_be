package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cartdetails")
@Data
public class CartDetails {
    @EmbeddedId
    private CartDetailsId id = new CartDetailsId();

    @ManyToOne
    @MapsId("cartID")
    @JoinColumn(name = "CartID_FK")
    private Cart cart;

    @ManyToOne
    @MapsId("versionID")
    @JoinColumn(name = "VersionID_FK")
    private Version version;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;
}