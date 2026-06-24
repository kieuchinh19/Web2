package com.web2.chinh.controller;

import com.web2.chinh.dto.ApiResponse;
import com.web2.chinh.dto.CartRequest;
import com.web2.chinh.dto.CategoryResponse;
import com.web2.chinh.dto.BrandResponse;
import com.web2.chinh.dto.HeaderAuthRequest;
import com.web2.chinh.dto.UserResponse;
import com.web2.chinh.entity.User;
import com.web2.chinh.repository.ProductRepository;
import com.web2.chinh.repository.UserRepository;
import com.web2.chinh.dto.AddToCartRequest;
import com.web2.chinh.service.BrandService;
import com.web2.chinh.service.CartService;
import com.web2.chinh.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/header")
@RequiredArgsConstructor
public class HeaderController {
    private static final String SESSION_USER_ID = "HEADER_USER_ID";
    private static final String SESSION_CART = "HEADER_CART";
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAll()));
    }

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getBrands() {
        return ResponseEntity.ok(ApiResponse.success(brandService.getAll()));
    }

    @GetMapping("/auth/current-user")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (userId instanceof Long id) {
            return userRepository.findById(id)
                    .map(user -> ResponseEntity.ok(ApiResponse.success(UserResponse.fromEntity(user))))
                    .orElseGet(() -> ResponseEntity.ok(ApiResponse.success((UserResponse) null)));
        }
        return ResponseEntity.ok(ApiResponse.success((UserResponse) null));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(
            @Valid @RequestBody HeaderAuthRequest request,
            HttpSession session) {
        User user = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername()))
                .orElseThrow(() -> new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không đúng"));

        if (!user.getPassword().equals(hashPassword(request.getPassword()))) {
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không đúng");
        }
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new IllegalStateException("Tài khoản đang bị khóa");
        }

        session.setAttribute(SESSION_USER_ID, user.getId());

        // Merge session cart (guest) vào user cart nếu có
        Object sessionCartData = session.getAttribute(SESSION_CART);
        if (sessionCartData instanceof Map<?, ?> rawMap && !rawMap.isEmpty()) {
            List<AddToCartRequest> toMerge = new ArrayList<>();
            rawMap.forEach((k, v) -> {
                if (k instanceof String key && key.startsWith("id:")) {
                    try {
                        Long pid = Long.parseLong(key.substring(3));
                        Integer qty = v instanceof Integer i ? i : 1;
                        toMerge.add(new AddToCartRequest());
                        toMerge.get(toMerge.size() - 1).setProductId(pid);
                        toMerge.get(toMerge.size() - 1).setQuantity(qty);
                    } catch (NumberFormatException ignored) {
                    }
                }
            });
            if (!toMerge.isEmpty()) {
                cartService.mergeSessionCart(user.getId(), toMerge);
            }
            session.removeAttribute(SESSION_CART);
        }

        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", UserResponse.fromEntity(user)));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công", null));
    }

    @GetMapping("/cart/count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCartCount(HttpSession session) {
        Map<String, Integer> cart = getCart(session);
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", countCart(cart))));
    }

    @PostMapping("/cart/add")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addToCart(
            @Valid @RequestBody CartRequest request,
            HttpSession session) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            request.setQuantity(1);
        }
        if (request.getProductId() != null) {
            productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        }
        String key = buildCartKey(request);
        Map<String, Integer> cart = getCart(session);
        cart.merge(key, request.getQuantity(), Integer::sum);
        session.setAttribute(SESSION_CART, cart);
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", countCart(cart))));
    }

    /** Gộp session cart vào user cart (gọi từ FE sau khi đăng nhập thành công). */
    @PostMapping("/cart/merge")
    public ResponseEntity<ApiResponse<Map<String, Object>>> mergeSessionCart(
            @RequestBody List<AddToCartRequest> items,
            HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (!(userId instanceof Long uid)) {
            return ResponseEntity.ok(ApiResponse.success(Map.of("count", 0, "merged", false)));
        }
        if (items != null && !items.isEmpty()) {
            cartService.mergeSessionCart(uid, items);
        }
        session.removeAttribute(SESSION_CART);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "count", cartService.getItemCount(uid),
                "merged", true)));
    }

    private Map<String, Integer> getCart(HttpSession session) {
        Object data = session.getAttribute(SESSION_CART);
        if (data instanceof Map<?, ?> existing) {
            //noinspection unchecked
            return (Map<String, Integer>) existing;
        }
        Map<String, Integer> cart = new HashMap<>();
        session.setAttribute(SESSION_CART, cart);
        return cart;
    }

    private int countCart(Map<String, Integer> cart) {
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }

    private String buildCartKey(CartRequest request) {
        if (request.getProductId() != null) {
            return "id:" + request.getProductId();
        }
        return "name:" + (request.getProductName() == null ? "unknown" : request.getProductName().trim());
    }

    private static String hashPassword(String raw) {
        return raw == null ? null : "HASHED_" + raw;
    }
}
