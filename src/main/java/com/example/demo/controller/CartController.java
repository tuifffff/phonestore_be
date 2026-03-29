package com.example.demo.controller;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.request.UpdateCartItemRequest;
import com.example.demo.dto.response.ApiResponse; // Import hộp quà thần thánh
import com.example.demo.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {

    CartService cartService;

    // 1. Lấy giỏ hàng của chính tôi
    @GetMapping("/my-cart")
    public ApiResponse<?> getMyCart(Authentication authentication) {
        String username = authentication.getName();
        return ApiResponse.builder()
                .result(cartService.getCartByUsername(username))
                .build();
    }

    // 2. Thêm sản phẩm vào giỏ
    @PostMapping("/add")
    public ApiResponse<String> addToCart(@RequestBody AddToCartRequest request, Authentication authentication) {
        String username = authentication.getName();
        cartService.addToCart(username, request);
        return ApiResponse.<String>builder()
                .message("Đã thêm sản phẩm vào giỏ hàng thành công!")
                .build();
    }
    // 3. Cập nhật số lượng (Nút + - trong giỏ hàng)
    @PutMapping("/update")
    public ApiResponse<String> updateCart(@RequestBody UpdateCartItemRequest request, Authentication authentication) {
        String username = authentication.getName();
        cartService.updateCartItem(username, request);

        return ApiResponse.<String>builder()
                .message("Đã cập nhật số lượng thành công!")
                .build();
    }
    // 4. Xóa sản phẩm khỏi giỏ (Nút xóa trong giỏ hàng)
    @DeleteMapping("/remove/{versionID}")
    public ApiResponse<String> removeFromCart(@PathVariable Integer versionID, Authentication authentication) {
        String username = authentication.getName();
        cartService.removeFromCart(username, versionID);

        return ApiResponse.<String>builder()
                .message("Đã xóa sản phẩm khỏi giỏ hàng!")
                .build();
    }
}