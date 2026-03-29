package com.example.demo.mapper;

import com.example.demo.dto.response.OrderDetailResponse;
import com.example.demo.dto.response.OrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "details", source = "orderDetails") // Map từ List<OrderDetail> trong Entity
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "productName", source = "version.product.productName")
    @Mapping(target = "colour", source = "version.colour")
    @Mapping(target = "storage", source = "version.storage")
    @Mapping(target = "imageURL", source = "version.imageURL")
    OrderDetailResponse toDetailResponse(OrderDetail detail);

    List<OrderResponse> toOrderResponseList(List<Order> orders);
}
