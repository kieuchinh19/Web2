package com.web2.chinh.controller;

import com.web2.chinh.dto.ApiResponse;
import com.web2.chinh.dto.BrandRequest;
import com.web2.chinh.dto.BrandResponse;
import com.web2.chinh.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public ResponseEntity<ApiResponse<BrandResponse>> create(@Valid @RequestBody BrandRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(brandService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody BrandRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", brandService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        brandService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa thành công", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(brandService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BrandResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(brandService.getAll()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> search(@RequestParam String name) {
        return ResponseEntity.ok(ApiResponse.success(brandService.searchByName(name)));
    }

    @GetMapping("/by-country")
    public ResponseEntity<ApiResponse<List<BrandResponse>>> byCountry(@RequestParam String country) {
        return ResponseEntity.ok(ApiResponse.success(brandService.getByCountry(country)));
    }

    @GetMapping("/{id}/product-count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> countProducts(@PathVariable Long id) {
        BrandResponse brand = brandService.getById(id);
        long count = brandService.countProducts(id);
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "brandId", id,
                "brandName", brand.getName(),
                "productCount", count)));
    }
}
