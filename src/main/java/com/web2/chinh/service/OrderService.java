package com.web2.chinh.service;

import com.web2.chinh.dto.OrderRequest;
import com.web2.chinh.dto.OrderResponse;
import com.web2.chinh.entity.Order;

import java.util.List;

public interface OrderService {
    OrderResponse create(OrderRequest request);

    OrderResponse updateStatus(Long id, Order.OrderStatus status);

    OrderResponse updatePaymentStatus(Long id, Order.PaymentStatus status);

    void delete(Long id);

    OrderResponse getById(Long id);

    List<OrderResponse> getAll();

    List<OrderResponse> getByUserId(Long userId);

    List<OrderResponse> getByStatus(Order.OrderStatus status);
}
