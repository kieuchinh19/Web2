package com.web2.chinh.controller.admin;

import com.web2.chinh.dto.ProductResponse;
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
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    private static final String UPLOAD_DIR = "uploads/products/";

    // ===== LIST + PAGINATION + SEARCH + FILTER =====
    @GetMapping
    @Transactional(readOnly = true)
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Product> productPage = productRepository.searchAndFilter(keyword, categoryId, brandId, pageable);

        // Convert entity -> ProductResponse (đã có sẵn categoryName/brandName)
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(p -> {
                    // Force load lazy associations trước khi tách khỏi session
                    if (p.getCategory() != null) p.getCategory().getName();
                    if (p.getBrand() != null) p.getBrand().getName();
                    return ProductResponse.fromEntity(p);
                })
                .toList();

        model.addAttribute("page", "products");
        model.addAttribute("products", productResponses);
        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedBrandId", brandId);

        // pagination window
        int total = Math.max(1, productPage.getTotalPages());
        int start = Math.max(0, page - 2);
        int end = Math.min(total - 1, page + 2);
        model.addAttribute("pageStart", start);
        model.addAttribute("pageEnd", end);

        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        return "admin/products";
    }

    // ===== SAVE (CREATE + UPDATE) =====
    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String name,
                       @RequestParam(required = false) String description,
                       @RequestParam Double price,
                       @RequestParam Integer quantity,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) Long brandId,
                       @RequestParam(required = false) String imageUrl,
                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                       RedirectAttributes ra) {

        try {
            Product product;
            if (id != null && id > 0) {
                product = productRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm id=" + id));
            } else {
                product = new Product();
            }

            product.setName(name);
            product.setDescription(description);
            product.setPrice(java.math.BigDecimal.valueOf(price));
            product.setQuantity(quantity);

            if (categoryId != null) {
                product.setCategory(categoryRepository.findById(categoryId).orElse(null));
            } else {
                product.setCategory(null);
            }
            if (brandId != null) {
                product.setBrand(brandRepository.findById(brandId).orElse(null));
            } else {
                product.setBrand(null);
            }

            String imagePath = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imagePath = saveImageFile(imageFile);
            } else if (imageUrl != null && !imageUrl.isBlank()) {
                imagePath = imageUrl;
            }

            if (imagePath != null) {
                product.setImage(imagePath);
            }

            productRepository.save(product);
            ra.addFlashAttribute("message",
                    (id != null && id > 0) ? "Cập nhật sản phẩm thành công!" : "Thêm sản phẩm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // ===== DELETE (soft delete) =====
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            productService.softDelete(id);
            ra.addFlashAttribute("message", "Đã chuyển sản phẩm vào thùng rác!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // ===== THÙNG RÁC - LIST =====
    @GetMapping("/trash")
    @Transactional(readOnly = true)
    public String trash(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<ProductResponse> productPage = productService.getDeletedProducts(page, size, keyword);

        model.addAttribute("page", "trash");
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("keyword", keyword);

        int total = Math.max(1, productPage.getTotalPages());
        int start = Math.max(0, page - 2);
        int end = Math.min(total - 1, page + 2);
        model.addAttribute("pageStart", start);
        model.addAttribute("pageEnd", end);

        return "admin/trash";
    }

    // ===== KHÔI PHỤC =====
    @PostMapping("/trash/restore/{id}")
    public String restore(@PathVariable Long id, RedirectAttributes ra) {
        try {
            productService.restore(id);
            ra.addFlashAttribute("message", "Khôi phục sản phẩm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/products/trash";
    }

    // ===== XÓA VĨNH VIỄN =====
    @PostMapping("/trash/hard-delete/{id}")
    public String hardDelete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            productService.hardDelete(id);
            ra.addFlashAttribute("message", "Đã xóa vĩnh viễn sản phẩm!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/products/trash";
    }

    // ===== UPLOAD ẢNH RIÊNG (AJAX preview) =====
    @PostMapping("/upload-image")
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            return saveImageFile(file);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String saveImageFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new IOException("File rỗng");

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("File không phải định dạng ảnh");
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + ext;

        Path target = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/products/" + filename;
    }
}
