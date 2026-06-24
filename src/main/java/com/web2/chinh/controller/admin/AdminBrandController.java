package com.web2.chinh.controller.admin;

import com.web2.chinh.dto.BrandRequest;
import com.web2.chinh.dto.BrandResponse;
import com.web2.chinh.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
import java.util.UUID;

@Controller
@RequestMapping("/admin/brands")
@RequiredArgsConstructor
public class AdminBrandController {

    private final BrandService brandService;

    private static final String UPLOAD_DIR = "uploads/brands/";

    // ===== LIST + PAGINATION + SEARCH =====
    @GetMapping
    @Transactional(readOnly = true)
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<BrandResponse> brandPage = brandService.getActiveBrands(page, size, keyword);

        model.addAttribute("page", "brands");
        model.addAttribute("brands", brandPage.getContent());
        model.addAttribute("brandPage", brandPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", brandPage.getTotalPages());
        model.addAttribute("totalItems", brandPage.getTotalElements());
        model.addAttribute("keyword", keyword);

        int total = Math.max(1, brandPage.getTotalPages());
        int start = Math.max(0, page - 2);
        int end = Math.min(total - 1, page + 2);
        model.addAttribute("pageStart", start);
        model.addAttribute("pageEnd", end);

        return "admin/brands";
    }

    // ===== SAVE (CREATE + UPDATE) =====
    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String logoUrl,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            RedirectAttributes ra) {
        try {
            String logoPath = null;
            if (logoFile != null && !logoFile.isEmpty()) {
                logoPath = saveLogoFile(logoFile);
            } else if (logoUrl != null && !logoUrl.isBlank()) {
                logoPath = logoUrl;
            }

            BrandRequest req = BrandRequest.builder()
                    .name(name)
                    .country(country)
                    .description(description)
                    .website(website)
                    .logo(logoPath)
                    .build();

            if (id != null && id > 0) {
                // Khi update mà không upload file mới, giữ logo cũ
                if (logoPath == null) {
                    BrandResponse existing = brandService.getById(id);
                    req.setLogo(existing.getLogo());
                }
                brandService.update(id, req);
                ra.addFlashAttribute("message", "Cập nhật thương hiệu thành công!");
            } else {
                brandService.create(req);
                ra.addFlashAttribute("message", "Thêm thương hiệu thành công!");
            }
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/brands";
    }

    // ===== SOFT DELETE =====
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            brandService.softDelete(id);
            ra.addFlashAttribute("message", "Đã chuyển thương hiệu vào thùng rác!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/brands";
    }

    // ===== THÙNG RÁC - LIST =====
    @GetMapping("/trash")
    @Transactional(readOnly = true)
    public String trash(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<BrandResponse> brandPage = brandService.getDeletedBrands(page, size, keyword);

        model.addAttribute("page", "brand-trash");
        model.addAttribute("brands", brandPage.getContent());
        model.addAttribute("brandPage", brandPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", brandPage.getTotalPages());
        model.addAttribute("totalItems", brandPage.getTotalElements());
        model.addAttribute("keyword", keyword);

        int total = Math.max(1, brandPage.getTotalPages());
        int start = Math.max(0, page - 2);
        int end = Math.min(total - 1, page + 2);
        model.addAttribute("pageStart", start);
        model.addAttribute("pageEnd", end);

        return "admin/brand-trash";
    }

    // ===== KHÔI PHỤC =====
    @PostMapping("/trash/restore/{id}")
    public String restore(@PathVariable Long id, RedirectAttributes ra) {
        try {
            brandService.restore(id);
            ra.addFlashAttribute("message", "Khôi phục thương hiệu thành công!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/brands/trash";
    }

    // ===== XÓA VĨNH VIỄN =====
    @PostMapping("/trash/hard-delete/{id}")
    public String hardDelete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            brandService.hardDelete(id);
            ra.addFlashAttribute("message", "Đã xóa vĩnh viễn thương hiệu!");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/brands/trash";
    }

    // ===== UPLOAD LOGO (AJAX) =====
    @PostMapping("/upload-logo")
    @ResponseBody
    public String uploadLogo(@RequestParam("file") MultipartFile file) {
        try {
            return saveLogoFile(file);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String saveLogoFile(MultipartFile file) throws IOException {
        if (file.isEmpty())
            throw new IOException("File rỗng");

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
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID().toString() + ext;

        Path target = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/brands/" + filename;
    }
}
