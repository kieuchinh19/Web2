package com.web2.chinh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FrontendPageController {

    // ============ FRONTEND: TRANG DANH MỤC SẢN PHẨM ============
    @GetMapping("/category/{id}")
    public String categoryPage(@RequestParam(required = false) Integer page,
            @RequestParam(required = false) String sort,
            @PathVariable Long id,
            Model model) {
        model.addAttribute("page", "category");
        model.addAttribute("categoryId", id);
        model.addAttribute("currentSort", sort == null ? "newest" : sort);
        model.addAttribute("currentPage", page == null ? 0 : page);
        return "category";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    // ============ FRONTEND: TRANG GIỎ HÀNG ============
    @GetMapping("/cart")
    public String cartPage() {
        return "cart";
    }

    // ============ FRONTEND: TRANG CHI TIẾT SẢN PHẨM ============
    @GetMapping("/product/{id}")
    public String productDetailPage(@PathVariable Long id, Model model) {
        model.addAttribute("productId", id);
        return "product-detail";
    }
}
