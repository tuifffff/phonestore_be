package com.example.demo.controller;

import com.example.demo.config.JwtUtils;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.JwtResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.service.MembershipService;
import com.example.demo.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthenticationManager authenticationManager;
    JwtUtils jwtUtils;
    UserService userService;
    MembershipService membershipService;

    @PostMapping("/login")
    public ApiResponse<JwtResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findByUsername(authentication.getName());
        String jwt = jwtUtils.generateJwtToken(user);

        // Lấy danh sách permissions từ Role
        List<String> permissions = (user.getRole() != null && user.getRole().getPermissions() != null)
                ? user.getRole().getPermissions().stream()
                      .map(p -> p.getName())
                      .collect(Collectors.toList())
                : List.of();

        JwtResponse jwtResponse = new JwtResponse(
                jwt,
                user.getUserID(),
                user.getUsername(),
                user.getRole() != null ? user.getRole().getName() : "USER",
                permissions
        );

        return ApiResponse.<JwtResponse>builder()
                .result(jwtResponse)
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody RegisterRequest request) {
        UserResponse response = userService.registerNewUser(request);
        // Tạo membership mặc định cho user mới
        User newUser = userService.findByUsername(request.getUsername());
        membershipService.initMembership(newUser);
        return ApiResponse.<UserResponse>builder()
                .result(response)
                .message("Đăng ký thành công!")
                .build();
    }
}