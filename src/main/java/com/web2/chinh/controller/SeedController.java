package com.web2.chinh.controller;

import com.web2.chinh.config.DataSeeder;
import com.web2.chinh.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/seed")
@RequiredArgsConstructor
public class SeedController {

    private final DataSeeder dataSeeder;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> seed() {
        // Gọi trực tiếp logic seed
        dataSeeder.run();
        return ResponseEntity.ok(ApiResponse.success("Seed dữ liệu mỹ phẩm thành công!", Map.of(
                "brands", 5,
                "categories", 5,
                "products", 10)));
    }
}
