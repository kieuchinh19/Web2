package com.web2.chinh.dto;

import com.web2.chinh.entity.Order;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String orderCode;
    private Long userId;
    private String username;
    private String fullName;
    private String phone;
    private String email;
    private String shippingAddress;
    private String note;
    private BigDecimal totalAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private Order.OrderStatus status;
    private Order.PaymentMethod paymentMethod;
    private Order.PaymentStatus paymentStatus;
    private LocalDateTime orderDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;

    public static OrderResponse fromEntity(Order o) {
        return OrderResponse.builder()
                .id(o.getId())
                .orderCode(o.getOrderCode())
                .userId(o.getUser() != null ? o.getUser().getId() : null)
                .username(o.getUser() != null ? o.getUser().getUsername() : null)
                .fullName(o.getFullName())
                .phone(o.getPhone())
                .email(o.getEmail())
                .shippingAddress(o.getShippingAddress())
                .note(o.getNote())
                .totalAmount(o.getTotalAmount())
                .shippingFee(o.getShippingFee())
                .discountAmount(o.getDiscountAmount())
                .status(o.getStatus())
                .paymentMethod(o.getPaymentMethod())
                .paymentStatus(o.getPaymentStatus())
                .orderDate(o.getOrderDate())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .items(o.getItems() != null
                        ? o.getItems().stream().map(OrderItemResponse::fromEntity).collect(Collectors.toList())
                        : null)
                .build();
    }
}
