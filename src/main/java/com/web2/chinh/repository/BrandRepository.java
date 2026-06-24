package com.web2.chinh.repository;

import com.web2.chinh.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);

    boolean existsByName(String name);

    List<Brand> findByNameContainingIgnoreCase(String keyword);

    List<Brand> findByCountry(String country);

    // ===== Soft delete queries =====
    Page<Brand> findByIsDeletedFalse(Pageable pageable);

    Page<Brand> findByIsDeletedTrue(Pageable pageable);

    @Query("SELECT b FROM Brand b WHERE b.isDeleted = false AND " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Brand> searchActive(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Brand b WHERE b.isDeleted = true AND " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Brand> searchDeleted(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Brand b WHERE LOWER(b.name) = LOWER(:name) AND b.isDeleted = false")
    Optional<Brand> findActiveByName(@Param("name") String name);
}
