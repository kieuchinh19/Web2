package com.web2.chinh.dto;

import com.web2.chinh.entity.Brand;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandResponse {
    private Long id;
    private String name;
    private String country;
    private String description;
    private String logo;
    private String website;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer productCount;
    private Boolean isDeleted;
    private LocalDateTime deletedAt;

    public static BrandResponse fromEntity(Brand b, Integer productCount) {
        return BrandResponse.builder()
                .id(b.getId())
                .name(b.getName())
                .country(b.getCountry())
                .description(b.getDescription())
                .logo(b.getLogo())
                .website(b.getWebsite())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .productCount(productCount)
                .isDeleted(b.getIsDeleted())
                .deletedAt(b.getDeletedAt())
                .build();
    }
}
