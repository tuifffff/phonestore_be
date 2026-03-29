package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "address")
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressID;

    private String street;   // Số nhà, tên đường
    private String district; // Quận/Huyện
    private String city;     // Thành phố

    @Column(name = "is_default")
    private Boolean isDefault = false; // Có phải địa chỉ mặc định không?

    @ManyToOne
    @JoinColumn(name = "UserID_FK")
    @JsonIgnore // Tránh bị lặp vô tận khi render JSON
    private User user;
}
