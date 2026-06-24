package com.web2.chinh.repository;

import com.web2.chinh.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    Optional<Category> findBySlug(String slug);

    boolean existsByName(String name);

    List<Category> findByNameContainingIgnoreCase(String keyword);

    // ===== Soft delete queries =====
    Page<Category> findByIsDeletedFalse(Pageable pageable);

    Page<Category> findByIsDeletedTrue(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.isDeleted = false AND " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Category> searchActive(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.isDeleted = true AND " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Category> searchDeleted(@Param("keyword") String keyword, Pageable pageable);

    // Tìm theo tên bỏ qua soft delete để check trùng
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name) AND c.isDeleted = false")
    Optional<Category> findActiveByName(@Param("name") String name);
}
