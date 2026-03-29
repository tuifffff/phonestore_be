package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orderdetail")
@Data
public class OrderDetail {
    @EmbeddedId
    private OrderDetailId id = new OrderDetailId();

    @ManyToOne
    @MapsId("orderID")
    @JoinColumn(name = "OrderID_FK")
    @JsonIgnore
    private Order order;

    @ManyToOne
    @MapsId("versionID")
    @JoinColumn(name = "VersionID_FK")
    private Version version;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "Price", precision = 15, scale = 2) // Tối đa 15 chữ số, 2 số sau dấu phẩy
    private BigDecimal price;

}