package com.web2.chinh.dto;

import com.web2.chinh.entity.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    @NotNull(message = "ID người dùng không được để trống")
    private Long userId;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String shippingAddress;

    @Size(max = 500)
    private String note;

    private Order.PaymentMethod paymentMethod;

    private BigDecimal shippingFee;

    private BigDecimal discountAmount;

    @NotEmpty(message = "Đơn hàng phải có ít nhất 1 sản phẩm")
    @Valid
    private List<OrderItemRequest> items;
}
