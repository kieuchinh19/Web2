package com.web2.chinh.service;

import com.web2.chinh.dto.BrandRequest;
import com.web2.chinh.dto.BrandResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BrandService {
    BrandResponse create(BrandRequest request);

    BrandResponse update(Long id, BrandRequest request);

    void delete(Long id);

    BrandResponse getById(Long id);

    List<BrandResponse> getAll();

    List<BrandResponse> searchByName(String keyword);

    List<BrandResponse> getByCountry(String country);

    long countProducts(Long brandId);

    // ===== Soft delete methods =====
    void softDelete(Long id);
    void restore(Long id);
    void hardDelete(Long id);
    Page<BrandResponse> getActiveBrands(int page, int size, String keyword);
    Page<BrandResponse> getDeletedBrands(int page, int size, String keyword);
}
