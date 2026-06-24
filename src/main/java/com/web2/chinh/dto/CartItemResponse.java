package com.web2.chinh.dto;

import com.web2.chinh.entity.CartItem;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private Integer quantity;
    private BigDecimal price;
    private Integer stockQuantity; // số lượng tồn kho
    private BigDecimal subtotal; // thành tiền = price * quantity

    public static CartItemResponse fromEntity(CartItem item) {
        CartItemResponseBuilder b = CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .productImage(item.getProduct() != null ? item.getProduct().getImage() : null)
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .stockQuantity(item.getProduct() != null ? item.getProduct().getQuantity() : null)
                .subtotal(item.getPrice() != null && item.getQuantity() != null
                        ? item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                        : BigDecimal.ZERO);
        return b.build();
    }
}
