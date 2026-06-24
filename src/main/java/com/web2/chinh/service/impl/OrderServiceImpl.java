package com.web2.chinh.service.impl;

import com.web2.chinh.dto.OrderItemRequest;
import com.web2.chinh.dto.OrderRequest;
import com.web2.chinh.dto.OrderResponse;
import com.web2.chinh.entity.Order;
import com.web2.chinh.entity.OrderItem;
import com.web2.chinh.entity.Product;
import com.web2.chinh.entity.User;
import com.web2.chinh.exception.ResourceNotFoundException;
import com.web2.chinh.repository.OrderRepository;
import com.web2.chinh.repository.ProductRepository;
import com.web2.chinh.repository.UserRepository;
import com.web2.chinh.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public OrderResponse create(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user id=" + request.getUserId()));

        Order order = Order.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .shippingAddress(request.getShippingAddress())
                .note(request.getNote())
                .shippingFee(request.getShippingFee() != null ? request.getShippingFee() : BigDecimal.ZERO)
                .discountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO)
                .paymentMethod(
                        request.getPaymentMethod() != null ? request.getPaymentMethod() : Order.PaymentMethod.COD)
                .status(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.UNPAID)
                .items(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy sản phẩm id=" + itemReq.getProductId()));

            if (product.getQuantity() < itemReq.getQuantity()) {
                throw new IllegalStateException("Sản phẩm " + product.getName() + " không đủ hàng!");
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(subtotal);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .productImage(product.getImage())
                    .price(product.getPrice())
                    .quantity(itemReq.getQuantity())
                    .subtotal(subtotal)
                    .build();
            order.getItems().add(item);

            product.setQuantity(product.getQuantity() - itemReq.getQuantity());
            productRepository.save(product);
        }

        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;
        BigDecimal discount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;
        order.setTotalAmount(total.add(shippingFee).subtract(discount));

        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    @Override
    public OrderResponse updateStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng id=" + id));
        order.setStatus(status);
        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    @Override
    public OrderResponse updatePaymentStatus(Long id, Order.PaymentStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng id=" + id));
        order.setPaymentStatus(status);
        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    @Override
    public void delete(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng id=" + id));
        orderRepository.delete(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng id=" + id));
        // Force load items
        order.getItems().size();
        return OrderResponse.fromEntity(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        return orderRepository.findAll().stream()
                .map(o -> {
                    o.getItems().size();
                    return OrderResponse.fromEntity(o);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(o -> {
                    o.getItems().size();
                    return OrderResponse.fromEntity(o);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(o -> {
                    o.getItems().size();
                    return OrderResponse.fromEntity(o);
                })
                .collect(Collectors.toList());
    }
}
