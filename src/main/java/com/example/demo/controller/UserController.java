package com.example.demo.controller;

import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.request.ResetPasswordRequest;
import com.example.demo.dto.request.UpdateMyInfoRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    // 1. Lấy thông tin mình
    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    // 2. Cập nhật thông tin
    @PutMapping("/update")
    public ApiResponse<String> update(@RequestBody UpdateMyInfoRequest request) {
        userService.updateMyInfo(request);
        return ApiResponse.<String>builder()
                .message("Cập nhật thông tin thành công!")
                .build();
    }
    // 3. Đổi mật khẩu
    @PostMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody ChangePasswordRequest request) {
        // Đã có GlobalExceptionHandler nên không cần try-catch
        userService.changePassword(request);
        return ApiResponse.<String>builder()
                .message("Đổi mật khẩu thành công!")
                .build();
    }
    // 4. Quên mật khẩu - 2 bước
    // Bước A: Gửi mã OTP về mail
    @PostMapping("/forgot-password/send-otp")
    public ApiResponse<String> sendOtp(@RequestParam String email) {
        userService.sendOtpForgotPassword(email);
        return ApiResponse.<String>builder()
                .message("Mã OTP đã được gửi về email của bạn.")
                .build();
    }

    // Bước B: Nhập mã OTP và Pass mới
    @PostMapping("/forgot-password/reset")
    public ApiResponse<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPasswordWithOtp(
                request.getEmail(),
                request.getOtp(),
                request.getNewPassword()
        );
        return ApiResponse.<String>builder()
                .message("Đặt lại mật khẩu thành công!")
                .build();
    }
    // 5. Cấp quyền
    @PutMapping("/{username}/role")
    public ApiResponse<UserResponse> updateRole(
            @PathVariable String username,
            @RequestBody String roleName) {

        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUserRole(username, roleName))
                .message("Cấp quyền" + roleName +" thành công cho" + username)
                .build();
    }
    // 6. Thu hồi quyền
    @DeleteMapping("/{username}/role")
    public ApiResponse<UserResponse> revokeRole(@PathVariable String username) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.revokeRole(username))
                .message("Đã thu hồi quyền của " + username + " thành công!")
                .build();
    }

    // 7. Tìm kiếm và lọc người dùng theo username và role
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<PageResponse<UserResponse>> searchAndFilter(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.<PageResponse<UserResponse>>builder()
                .result(userService.searchAndFilterUsers(keyword, role, pageable))
                .build();
    }
}