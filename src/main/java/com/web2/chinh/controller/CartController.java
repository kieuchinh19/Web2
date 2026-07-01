package com.web2.chinh.controller;

import com.web2.chinh.dto.AddToCartRequest;
import com.web2.chinh.dto.ApiResponse;
import com.web2.chinh.dto.CartItemResponse;
import com.web2.chinh.dto.CartResponse;
import com.web2.chinh.dto.UpdateCartItemRequest;
import com.web2.chinh.entity.Product;
import com.web2.chinh.repository.ProductRepository;
import com.web2.chinh.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private static final String SESSION_USER_ID = "HEADER_USER_ID";
    private static final String SESSION_CART = "HEADER_CART";

    private final CartService cartService;
    private final ProductRepository productRepository;

    /** Lấy toàn bộ giỏ hàng. */
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return ResponseEntity.ok(ApiResponse.success(buildGuestCartResponse(session)));
        }
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(userId)));
    }

    /** Lấy tổng tiền. */
    @GetMapping("/total")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTotal(HttpSession session) {
        Long userId = currentUserId(session);
        BigDecimal total = userId == null ? BigDecimal.ZERO : cartService.getTotalAmount(userId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("total", total)));
    }

    /** Lấy số lượng sản phẩm trong giỏ. */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCount(HttpSession session) {
        Long userId = currentUserId(session);
        int count = userId == null ? countSessionCart(session) : cartService.getItemCount(userId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", count)));
    }

    /** Thêm sản phẩm vào giỏ. */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            // Guest: lưu session
            addSessionItem(session, request.getProductId(), request.getQuantity());
            return ResponseEntity.ok(ApiResponse.success(Map.of(
                    "count", countSessionCart(session),
                    "guest", true)));
        }
        CartItemResponse item = cartService.addToCart(userId, request.getProductId(), request.getQuantity());
        int count = cartService.getItemCount(userId);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "count", count,
                "item", item,
                "guest", false)));
    }

    /** Cập nhật số lượng. */
    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateQuantity(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request,
            HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            Map<String, Integer> cart = getSessionCart(session);
            if (request.getQuantity() <= 0) {
                cart.remove("id:" + productId);
            } else {
                cart.put("id:" + productId, request.getQuantity());
            }
            session.setAttribute(SESSION_CART, cart);
            return ResponseEntity.ok(ApiResponse.success(Map.of(
                    "count", countSessionCart(session), "guest", true)));
        }
        CartItemResponse item = cartService.updateQuantity(userId, productId, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "count", cartService.getItemCount(userId),
                "item", item,
                "guest", false)));
    }

    /** Tăng 1 đơn vị. */
    @PostMapping("/increase/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> increase(
            @PathVariable Long productId,
            HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            Map<String, Integer> cart = getSessionCart(session);
            cart.merge("id:" + productId, 1, Integer::sum);
            session.setAttribute(SESSION_CART, cart);
            return ResponseEntity.ok(ApiResponse.success(Map.of(
                    "count", countSessionCart(session), "guest", true)));
        }
        CartItemResponse item = cartService.increase(userId, productId);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "count", cartService.getItemCount(userId), "item", item, "guest", false)));
    }

    /** Giảm 1 đơn vị. */
    @PostMapping("/decrease/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> decrease(
            @PathVariable Long productId,
            HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            Map<String, Integer> cart = getSessionCart(session);
            String key = "id:" + productId;
            int newQty = cart.getOrDefault(key, 0) - 1;
            if (newQty <= 0) {
                cart.remove(key);
            } else {
                cart.put(key, newQty);
            }
            session.setAttribute(SESSION_CART, cart);
            return ResponseEntity.ok(ApiResponse.success(Map.of(
                    "count", countSessionCart(session), "guest", true)));
        }
        CartItemResponse item = cartService.decrease(userId, productId);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "count", cartService.getItemCount(userId), "item", item, "guest", false)));
    }

    /** Xóa 1 sản phẩm. */
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> removeItem(
            @PathVariable Long productId,
            HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            Map<String, Integer> cart = getSessionCart(session);
            cart.remove("id:" + productId);
            session.setAttribute(SESSION_CART, cart);
            return ResponseEntity.ok(ApiResponse.success(Map.of(
                    "count", countSessionCart(session), "guest", true)));
        }
        cartService.removeItem(userId, productId);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "count", cartService.getItemCount(userId), "guest", false)));
    }

    /** Xóa toàn bộ giỏ. */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            session.removeAttribute(SESSION_CART);
        } else {
            cartService.clearCart(userId);
        }
        return ResponseEntity.ok(ApiResponse.success("Đã xóa toàn bộ giỏ hàng", null));
    }

    // ===== Helpers: lấy user hiện tại =====
    private Long currentUserId(HttpSession session) {
        Object u = session.getAttribute(SESSION_USER_ID);
        return u instanceof Long id ? id : null;
    }

    // ===== Helpers: session cart cho guest =====
    @SuppressWarnings("unchecked")
    private Map<String, Integer> getSessionCart(HttpSession session) {
        Object data = session.getAttribute(SESSION_CART);
        if (data instanceof Map<?, ?> m) {
            return (Map<String, Integer>) m;
        }
        Map<String, Integer> cart = new HashMap<>();
        session.setAttribute(SESSION_CART, cart);
        return cart;
    }

    private void addSessionItem(HttpSession session, Long productId, Integer qty) {
        Map<String, Integer> cart = getSessionCart(session);
        cart.merge("id:" + productId, qty, Integer::sum);
        session.setAttribute(SESSION_CART, cart);
    }

    private CartResponse buildGuestCartResponse(HttpSession session) {
        Map<String, Integer> cart = getSessionCart(session);
        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String key = entry.getKey();
            if (key == null || !key.startsWith("id:")) {
                continue;
            }
            try {
                Long productId = Long.parseLong(key.substring(3));
                Integer qty = entry.getValue();
                Product product = productRepository.findById(productId).orElse(null);
                if (product == null) {
                    continue;
                }
                BigDecimal price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
                BigDecimal subtotal = price.multiply(BigDecimal.valueOf(qty));
                total = total.add(subtotal);
                items.add(CartItemResponse.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .productImage(product.getImage())
                        .quantity(qty)
                        .price(price)
                        .stockQuantity(product.getQuantity())
                        .subtotal(subtotal)
                        .build());
            } catch (NumberFormatException ignored) {
            }
        }

        return CartResponse.builder()
                .items(items)
                .totalItems(items.stream().mapToInt(CartItemResponse::getQuantity).sum())
                .totalAmount(total)
                .build();
    }

    private int countSessionCart(HttpSession session) {
        return getSessionCart(session).values().stream().mapToInt(Integer::intValue).sum();
    }
}
