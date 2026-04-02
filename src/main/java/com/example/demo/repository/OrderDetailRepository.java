package com.example.demo.repository;

import com.example.demo.entity.OrderDetail;
import com.example.demo.entity.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {}

