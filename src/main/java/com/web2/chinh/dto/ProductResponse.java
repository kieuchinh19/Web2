package com.web2.chinh.dto;

import com.web2.chinh.entity.Product;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;

    // Thêm ảnh sản phẩm
    private String image;

    // Tên phẳng để hiển thị dễ ở template
    private String categoryName;
    private String brandName;

    private CategoryResponse category;
    private BrandResponse brand;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Soft delete info
    private Boolean isDeleted;
    private LocalDateTime deletedAt;

    public static ProductResponse fromEntity(Product p) {

        ProductResponseBuilder builder = ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .quantity(p.getQuantity())
                .image(p.getImage())
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .brandName(p.getBrand() != null ? p.getBrand().getName() : null)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .isDeleted(p.getIsDeleted())
                .deletedAt(p.getDeletedAt());

        if (p.getCategory() != null) {
            builder.category(CategoryResponse.fromEntity(p.getCategory(), null));
        }

        if (p.getBrand() != null) {
            builder.brand(BrandResponse.fromEntity(p.getBrand(), null));
        }

        return builder.build();
    }
}