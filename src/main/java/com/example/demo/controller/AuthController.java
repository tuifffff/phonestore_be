package com.example.demo.controller;

import com.example.demo.config.JwtUtils;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.JwtResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // Tự động tạo Constructor cho các field 'final'
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // Tự thêm 'private final' cho mọi field
public class AuthController {

    AuthenticationManager authenticationManager;
    JwtUtils jwtUtils;
    UserService userService;

    @PostMapping("/login")
    public ApiResponse<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // 1. Kiểm tra tài khoản & mật khẩu
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Sinh Token

        // 3. Lấy thông tin User thật
        User user = userService.findByUsername(authentication.getName());
        String jwt = jwtUtils.generateJwtToken(user);
        // 4. Đóng gói dữ liệu trả về
        JwtResponse jwtResponse = new JwtResponse(
                jwt,
                user.getUserID().intValue(),
                user.getUsername(),
                user.getRole() != null ? user.getRole().getName() : "USER"
        );

        return ApiResponse.<JwtResponse>builder()
                .result(jwtResponse)
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody RegisterRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.registerNewUser(request))
                .message("Đăng ký thành công!")
                .build();
    }
}