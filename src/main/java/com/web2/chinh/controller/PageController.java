package com.web2.chinh.controller;

import com.web2.chinh.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class PageController {

    private final ProductService productService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final com.web2.chinh.service.NewsService newsService;
    private final com.web2.chinh.service.OrderService orderService;

    // ============ DASHBOARD ============
    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(Model model) {
        model.addAttribute("page", "dashboard");
        model.addAttribute("totalProducts", productService.getAll().size());
        model.addAttribute("totalBrands", brandService.getAll().size());
        model.addAttribute("totalCategories", categoryService.getAll().size());
        model.addAttribute("totalUsers", userService.getAll().size());
        model.addAttribute("totalNews", newsService.getAll().size());
        model.addAttribute("totalOrders", orderService.getAll().size());
        return "admin/dashboard";
    }

    // ============ PRODUCTS ============
    // Đã chuyển sang AdminProductController để xử lý đầy đủ CRUD + phân trang + tìm kiếm + upload
    // @GetMapping("/products") ...

    // ============ BRANDS ============
    // Đã chuyển sang AdminBrandController để xử lý CRUD + phân trang + tìm kiếm + upload logo + thùng rác
    // @GetMapping("/brands") ...

    // ============ CATEGORIES ============
    // Đã chuyển sang AdminCategoryController để xử lý CRUD + phân trang + tìm kiếm + thùng rác
    // @GetMapping("/categories") ...

    // ============ USERS ============
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("page", "users");
        model.addAttribute("users", userService.getAll());
        return "admin/users";
    }

    // ============ NEWS ============
    @GetMapping("/news")
    public String news(Model model) {
        model.addAttribute("page", "news");
        model.addAttribute("newsList", newsService.getAll());
        return "admin/news";
    }

    @GetMapping("/news/{id}")
    public String newsDetail(@PathVariable Long id, Model model) {
        model.addAttribute("page", "news");
        model.addAttribute("newsItem", newsService.getById(id));
        return "admin/news-detail";
    }

    // ============ ORDERS ============
    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("page", "orders");
        model.addAttribute("orders", orderService.getAll());
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        model.addAttribute("page", "orders");
        model.addAttribute("order", orderService.getById(id));
        return "admin/order-detail";
    }
}
