package com.example.demo.repository;

import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
    // Explicit fetch by Order ID to avoid lazy-loading surprises
    List<OrderDetail> findByOrder_OrderID(Integer orderID);
}
