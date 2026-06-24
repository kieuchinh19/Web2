package com.web2.chinh.controller;

import com.web2.chinh.dto.ApiResponse;
import com.web2.chinh.dto.OrderRequest;
import com.web2.chinh.dto.OrderResponse;
import com.web2.chinh.entity.Order;
import com.web2.chinh.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(orderService.create(request)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status) {
        return ResponseEntity
                .ok(ApiResponse.success("Cập nhật trạng thái thành công", orderService.updateStatus(id, status)));
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<ApiResponse<OrderResponse>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam Order.PaymentStatus status) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật thanh toán thành công", orderService.updatePaymentStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa đơn hàng thành công", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(orderService.getAll()));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getByUserId(userId)));
    }

    @GetMapping("/by-status")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getByStatus(@RequestParam Order.OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getByStatus(status)));
    }
}
