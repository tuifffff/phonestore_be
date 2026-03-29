package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReviewID")
    private Integer reviewId;

    @ManyToOne
    @JoinColumn(name = "ProductID_FK", nullable = false)
    private Product product; // Liên kết với bảng Product

    @ManyToOne
    @JoinColumn(name = "UserID_FK", nullable = false)
    private User user; // Liên kết với bảng User

    @Column(name = "Rating")
    private Integer rating; // Số sao (1-5)

    @Column(name = "Comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;
}