package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Data
public class CartDetailsId implements Serializable {
    @Column(name = "CartID_FK")
    private Integer cartID;
    @Column(name = "VersionID_FK")
    private Integer versionID;
}