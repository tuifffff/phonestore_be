package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MembershipTier tier = MembershipTier.REGULAR;

    @Column(name = "total_spent", precision = 15, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(name = "since")
    private LocalDateTime since; // Ngày đạt hạng hiện tại

    public enum MembershipTier {
        REGULAR, SILVER, GOLD, PLATINUM;

        // Ngưỡng chi tiêu để đạt hạng (VNĐ)
        public static MembershipTier fromSpent(BigDecimal totalSpent) {
            if (totalSpent.compareTo(new BigDecimal("50000000")) >= 0) return PLATINUM; // 50 triệu
            if (totalSpent.compareTo(new BigDecimal("20000000")) >= 0) return GOLD;     // 20 triệu
            if (totalSpent.compareTo(new BigDecimal("10000000")) >= 0) return SILVER;   // 10 triệu
            return REGULAR;
        }

        // Ngưỡng tiếp theo để lên hạng
        public BigDecimal nextThreshold() {
            return switch (this) {
                case REGULAR  -> new BigDecimal("10000000");
                case SILVER   -> new BigDecimal("20000000");
                case GOLD     -> new BigDecimal("50000000");
                case PLATINUM -> new BigDecimal("50000000"); // Đã max
            };
        }
    }
}
