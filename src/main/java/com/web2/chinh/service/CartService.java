package com.web2.chinh.service;

import com.web2.chinh.dto.CartItemResponse;
import com.web2.chinh.dto.CartResponse;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    /** Lấy giỏ hàng của user (tự tạo nếu chưa có). */
    CartResponse getCart(Long userId);

    /** Lấy số lượng sản phẩm trong giỏ của user. */
    int getItemCount(Long userId);

    /** Lấy tổng tiền trong giỏ của user. */
    BigDecimal getTotalAmount(Long userId);

    /** Thêm sản phẩm vào giỏ. */
    CartItemResponse addToCart(Long userId, Long productId, Integer quantity);

    /** Cập nhật số lượng 1 sản phẩm (0 = xóa). */
    CartItemResponse updateQuantity(Long userId, Long productId, Integer quantity);

    /** Tăng số lượng 1 đơn vị. */
    CartItemResponse increase(Long userId, Long productId);

    /** Giảm số lượng 1 đơn vị (về 0 sẽ tự xóa). */
    CartItemResponse decrease(Long userId, Long productId);

    /** Xóa 1 sản phẩm khỏi giỏ. */
    void removeItem(Long userId, Long productId);

    /** Xóa toàn bộ giỏ hàng. */
    void clearCart(Long userId);

    /** Gộp giỏ hàng từ session vào user (sau khi đăng nhập). */
    CartResponse mergeSessionCart(Long userId, List<com.web2.chinh.dto.AddToCartRequest> sessionItems);
}
