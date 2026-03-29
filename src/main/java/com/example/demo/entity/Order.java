package com.example.demo.entity;

import com.example.demo.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "`order`" )
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private Integer orderID;

    @Column(name = "Total", precision = 15, scale = 2, nullable = false)
    private java.math.BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "UserID_FK")
    private User user;

    // Các thông tin giao hàng
    @Column(name = "ReceiverName")
    private String receiverName;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "ShippingAddress", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "Note", columnDefinition = "TEXT")
    private String note;

    // Đã dùng CreatedAt theo đúng Database của ông
    @Column(name = "CreatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    // ... các trường cũ giữ nguyên ...

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        if (status == null) {
            status = OrderStatus.PENDING; // Mặc định đơn mới là chờ duyệt
        }
    }
}