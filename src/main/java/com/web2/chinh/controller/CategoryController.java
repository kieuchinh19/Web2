package com.web2.chinh.controller;

import com.web2.chinh.dto.ApiResponse;
import com.web2.chinh.dto.CategoryRequest;
import com.web2.chinh.dto.CategoryResponse;
import com.web2.chinh.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // CREATE
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(categoryService.create(request)));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", categoryService.update(id, request)));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa thành công", null));
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getById(id)));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAll()));
    }

    // SEARCH
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> search(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.searchByName(name)));
    }

    // COUNT PRODUCTS BY CATEGORY
    @GetMapping("/{id}/product-count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> countProducts(@PathVariable Long id) {
        CategoryResponse category = categoryService.getById(id);
        long count = categoryService.countProducts(id);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "categoryId", id,
                "categoryName", category.getName(),
                "productCount", count)));
    }
}
