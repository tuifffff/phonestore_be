package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class OrderCreateRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    private String receiverName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String shippingAddress;

    private String note; // Ghi chú (không bắt buộc nên không cần @NotBlank)

    private List<Integer> versionIds; // Danh sách ID các phiên bản sản phẩm muốn thanh toán
}