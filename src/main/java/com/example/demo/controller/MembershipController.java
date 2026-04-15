package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.MembershipResponse;
import com.example.demo.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    // User: Xem hạng thành viên của chính mình
    @GetMapping("/my")
    public ApiResponse<MembershipResponse> getMyMembership() {
        return ApiResponse.<MembershipResponse>builder()
                .result(membershipService.getMyMembership())
                .build();
    }

    // Admin: Xem hạng thành viên của bất kỳ user nào
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<MembershipResponse> getMembershipByUser(@PathVariable Integer userId) {
        return ApiResponse.<MembershipResponse>builder()
                .result(membershipService.getMembershipByUserId(userId))
                .build();
    }
}
