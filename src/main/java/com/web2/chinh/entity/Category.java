package com.web2.chinh.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 255)
    private String slug;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== Soft delete =====
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Quan hệ 1-N với Product
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // tránh vòng lặp khi trả về JSON
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
        if (this.slug == null && this.name != null) {
            this.slug = this.name.toLowerCase()
                    .replaceAll("[áàảãạâấầẩẫậăắằẳẵặ]", "a")
                    .replaceAll("[éèẻẽẹêếềểễệ]", "e")
                    .replaceAll("[íìỉĩị]", "i")
                    .replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o")
                    .replaceAll("[úùủũụưứừửữự]", "u")
                    .replaceAll("[ýỳỷỹỵ]", "y")
                    .replaceAll("[đ]", "d")
                    .replaceAll("\\s+", "-")
                    .replaceAll("[^a-z0-9-]", "");
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
