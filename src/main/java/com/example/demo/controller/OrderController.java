package com.example.demo.controller;

import com.example.demo.dto.request.OrderCreateRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    // 1. Tiến hành chốt đơn (Checkout)
    @PostMapping("/checkout")
    public ApiResponse<OrderResponse> checkout(@Valid @RequestBody OrderCreateRequest request, Authentication authentication) {
        // Lấy tên tài khoản từ Token
        String username = authentication.getName();

        // Tiến hành chốt đơn - Lỗi sẽ do GlobalExceptionHandler lo
        OrderResponse orderResponse = orderService.placeOrder(username, request);

        return ApiResponse.<OrderResponse>builder()
                .result(orderResponse)
                .message("Đặt hàng thành công! Cảm ơn bạn đã mua sắm.")
                .build();
    }

    // 2. Xem danh sách đơn hàng của tôi
    @GetMapping("/my-orders")
    public ApiResponse<PageResponse<OrderResponse>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<PageResponse<OrderResponse>>builder()
                .result(orderService.getMyOrders(username, page, size))
                .build();
    }
    // 3. Hủy đơn hàng (Cho người dùng)
    @DeleteMapping("/{orderId}/cancel")
    public ApiResponse<String> cancelOrder(@PathVariable Integer orderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        orderService.cancelOrder(orderId, username);
        return ApiResponse.<String>builder()
                .message("Đã hủy đơn hàng thành công.")
                .build();
    }

    // 4. Admin cập nhật trạng thái đơn hàng (Duyệt đơn, Chuyển giao hàng...)
    // Ví dụ: status truyền vào là "SHIPPING", "DELIVERED", v.v.

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAuthority('APPROVE_ORDER')")
    public ApiResponse<OrderResponse> updateStatus(
            @PathVariable Integer orderId,
            @RequestParam String status) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderStatus(orderId, status))
                .build();
    }

    // 5. Chỉnh sửa thông tin nhận hàng (Chỉ khi đơn hàng còn ở trạng thái PENDING)
    @PutMapping("/{orderId}/shipping-info")
    public ApiResponse<OrderResponse> updateShippingInfo(
            @PathVariable Integer orderId,
            @Valid @RequestBody OrderCreateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateShippingInfo(orderId, username, request))
                .build();
    }
    // 6. Admin: Lấy toàn bộ đơn hàng (Đã có phân trang xịn)
    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('VIEW_ALL_ORDERS')")
    public ApiResponse<PageResponse<OrderResponse>> getAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.<PageResponse<OrderResponse>>builder()
                .result(orderService.getAllOrdersForAdmin(status, keyword, pageable))
                .build();
    }

    // 7. Xem chi tiết một đơn hàng (Dùng chung cho cả User và Admin)
    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrderDetail(@PathVariable Integer orderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Service sẽ check: Nếu là Admin thì xem thoải mái, nếu là User thì chỉ được xem đơn của mình
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderDetail(orderId, username))
                .build();
    }

    // 8. Admin: Đếm số đơn hàng đang chờ duyệt (Để hiện Badge thông báo)
    @GetMapping("/admin/count-pending")
    @PreAuthorize("hasAuthority('APPROVE_ORDER')")
    public ApiResponse<Long> countPendingOrders() {
        return ApiResponse.<Long>builder()
                .result(orderService.countOrdersByStatus("PENDING"))
                .build();
    }
    // 9. Xác nhận thanh toán (Dùng cho Sandbox/VNPay/Momo callback)
    @PostMapping("/{orderId}/confirm-payment")
    @PreAuthorize("hasAuthority('APPROVE_ORDER')")
    public ApiResponse<OrderResponse> confirmPayment(@PathVariable Integer orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.confirmPayment(orderId))
                .message("Đã xác nhận thanh toán thành công!")
                .build();
    }

    // 10. Từ chối đơn hàng (Admin reject kèm lý do)
    @PostMapping("/{orderId}/reject")
    @PreAuthorize("hasAuthority('APPROVE_ORDER')")
    public ApiResponse<String> rejectOrder(
            @PathVariable Integer orderId,
            @RequestParam String reason) {
        orderService.rejectOrder(orderId, reason);
        return ApiResponse.<String>builder()
                .message("Đã từ chối đơn hàng. Kho đã được hoàn lại.")
                .build();
    }

    // 11. Xuất hóa đơn (Trả về thông tin để FE in hoặc làm PDF)
    @GetMapping("/{orderId}/invoice")
    public ApiResponse<OrderResponse> exportInvoice(@PathVariable Integer orderId) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getInvoiceData(orderId))
                .build();
    }
}