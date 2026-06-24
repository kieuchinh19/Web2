package com.web2.chinh.dto;

import com.web2.chinh.entity.Cart;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
        private Long id;
        private Long userId;
        private List<CartItemResponse> items;
        private Integer totalItems; // tổng số lượng sản phẩm
        private BigDecimal totalAmount; // tổng tiền

        public static CartResponse fromEntity(Cart cart) {
                if (cart == null) {
                        return CartResponse.builder()
                                        .items(List.of())
                                        .totalItems(0)
                                        .totalAmount(BigDecimal.ZERO)
                                        .build();
                }
                List<CartItemResponse> items = cart.getItems() == null ? List.of()
                                : cart.getItems().stream()
                                                .map(CartItemResponse::fromEntity)
                                                .collect(Collectors.toList());

                int totalItems = items.stream().mapToInt(CartItemResponse::getQuantity).sum();
                BigDecimal total = items.stream()
                                .map(CartItemResponse::getSubtotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                return CartResponse.builder()
                                .id(cart.getId())
                                .userId(cart.getUser() != null ? cart.getUser().getId() : null)
                                .items(items)
                                .totalItems(totalItems)
                                .totalAmount(total)
                                .build();
        }
}
