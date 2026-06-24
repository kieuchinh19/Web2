package com.web2.chinh.service.impl;

import com.web2.chinh.dto.ProductRequest;
import com.web2.chinh.dto.ProductResponse;
import com.web2.chinh.entity.Brand;
import com.web2.chinh.entity.Category;
import com.web2.chinh.entity.Product;
import com.web2.chinh.exception.ResourceNotFoundException;
import com.web2.chinh.repository.BrandRepository;
import com.web2.chinh.repository.CategoryRepository;
import com.web2.chinh.repository.ProductRepository;
import com.web2.chinh.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

   @Override
public ProductResponse create(ProductRequest request) {

    Product product = Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .quantity(request.getQuantity())
            .image(request.getImage())
            .category(resolveCategory(request.getCategoryId()))
            .brand(resolveBrand(request.getBrandId()))
            .build();

    return ProductResponse.fromEntity(productRepository.save(product));
}

    @Override
public ProductResponse update(Long id, ProductRequest request) {

    Product product = productRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Không tìm thấy sản phẩm id=" + id));

    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setQuantity(request.getQuantity());
    product.setImage(request.getImage());

    product.setCategory(resolveCategory(request.getCategoryId()));
    product.setBrand(resolveBrand(request.getBrandId()));

    return ProductResponse.fromEntity(productRepository.save(product));
}

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm id=" + id);
        }
        productRepository.deleteById(id);
    }

    // ===== Soft delete =====
    @Override
    public void softDelete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm id=" + id));
        product.setIsDeleted(true);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Override
    public void restore(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm id=" + id));
        product.setIsDeleted(false);
        product.setDeletedAt(null);
        productRepository.save(product);
    }

    @Override
    public void hardDelete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm id=" + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getDeletedProducts(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "deletedAt"));
        Page<Product> result;
        if (keyword == null || keyword.isBlank()) {
            result = productRepository.findByIsDeletedTrue(pageable);
        } else {
            result = productRepository.searchDeleted(keyword.trim(), pageable);
        }
        return result.map(p -> {
            if (p.getCategory() != null) p.getCategory().getName();
            if (p.getBrand() != null) p.getBrand().getName();
            return ProductResponse.fromEntity(p);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm id=" + id));
        return ProductResponse.fromEntity(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getPaged(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return productRepository.findAll(PageRequest.of(page, size, sort))
                .map(ProductResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findInStock(Integer minQty) {
        return productRepository.findByQuantityGreaterThan(minQty).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findByCategoryId(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Không tìm thấy danh mục id=" + categoryId);
        }
        return productRepository.findByCategoryId(categoryId).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> findByCategoryPaged(Long categoryId, int page, int size, String sort) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Không tìm thấy danh mục id=" + categoryId);
        }
        Sort sortObj;
        switch (sort == null ? "newest" : sort) {
            case "price-asc":
                sortObj = Sort.by(Sort.Direction.ASC, "price");
                break;
            case "price-desc":
                sortObj = Sort.by(Sort.Direction.DESC, "price");
                break;
            case "best-selling":
                sortObj = Sort.by(Sort.Direction.DESC, "quantity");
                break;
            case "newest":
            default:
                sortObj = Sort.by(Sort.Direction.DESC, "id");
                break;
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);
        return productRepository.findByCategoryIdAndIsDeletedFalse(categoryId, pageable)
                .map(p -> {
                    if (p.getCategory() != null) p.getCategory().getName();
                    if (p.getBrand() != null) p.getBrand().getName();
                    return ProductResponse.fromEntity(p);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findByBrandId(Long brandId) {
        if (!brandRepository.existsById(brandId)) {
            throw new ResourceNotFoundException("Không tìm thấy thương hiệu id=" + brandId);
        }
        return productRepository.findByBrandId(brandId).stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục id=" + categoryId));
    }

    private Brand resolveBrand(Long brandId) {
        if (brandId == null) return null;
        return brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thương hiệu id=" + brandId));
    }
}
