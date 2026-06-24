package com.web2.chinh.repository;

import com.web2.chinh.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Tìm theo tên (không phân biệt hoa thường)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Tìm theo khoảng giá
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Sản phẩm còn hàng
    List<Product> findByQuantityGreaterThan(Integer quantity);

    // Tìm theo Category
    List<Product> findByCategoryId(Long categoryId);
    // Đếm sản phẩm theo Category
    long countByCategoryId(Long categoryId);
    // Phân trang sản phẩm theo category (chỉ sản phẩm chưa xóa, chưa sort)
    Page<Product> findByCategoryIdAndIsDeletedFalse(Long categoryId, Pageable pageable);

    // Tìm theo Brand
    List<Product> findByBrandId(Long brandId);

    // Đếm sản phẩm theo Brand
    long countByBrandId(Long brandId);

    // Custom query với JPQL
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Tìm kiếm + lọc theo category + brand, có phân trang (chỉ sản phẩm chưa xóa)
    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:brandId IS NULL OR p.brand.id = :brandId) " +
            "AND p.isDeleted = false")
    Page<Product> searchAndFilter(@Param("keyword") String keyword,
                                  @Param("categoryId") Long categoryId,
                                  @Param("brandId") Long brandId,
                                  Pageable pageable);

    // ===== Soft delete queries =====

    // Lấy tất cả sản phẩm đã xóa (IsDeleted = true)
    Page<Product> findByIsDeletedTrue(Pageable pageable);

    // Tìm kiếm sản phẩm đã xóa theo keyword
    @Query("SELECT p FROM Product p WHERE p.isDeleted = true AND " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchDeleted(@Param("keyword") String keyword, Pageable pageable);
}
