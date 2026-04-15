package com.example.demo.dto.response;

import com.example.demo.entity.Membership;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipResponse {
    private Membership.MembershipTier tier;
    private BigDecimal totalSpent;
    private BigDecimal nextThreshold;
    private BigDecimal progressPercent; // % tiến độ đến hạng tiếp theo
    private LocalDateTime since;
    private String tierLabel;   // "Thành viên Vàng"
    private String tierIcon;    // "🥇"
}
