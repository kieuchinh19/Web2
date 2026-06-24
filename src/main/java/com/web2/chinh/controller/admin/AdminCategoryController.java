package com.web2.chinh.controller.admin;

import com.web2.chinh.dto.CategoryResponse;
import com.web2.chinh.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    // ===== LIST + PAGINATION + SEARCH =====
    @GetMapping
    @Transactional(readOnly = true)
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<CategoryResponse> categoryPage = categoryService.getActiveCategories(page, size, keyword);

        model.addAttribute("page", "categories");
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalItems", categoryPage.getTotalElements());
        model.addAttribute("keyword", keyword);

        int total = Math.max(1, categoryPage.getTotalPages());
        int start = Math.max(0, page - 2);
        int end = Math.min(total - 1, page + 2);
        model.addAttribute("pageStart", start);
        model.addAttribute("pageEnd", end);

        return "admin/categories";
    }

    // ===== SAVE (CREATE + UPDATE) =====
    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String slug,
            RedirectAttributes ra) {
        try {
            com.web2.chinh.dto.CategoryRequest req = com.web2.chinh.dto.CategoryRequest.builder()
                    .name(name)
                    .description(description)
                    .slug(slug)
                    .build();
            if (id != null && id > 0) {
                categoryService.update(id, req);
                ra.addFlashAttribute("message", "Cập nhật danh mục thành công!");
            } else {
                categoryService.create(req);
                ra.addFlashAttribute("message", "Thêm danh mục thành công!");
            }
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // ===== SOFT DELETE =====
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoryService.softDelete(id);
            ra.addFlashAttribute("message", "Đã chuyển danh mục vào thùng rác!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // ===== THÙNG RÁC - LIST =====
    @GetMapping("/trash")
    @Transactional(readOnly = true)
    public String trash(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<CategoryResponse> categoryPage = categoryService.getDeletedCategories(page, size, keyword);

        model.addAttribute("page", "category-trash");
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalItems", categoryPage.getTotalElements());
        model.addAttribute("keyword", keyword);

        int total = Math.max(1, categoryPage.getTotalPages());
        int start = Math.max(0, page - 2);
        int end = Math.min(total - 1, page + 2);
        model.addAttribute("pageStart", start);
        model.addAttribute("pageEnd", end);

        return "admin/category-trash";
    }

    // ===== KHÔI PHỤC =====
    @PostMapping("/trash/restore/{id}")
    public String restore(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoryService.restore(id);
            ra.addFlashAttribute("message", "Khôi phục danh mục thành công!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/categories/trash";
    }

    // ===== XÓA VĨNH VIỄN =====
    @PostMapping("/trash/hard-delete/{id}")
    public String hardDelete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoryService.hardDelete(id);
            ra.addFlashAttribute("message", "Đã xóa vĩnh viễn danh mục!");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/categories/trash";
    }
}
