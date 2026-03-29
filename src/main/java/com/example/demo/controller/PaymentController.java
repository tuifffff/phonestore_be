package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Value;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.service.OrderService; // Import thêm Service này
import com.example.demo.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    @Value("${app.frontend-url}")
    private String frontendUrl;
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
    public void paymentReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int paymentStatus = paymentService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        
        // Base URL của Frontend (Cần đổi theo cổng chạy FE, mặc định Vite là 5173)


        if (paymentStatus == 1) {
            try {
                Integer orderId = Integer.parseInt(orderInfo);
                orderService.updatePaymentStatus(orderId, "PAID"); 
                // Thanh toán thành công, redirect khách về trang lịch sử đơn hàng kèm param success
                response.sendRedirect(frontendUrl + "?page=orders&paymentStatus=success&orderId=" + orderId);
            } catch (NumberFormatException e) {
                // Mã đơn hàng sai, redirect kèm lỗi
                response.sendRedirect(frontendUrl + "?page=orders&paymentStatus=error&msg=InvalidOrder");
            }
        } else if (paymentStatus == 0) {
            // Thanh toán phụt (Khách tự hủy, hết tiền, ...)
            response.sendRedirect(frontendUrl + "?page=orders&paymentStatus=failed");
        } else {
            // Cảnh báo fake dữ liệu chữ ký
            response.sendRedirect(frontendUrl + "?page=orders&paymentStatus=checksum_failed");
        }
    }
}