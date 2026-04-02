package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer userID;

    @Column(name = "Username", nullable = false, unique = true)
    private String username;

    @Column(name = "Password", nullable = false)
    private String password;

    @Column(name = "Email", unique = true)
    private String email;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "Gender")
    private String gender;

    @Column(name = "FullName", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String fullName;

    @Column(name = "Avatar")
    private String avatar;

    @ManyToOne
    @JoinColumn(name = "role_name")
    private Role role;
    @OneToMany(mappedBy = "user")
    private java.util.List<Order> orders;
    // Thêm 2 trường này vào User.java
    private String resetToken;
    private java.time.LocalDateTime tokenExpiry;
}