package com.example.demo.controller;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.AddressService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {

    AddressService addressService;

    // 1. Lấy danh sách địa chỉ của tôi
    @GetMapping("/my")
    public ApiResponse<List<AddressResponse>> getMyAddresses(Authentication auth) {
        return ApiResponse.<List<AddressResponse>>builder()
                .result(addressService.getMyAddresses(auth.getName()))
                .build();
    }

    // 2. Thêm địa chỉ mới
    @PostMapping("/add")
    public ApiResponse<AddressResponse> addAddress(@RequestBody AddressRequest request, Authentication auth) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.createAddress(auth.getName(), request))
                .message("Thêm địa chỉ thành công!")
                .build();
    }

    // 3. Cập nhật địa chỉ
    @PutMapping("/update/{id}")
    public ApiResponse<AddressResponse> updateAddress(
            @PathVariable Integer id,
            @RequestBody AddressRequest request,
            Authentication auth) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.updateAddress(id, request, auth.getName()))
                .message("Cập nhật địa chỉ thành công!")
                .build();
    }

    // 4. Đặt địa chỉ làm mặc định
    @PatchMapping("/set-default/{id}")
    public ApiResponse<Void> setDefaultAddress(@PathVariable Integer id, Authentication auth) {
        addressService.setDefaultAddress(id, auth.getName());
        return ApiResponse.<Void>builder()
                .message("Đã đặt làm địa chỉ mặc định!")
                .build();
    }
    // 5. Xóa địa chỉ
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable Integer id, Authentication auth) {
        addressService.deleteAddress(id, auth.getName());
        return ApiResponse.<Void>builder()
                .message("Đã xóa địa chỉ thành công!")
                .build();
    }
}