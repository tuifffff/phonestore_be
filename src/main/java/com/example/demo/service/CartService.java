package com.example.demo.service;

import com.example.demo.dto.request.AddToCartRequest;
import com.example.demo.dto.request.UpdateCartItemRequest;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.response.CartResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {

    CartRepository cartRepository;
    CartDetailsRepository cartDetailsRepository;
    UserRepository userRepository;
    VersionRepository versionRepository;

    // 1. Lấy giỏ hàng bằng Username - Trả về DTO phẳng cho FE đọc được
    public List<CartResponse> getCartByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        List<CartDetails> details = cartDetailsRepository.findByCart_User_UserID(user.getUserID());

        return details.stream().map(d -> {
            Version v = d.getVersion();
            String productName = v.getProduct() != null ? v.getProduct().getProductName() : "Sản phẩm";
            Integer productId = v.getProduct() != null ? v.getProduct().getProductID() : null;

            return CartResponse.builder()
                    .id(Long.valueOf(v.getVersionID()))
                    .productId(productId)
                    .name(productName + " - " + v.getColour() + " " + v.getStorage())
                    .img(v.getImageURL())
                    .price(v.getPrice())
                    .quantity(d.getQuantity())
                    .selectedColor(v.getColour())
                    .selectedStorage(v.getStorage())
                    .build();
        }).collect(Collectors.toList());
    }

    // 2. Thêm sản phẩm vào giỏ
    @Transactional
    public void addToCart(String username, AddToCartRequest request) {
        // Tìm User
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Tìm hoặc tạo mới Giỏ hàng (Sử dụng Optional để code mượt hơn)
        // 1. Tìm hoặc tạo Giỏ hàng (Cách viết để cart là effectively final)
        Cart existingCart = cartRepository.findByUser_UserID(user.getUserID());
        if (existingCart == null) {
            existingCart = cartRepository.save(Cart.builder().user(user).build());
        }
        final Cart cart = existingCart; // 👈 Chốt hạ: Biến 'cart' này bây giờ là hằng số, dùng thoải mái trong Lambda

// 2. Tìm Version
        Version version = versionRepository.findById(request.getVersionID())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

// 3. Tạo ID hỗn hợp
        CartDetailsId id = new CartDetailsId(cart.getCartID(), request.getVersionID());

// 4. Xử lý logic CartDetails (Đoạn này giữ nguyên, nó sẽ hết báo đỏ)
        CartDetails details = cartDetailsRepository.findById(id)
                .map(existingDetails -> {
                    existingDetails.setQuantity(existingDetails.getQuantity() + request.getQuantity());
                    return existingDetails;
                })
                .orElseGet(() -> CartDetails.builder()
                        .id(id)
                        .cart(cart)
                        .version(version)
                        .quantity(request.getQuantity())
                        .build());

        cartDetailsRepository.save(details);
    }

    // 3. Cập nhật số lượng (Cho nút + -)
    @Transactional
    public void updateCartItem(String username, UpdateCartItemRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Cart cart = cartRepository.findByUser_UserID(user.getUserID());
        CartDetailsId id = new CartDetailsId(cart.getCartID(), request.getVersionID());

        CartDetails details = cartDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        details.setQuantity(request.getQuantity());
        cartDetailsRepository.save(details);
    }

    // 4. Xóa sản phẩm khỏi giỏ
    @Transactional
    public void removeFromCart(String username, Integer versionID) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Cart cart = cartRepository.findByUser_UserID(user.getUserID());
        CartDetailsId id = new CartDetailsId(cart.getCartID(), versionID);

        cartDetailsRepository.deleteById(id);
    }
}