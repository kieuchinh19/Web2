package com.web2.chinh.service.impl;

import com.web2.chinh.dto.AddToCartRequest;
import com.web2.chinh.dto.CartItemResponse;
import com.web2.chinh.dto.CartResponse;
import com.web2.chinh.entity.Cart;
import com.web2.chinh.entity.CartItem;
import com.web2.chinh.entity.Product;
import com.web2.chinh.entity.User;
import com.web2.chinh.exception.ResourceNotFoundException;
import com.web2.chinh.repository.CartItemRepository;
import com.web2.chinh.repository.CartRepository;
import com.web2.chinh.repository.ProductRepository;
import com.web2.chinh.repository.UserRepository;
import com.web2.chinh.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /** Lấy hoặc tạo cart cho user. */
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserIdWithItems(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id=" + userId));
            Cart cart = Cart.builder().user(user).build();
            return cartRepository.save(cart);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId).orElse(null);
        return CartResponse.fromEntity(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public int getItemCount(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId).orElse(null);
        if (cart == null || cart.getItems() == null)
            return 0;
        return cart.getItems().stream().mapToInt(CartItem::getQuantity).sum();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmount(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId).orElse(null);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return cart.getItems().stream()
                .map(ci -> ci.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public CartItemResponse addToCart(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0)
            quantity = 1;

        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm id=" + productId));

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(ci -> ci.getProduct() != null && ci.getProduct().getId().equals(productId))
                .findFirst();

        CartItem item;
        if (existing.isPresent()) {
            item = existing.get();
            int newQty = item.getQuantity() + quantity;
            if (product.getQuantity() != null && newQty > product.getQuantity()) {
                newQty = product.getQuantity();
            }
            item.setQuantity(newQty);
            item.setPrice(product.getPrice());
        } else {
            int qty = Math.min(quantity, product.getQuantity() != null ? product.getQuantity() : quantity);
            item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(qty)
                    .price(product.getPrice())
                    .build();
            cart.getItems().add(item);
        }
        cartRepository.save(cart);
        return CartItemResponse.fromEntity(item);
    }

    @Override
    public CartItemResponse updateQuantity(Long userId, Long productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            removeItem(userId, productId);
            return null;
        }
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Giỏ hàng trống"));
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct() != null && ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không có trong giỏ"));
        Product product = item.getProduct();
        int newQty = Math.min(quantity, product.getQuantity() != null ? product.getQuantity() : quantity);
        item.setQuantity(newQty);
        cartRepository.save(cart);
        return CartItemResponse.fromEntity(item);
    }

    @Override
    public CartItemResponse increase(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Giỏ hàng trống"));
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct() != null && ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không có trong giỏ"));
        int newQty = item.getQuantity() + 1;
        if (item.getProduct().getQuantity() != null && newQty > item.getProduct().getQuantity()) {
            newQty = item.getProduct().getQuantity();
        }
        item.setQuantity(newQty);
        cartRepository.save(cart);
        return CartItemResponse.fromEntity(item);
    }

    @Override
    public CartItemResponse decrease(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Giỏ hàng trống"));
        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getProduct() != null && ci.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không có trong giỏ"));
        int newQty = item.getQuantity() - 1;
        if (newQty <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
            cartRepository.save(cart);
            return null;
        }
        item.setQuantity(newQty);
        cartRepository.save(cart);
        return CartItemResponse.fromEntity(item);
    }

    @Override
    public void removeItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId).orElse(null);
        if (cart == null)
            return;
        boolean removed = cart.getItems()
                .removeIf(ci -> ci.getProduct() != null && ci.getProduct().getId().equals(productId));
        if (removed) {
            cartRepository.save(cart);
        }
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId).orElse(null);
        if (cart == null)
            return;
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public CartResponse mergeSessionCart(Long userId, List<AddToCartRequest> sessionItems) {
        if (sessionItems == null || sessionItems.isEmpty()) {
            return getCart(userId);
        }
        for (AddToCartRequest item : sessionItems) {
            if (item == null || item.getProductId() == null)
                continue;
            try {
                addToCart(userId, item.getProductId(), item.getQuantity());
            } catch (Exception ignored) {
                // Bỏ qua sản phẩm không tồn tại / lỗi
            }
        }
        return getCart(userId);
    }
}
