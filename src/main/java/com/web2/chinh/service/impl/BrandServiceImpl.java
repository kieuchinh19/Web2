package com.web2.chinh.service.impl;

import com.web2.chinh.dto.BrandRequest;
import com.web2.chinh.dto.BrandResponse;
import com.web2.chinh.entity.Brand;
import com.web2.chinh.exception.ResourceNotFoundException;
import com.web2.chinh.repository.BrandRepository;
import com.web2.chinh.repository.ProductRepository;
import com.web2.chinh.service.BrandService;
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
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    @Override
    public BrandResponse create(BrandRequest request) {
        if (brandRepository.findActiveByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Thương hiệu đã tồn tại: " + request.getName());
        }
        Brand brand = Brand.builder()
                .name(request.getName())
                .country(request.getCountry())
                .description(request.getDescription())
                .logo(request.getLogo())
                .website(request.getWebsite())
                .isDeleted(false)
                .build();
        return toResponse(brandRepository.save(brand));
    }

    @Override
    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu id=" + id));
        // Check trùng tên (trừ chính nó)
        brandRepository.findActiveByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Thương hiệu đã tồn tại: " + request.getName());
            }
        });
        brand.setName(request.getName());
        brand.setCountry(request.getCountry());
        brand.setDescription(request.getDescription());
        brand.setLogo(request.getLogo());
        brand.setWebsite(request.getWebsite());
        return toResponse(brandRepository.save(brand));
    }

    @Override
    public void delete(Long id) {
        softDelete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponse getById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu id=" + id));
        return toResponse(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getAll() {
        return brandRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> searchByName(String keyword) {
        return brandRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponse> getByCountry(String country) {
        return brandRepository.findByCountry(country).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countProducts(Long brandId) {
        return productRepository.countByBrandId(brandId);
    }

    // ===== Soft delete =====
    @Override
    public void softDelete(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu id=" + id));
        brand.setIsDeleted(true);
        brand.setDeletedAt(LocalDateTime.now());
        brandRepository.save(brand);
    }

    @Override
    public void restore(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu id=" + id));
        brandRepository.findActiveByName(brand.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException(
                        "Không thể khôi phục: tên thương hiệu đã tồn tại ở thương hiệu khác: " + brand.getName());
            }
        });
        brand.setIsDeleted(false);
        brand.setDeletedAt(null);
        brandRepository.save(brand);
    }

    @Override
    public void hardDelete(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy thương hiệu id=" + id);
        }
        if (productRepository.countByBrandId(id) > 0) {
            throw new IllegalStateException("Không thể xóa vĩnh viễn thương hiệu đang có sản phẩm!");
        }
        brandRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandResponse> getActiveBrands(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Brand> result;
        if (keyword == null || keyword.isBlank()) {
            result = brandRepository.findByIsDeletedFalse(pageable);
        } else {
            result = brandRepository.searchActive(keyword.trim(), pageable);
        }
        return result.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandResponse> getDeletedBrands(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "deletedAt"));
        Page<Brand> result;
        if (keyword == null || keyword.isBlank()) {
            result = brandRepository.findByIsDeletedTrue(pageable);
        } else {
            result = brandRepository.searchDeleted(keyword.trim(), pageable);
        }
        return result.map(this::toResponse);
    }

    private BrandResponse toResponse(Brand b) {
        Integer count = (int) productRepository.countByBrandId(b.getId());
        return BrandResponse.fromEntity(b, count);
    }
}
