package com.web2.chinh.service;

import com.web2.chinh.dto.ProductRequest;
import com.web2.chinh.dto.ProductResponse;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
    ProductResponse getById(Long id);
    List<ProductResponse> getAll();
    Page<ProductResponse> getPaged(int page, int size, String sortBy, String direction);
    List<ProductResponse> searchByName(String name);
    List<ProductResponse> findByPriceRange(BigDecimal min, BigDecimal max);
    List<ProductResponse> findInStock(Integer minQty);
    List<ProductResponse> findByCategoryId(Long categoryId);
    // Phân trang sản phẩm theo category với sort
    // sort: price-asc | price-desc | newest | best-selling
    Page<ProductResponse> findByCategoryPaged(Long categoryId, int page, int size, String sort);
    List<ProductResponse> findByBrandId(Long brandId);

    // ===== Soft delete methods =====
    void softDelete(Long id);
    void restore(Long id);
    void hardDelete(Long id);
    Page<ProductResponse> getDeletedProducts(int page, int size, String keyword);
}
