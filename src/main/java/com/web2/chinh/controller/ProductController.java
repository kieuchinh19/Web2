package com.web2.chinh.controller;

import com.web2.chinh.dto.ApiResponse;
import com.web2.chinh.dto.ProductRequest;
import com.web2.chinh.dto.ProductResponse;
import com.web2.chinh.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // CREATE
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(productService.create(request)));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", productService.update(id, request)));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa thành công", null));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(productService.getAll()));
    }

    // GET PAGED
    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(ApiResponse.success(productService.getPaged(page, size, sortBy, direction)));
    }

    // SEARCH BY NAME
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> search(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.success(productService.searchByName(name)));
    }

    // FIND BY PRICE RANGE
    @GetMapping("/price-range")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> priceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return ResponseEntity.ok(ApiResponse.success(productService.findByPriceRange(min, max)));
    }

    // FIND IN STOCK
    @GetMapping("/in-stock")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> inStock(
            @RequestParam(defaultValue = "0") Integer minQty) {
        return ResponseEntity.ok(ApiResponse.success(productService.findInStock(minQty)));
    }

    // FIND BY CATEGORY
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success(productService.findByCategoryId(categoryId)));
    }
    // FIND BY CATEGORY (paged + sort: price-asc | price-desc | newest | best-selling)
    @GetMapping("/by-category-paged/{categoryId}")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> findByCategoryPaged(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "newest") String sort) {
        return ResponseEntity.ok(ApiResponse.success(
                productService.findByCategoryPaged(categoryId, page, size, sort)));
    }

    // FIND BY BRAND
    @GetMapping("/by-brand/{brandId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> findByBrand(
            @PathVariable Long brandId) {
        return ResponseEntity.ok(ApiResponse.success(productService.findByBrandId(brandId)));
    }
}
