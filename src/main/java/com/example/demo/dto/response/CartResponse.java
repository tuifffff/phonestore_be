package com.example.demo.dto.response;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long id;              // ID của CartItem (dùng để xóa/update)
    private Integer productId;     // ID của sản phẩm
    private String name;           // Tên sản phẩm (để hiện lên FE)
    private String img;            // Ảnh sản phẩm (khớp với item.img ở FE)
    private BigDecimal price;      // Giá của phiên bản đã chọn
    private Integer quantity;      // Số lượng khách đặt
    private String selectedColor;  // Màu sắc khách chọn
    private String selectedStorage;// Dung lượng khách chọn
}
