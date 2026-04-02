package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.OrderService; // Import thêm Service này
import com.example.demo.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;
    OrderService orderService; // Thêm dòng này để gọi logic Update

    @GetMapping("/create-url")
    public ApiResponse<String> createPaymentUrl(
            @RequestParam long amount,
            @RequestParam String orderInfo, // LƯU Ý: Chỗ này từ nay ông nhớ truyền ID đơn hàng vào nhé (VD: "125")
            HttpServletRequest request) {

        String paymentUrl = paymentService.createPaymentUrl(amount, orderInfo, request);

        return ApiResponse.<String>builder()
                .result(paymentUrl)
                .message("Tạo link thanh toán VNPay thành công!")
                .build();
    }

    @GetMapping("/vnpay-return")
    public RedirectView paymentReturn(HttpServletRequest request) {
        int paymentStatus = paymentService.orderReturn(request);

        // Lấy cái orderInfo ra (Lúc này nó đang chứa ID đơn hàng do ông truyền vào lúc
        // tạo link)
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        if (paymentStatus == 1) {
            try {
                // Ép kiểu cái chuỗi orderInfo thành số Integer (ID đơn hàng)
                Integer orderId = Integer.parseInt(orderInfo);

                // GỌI LOGIC UPDATE Ở ĐÂY
                orderService.updatePaymentStatus(orderId, "PAID"); // Cập nhật thành Đã thanh toán

                return new RedirectView("http://localhost:5173/?paymentStatus=success&orderId=" + orderId + "&page=orders");
            } catch (NumberFormatException e) {
                return new RedirectView("http://localhost:5173/?paymentStatus=error&msg=Lỗi: Mã đơn hàng không hợp lệ!");
            }
        } else if (paymentStatus == 0) {
            return new RedirectView("http://localhost:5173/?paymentStatus=failed");
        } else {
            return new RedirectView("http://localhost:5173/?paymentStatus=checksum_failed");
        }
    }
}