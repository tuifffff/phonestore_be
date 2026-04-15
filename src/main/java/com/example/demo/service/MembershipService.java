package com.example.demo.service;

import com.example.demo.dto.response.MembershipResponse;
import com.example.demo.entity.Membership;
import com.example.demo.entity.Membership.MembershipTier;
import com.example.demo.entity.User;
import com.example.demo.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final UserService userService;

    // ── Tạo membership mặc định khi user đăng ký ──
    @Transactional
    public void initMembership(User user) {
        if (membershipRepository.findByUser_UserID(user.getUserID()).isEmpty()) {
            membershipRepository.save(Membership.builder()
                    .user(user)
                    .tier(MembershipTier.REGULAR)
                    .totalSpent(BigDecimal.ZERO)
                    .since(LocalDateTime.now())
                    .build());
        }
    }

    // ── Gọi sau khi đơn hàng DELIVERED: cộng tiền và kiểm tra nâng hạng ──
    @Transactional
    public void addSpentAndCheckUpgrade(User user, BigDecimal amount) {
        Membership membership = membershipRepository.findByUser_UserID(user.getUserID())
                .orElseGet(() -> {
                    // Fallback: tự tạo nếu chưa có (cũ account)
                    return membershipRepository.save(Membership.builder()
                            .user(user)
                            .tier(MembershipTier.REGULAR)
                            .totalSpent(BigDecimal.ZERO)
                            .since(LocalDateTime.now())
                            .build());
                });

        BigDecimal newTotal = membership.getTotalSpent().add(amount);
        membership.setTotalSpent(newTotal);

        // Kiểm tra nâng hạng
        MembershipTier newTier = MembershipTier.fromSpent(newTotal);
        if (newTier != membership.getTier()) {
            membership.setTier(newTier);
            membership.setSince(LocalDateTime.now()); // reset ngày đạt hạng
        }

        membershipRepository.save(membership);
    }

    // ── API: Lấy thông tin membership của chính mình ──
    public MembershipResponse getMyMembership() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        return buildResponse(user.getUserID());
    }

    // ── Admin: Lấy membership của bất kỳ user nào ──
    public MembershipResponse getMembershipByUserId(Integer userId) {
        return buildResponse(userId);
    }

    private MembershipResponse buildResponse(Integer userId) {
        Membership m = membershipRepository.findByUser_UserID(userId)
                .orElse(Membership.builder()
                        .tier(MembershipTier.REGULAR)
                        .totalSpent(BigDecimal.ZERO)
                        .since(LocalDateTime.now())
                        .build());

        BigDecimal nextThreshold = m.getTier().nextThreshold();
        BigDecimal progressPercent;

        if (m.getTier() == MembershipTier.PLATINUM) {
            progressPercent = new BigDecimal("100");
        } else {
            // Ngưỡng của hạng hiện tại (để tính %)
            BigDecimal currentFloor = currentFloor(m.getTier());
            BigDecimal range = nextThreshold.subtract(currentFloor);
            BigDecimal progress = m.getTotalSpent().subtract(currentFloor);
            progressPercent = progress.max(BigDecimal.ZERO)
                    .divide(range, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .min(new BigDecimal("100"))
                    .setScale(1, RoundingMode.HALF_UP);
        }

        return MembershipResponse.builder()
                .tier(m.getTier())
                .totalSpent(m.getTotalSpent())
                .nextThreshold(nextThreshold)
                .progressPercent(progressPercent)
                .since(m.getSince())
                .tierLabel(tierLabel(m.getTier()))
                .tierIcon(tierIcon(m.getTier()))
                .build();
    }

    private BigDecimal currentFloor(MembershipTier tier) {
        return switch (tier) {
            case REGULAR  -> BigDecimal.ZERO;
            case SILVER   -> new BigDecimal("10000000");
            case GOLD     -> new BigDecimal("20000000");
            case PLATINUM -> new BigDecimal("50000000");
        };
    }

    private String tierLabel(MembershipTier tier) {
        return switch (tier) {
            case REGULAR  -> "Thành viên";
            case SILVER   -> "Thành viên Bạc";
            case GOLD     -> "Thành viên Vàng";
            case PLATINUM -> "Thành viên Bạch Kim";
        };
    }

    private String tierIcon(MembershipTier tier) {
        return switch (tier) {
            case REGULAR  -> "🙂";
            case SILVER   -> "🥈";
            case GOLD     -> "🥇";
            case PLATINUM -> "💎";
        };
    }
}
