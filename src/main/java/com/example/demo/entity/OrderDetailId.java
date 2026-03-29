package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Data
public class OrderDetailId implements Serializable {
    @Column(name = "OrderID_FK")
    private Integer orderID;
    @Column(name = "VersionID_FK")
    private Integer versionID;
}