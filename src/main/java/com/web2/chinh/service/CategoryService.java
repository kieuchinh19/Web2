package com.web2.chinh.service;

import com.web2.chinh.dto.CategoryRequest;
import com.web2.chinh.dto.CategoryResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    void delete(Long id);

    CategoryResponse getById(Long id);

    List<CategoryResponse> getAll();

    List<CategoryResponse> searchByName(String keyword);

    long countProducts(Long categoryId);

    // ===== Soft delete methods =====
    void softDelete(Long id);
    void restore(Long id);
    void hardDelete(Long id);
    Page<CategoryResponse> getActiveCategories(int page, int size, String keyword);
    Page<CategoryResponse> getDeletedCategories(int page, int size, String keyword);
}
