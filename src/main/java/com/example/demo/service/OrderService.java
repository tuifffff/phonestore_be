package com.example.demo.service;

import com.example.demo.dto.request.OrderCreateRequest;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.dto.response.PageResponse;
import com.example.demo.entity.*;
import com.example.demo.enums.OrderStatus;
import com.example.demo.mapper.PageMapper;
import com.example.demo.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.mapper.OrderMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {
    EmailService emailService;
    UserRepository userRepository;
    CartRepository cartRepository;
    CartDetailsRepository cartDetailsRepository;
    OrderRepository orderRepository;
    OrderDetailRepository orderDetailRepository;
    VersionRepository versionRepository;
    OrderMapper orderMapper;
    MembershipService membershipService;

    @Transactional
    public OrderResponse placeOrder(String username, OrderCreateRequest request) {

        // 1. Tìm người dùng
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        // 2. Kiểm tra giỏ hàng
        Cart cart = cartRepository.findByUser_UserID(user.getUserID());
        if (cart == null) throw new RuntimeException("Giỏ hàng chưa có gì cả!");

        List<CartDetails> allCartItems = cartDetailsRepository.findByCart_CartID(cart.getCartID());
        if (allCartItems.isEmpty()) throw new RuntimeException("Giỏ hàng đang trống!");

        List<CartDetails> cartItems = allCartItems;
        if (request.getVersionIds() != null && !request.getVersionIds().isEmpty()) {
            cartItems = allCartItems.stream()
                    .filter(item -> request.getVersionIds().contains(item.getVersion().getVersionID()))
                    .toList();
        }

        if (cartItems.isEmpty()) throw new RuntimeException("Không có sản phẩm nào được chọn để thanh toán!");

        // 3. Tạo Hóa Đơn (Bảng Order) bằng Builder
        Order order = Order.builder()
                .user(user)
                .receiverName(request.getReceiverName())
                .phoneNumber(request.getPhoneNumber())
                .shippingAddress(request.getShippingAddress())
                .note(request.getNote())
                .createdAt(new Date())
                .status(OrderStatus.PENDING)
                .total(BigDecimal.ZERO)
                .build();

        Order savedOrder = orderRepository.save(order);
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartDetails item : cartItems) {
            Version version = item.getVersion();

            // KIỂM TRA VÀ TRỪ KHO (Đúng bảng Version của ông)
            if (version.getStock() < item.getQuantity()) {
                throw new RuntimeException("Máy màu " + version.getColour() + " không đủ hàng!");
            }

            // 3. TÍNH TOÁN TỔNG TIỀN (Sửa lỗi toán tử ở đây)
            BigDecimal itemPrice = version.getPrice();
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal subTotal = itemPrice.multiply(quantity);

            totalAmount = totalAmount.add(subTotal); // Cộng dồn vào tổng tiền
            // Tạo ID hỗn hợp (Cần @AllArgsConstructor ở OrderDetailId)
            // 3. Tạo OrderDetail

            OrderDetailId detailId = new OrderDetailId(savedOrder.getOrderID(), version.getVersionID());

            OrderDetail orderDetail = OrderDetail.builder()
                    .id(detailId)
                    .order(savedOrder)
                    .version(version)
                    .quantity(item.getQuantity())
                    .price(version.getPrice()) // Lấy giá từ Version - CHUẨN!
                    .build();
            orderDetailRepository.save(orderDetail);
        }

        // 5. Cập nhật tổng tiền
        savedOrder.setTotal(totalAmount);
        orderRepository.save(savedOrder);

        // 6. Xóa giỏ hàng
        cartDetailsRepository.deleteAll(cartItems);

        return orderMapper.toOrderResponse(orderRepository.save(savedOrder));
    }
    @Transactional
    public void cancelOrder(Integer orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        // Check quyền sở hữu
        if (!order.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này!");
        }

        // Chỉ cho hủy nếu đang PENDING
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PAID) {
            throw new RuntimeException("Đơn hàng không thể hủy ở trạng thái hiện tại!");
        }
        if (order.getStatus() == OrderStatus.PAID) {
            for (OrderDetail detail : order.getOrderDetails()) {
                Version v = detail.getVersion();
                v.setStock(v.getStock() + detail.getQuantity());
                versionRepository.save(v);
            }
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại!"));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());

            // Nếu chuyển từ trạng thái khác sang CANCELLED bởi Admin, cũng phải hoàn kho
            if ((newStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.REJECTED)
                    && order.getStatus() == OrderStatus.PAID) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    Version v = detail.getVersion();
                    v.setStock(v.getStock() + detail.getQuantity());
                    versionRepository.save(v);
                }
            }

            order.setStatus(newStatus);
            Order savedOrder = orderRepository.save(order);

            // ── VIP UPGRADE: Khi đơn DELIVERED thì cộng tiền và kiểm tra nâng hạng ──
            if (newStatus == OrderStatus.DELIVERED) {
                membershipService.addSpentAndCheckUpgrade(order.getUser(), order.getTotal());
            }

            return orderMapper.toOrderResponse(savedOrder);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái đơn hàng không hợp lệ!");
        }
    }

    @Transactional
    public OrderResponse updateShippingInfo(Integer orderId, String username, OrderCreateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại!"));

        if (!order.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền sửa đơn hàng này!");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Đơn hàng đang giao, không thể sửa thông tin!");
        }

        order.setReceiverName(request.getReceiverName());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setShippingAddress(request.getShippingAddress());
        order.setNote(request.getNote());

        return orderMapper.toOrderResponse(orderRepository.save(order));
    }
    // 1. Sửa kiểu trả về thành PageResponse và thêm tham số page, size
    public PageResponse<OrderResponse> getMyOrders(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        // Khai báo pageable ở đây để dòng 181 hết lỗi
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Order> orderPage = orderRepository.findByUser_UserID(user.getUserID(), pageable);

        // Dùng PageMapper để đóng gói
        return PageMapper.toPageResponse(orderPage, orderMapper::toOrderResponse);
    }
    // 1. Sửa kiểu trả về từ List sang PageResponse
    // Trong OrderService.java
    public PageResponse<OrderResponse> getAllOrdersForAdmin(String status, String keyword, Pageable pageable) {
        OrderStatus status1 = null;
        OrderStatus status2 = null;

        if (status != null && !status.isEmpty()) {
            status1 = OrderStatus.valueOf(status.toUpperCase());
            status2 = status1;
        }
        Page<Order> orderPage = orderRepository.findAllOrdersWithFilter(status1, status2, keyword, pageable);
        return PageMapper.toPageResponse(orderPage, orderMapper::toOrderResponse);
    }
    @Transactional
    public void rejectOrder(Integer orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không thấy đơn hàng"));

        // 1. Cập nhật trạng thái
        order.setStatus(OrderStatus.REJECTED);
        order.setNote("Lý do từ chối: " + reason);

        // 2. HOÀN KHO (Lấy từ OrderDetail ra để cộng lại)
        if (order.getStatus() == OrderStatus.PAID) {
            for (OrderDetail detail : order.getOrderDetails()) {
                Version v = detail.getVersion();
                v.setStock(v.getStock() + detail.getQuantity());
                versionRepository.save(v);
            }
        }
        order.setStatus(OrderStatus.REJECTED);
        order.setNote("Lý do từ chối: " + reason);
        orderRepository.save(order);
    }
    @Transactional
    public OrderResponse confirmPayment(Integer orderId) {
        // Reuse updatePaymentStatus which contains the stock-decrement logic for PAID
        updatePaymentStatus(orderId, "PAID");
        // return fresh mapped response
        return orderRepository.findById(orderId)
                .map(orderMapper::toOrderResponse)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại!"));
    }
    // Trong OrderService.java
    public OrderResponse getOrderDetail(Integer orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Check quyền: Chỉ Admin hoặc chính chủ đơn hàng mới được xem
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !order.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền xem đơn hàng này!");
        }

        return orderMapper.toOrderResponse(order);
    }

    // Hàm Invoice thực ra là gọi lại hàm getOrderDetail nhưng FE sẽ hiển thị kiểu khác
    public OrderResponse getInvoiceData(Integer orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toOrderResponse)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
    }
    public long countOrdersByStatus(String status) {
        if ("PENDING".equalsIgnoreCase(status)) {
            return orderRepository.countByStatus(OrderStatus.PENDING) +
                   orderRepository.countByStatus(OrderStatus.PAID);
        }
        try {
            return orderRepository.countByStatus(OrderStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return 0L;
        }
    }
    @Transactional
    public void updatePaymentStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy đơn hàng mã " + orderId));

        OrderStatus statusEnum = OrderStatus.valueOf(newStatus.toUpperCase());

        // KIỂM TRA TRÁNH TRỪ KHO 2 LẦN: Nếu đã PAID rồi thì không chạy lại logic bên dưới nữa
        if (order.getStatus() == OrderStatus.PAID) {
            log.info("Đơn hàng {} đã được thanh toán từ trước, bỏ qua logic trừ kho.", orderId);
            return;
        }

        order.setStatus(statusEnum);

        if (statusEnum == OrderStatus.PAID) {
            StringBuilder productList = new StringBuilder();

            for (OrderDetail detail : order.getOrderDetails()) {
                Version version = detail.getVersion();

                if (version.getStock() < detail.getQuantity()) {
                    throw new RuntimeException("Sản phẩm " + version.getColour() + " đã hết hàng!");
                }

                // TRỪ KHO THẬT KHI THANH TOÁN XONG
                version.setStock(version.getStock() - detail.getQuantity());
                versionRepository.save(version);

                productList.append("- ").append(version.getColour())
                        .append(" x").append(detail.getQuantity()).append("\n");
            }

            // Gửi Email xác nhận
            String subject = "Xác nhận thanh toán đơn hàng #" + order.getOrderID();
            String body = "Chào " + order.getReceiverName() + ",\n\n" +
                    "PhoneHub xác nhận đã nhận thanh toán.\n" +
                    "Danh sách máy: \n" + productList.toString() +
                    "Tổng: " + order.getTotal() + " VND\n\nCảm ơn bạn!";
            emailService.sendEmail(order.getUser().getEmail(), subject, body);
        }
        orderRepository.save(order);
    }
}
