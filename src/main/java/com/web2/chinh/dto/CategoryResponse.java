package com.web2.chinh.dto;

import com.web2.chinh.entity.Category;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String slug;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer productCount; // số sản phẩm trong danh mục
    private Boolean isDeleted;
    private LocalDateTime deletedAt;

    public static CategoryResponse fromEntity(Category c, Integer productCount) {
        return CategoryResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .slug(c.getSlug())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .productCount(productCount)
                .isDeleted(c.getIsDeleted())
                .deletedAt(c.getDeletedAt())
                .build();
    }
}
