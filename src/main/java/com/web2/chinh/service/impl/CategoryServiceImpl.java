package com.web2.chinh.service.impl;

import com.web2.chinh.dto.CategoryRequest;
import com.web2.chinh.dto.CategoryResponse;
import com.web2.chinh.entity.Category;
import com.web2.chinh.exception.ResourceNotFoundException;
import com.web2.chinh.repository.CategoryRepository;
import com.web2.chinh.repository.ProductRepository;
import com.web2.chinh.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.findActiveByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại: " + request.getName());
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .slug(request.getSlug())
                .isDeleted(false)
                .build();
        return toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục id=" + id));
        // Check trùng tên (trừ chính nó)
        categoryRepository.findActiveByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Tên danh mục đã tồn tại: " + request.getName());
            }
        });
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            category.setSlug(request.getSlug());
        }
        return toResponse(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        softDelete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục id=" + id));
        return toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchByName(String keyword) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countProducts(Long categoryId) {
        return productRepository.countByCategoryId(categoryId);
    }

    // ===== Soft delete =====
    @Override
    public void softDelete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục id=" + id));
        category.setIsDeleted(true);
        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }

    @Override
    public void restore(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục id=" + id));
        // Check trùng tên khi khôi phục
        categoryRepository.findActiveByName(category.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException(
                        "Không thể khôi phục: tên danh mục đã tồn tại ở danh mục khác: " + category.getName());
            }
        });
        category.setIsDeleted(false);
        category.setDeletedAt(null);
        categoryRepository.save(category);
    }

    @Override
    public void hardDelete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy danh mục id=" + id);
        }
        if (productRepository.countByCategoryId(id) > 0) {
            throw new IllegalStateException("Không thể xóa vĩnh viễn danh mục đang có sản phẩm!");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getActiveCategories(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Category> result;
        if (keyword == null || keyword.isBlank()) {
            result = categoryRepository.findByIsDeletedFalse(pageable);
        } else {
            result = categoryRepository.searchActive(keyword.trim(), pageable);
        }
        return result.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getDeletedCategories(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "deletedAt"));
        Page<Category> result;
        if (keyword == null || keyword.isBlank()) {
            result = categoryRepository.findByIsDeletedTrue(pageable);
        } else {
            result = categoryRepository.searchDeleted(keyword.trim(), pageable);
        }
        return result.map(this::toResponse);
    }

    private CategoryResponse toResponse(Category c) {
        Integer count = (int) productRepository.countByCategoryId(c.getId());
        return CategoryResponse.fromEntity(c, count);
    }
}
