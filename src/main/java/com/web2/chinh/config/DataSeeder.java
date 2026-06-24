package com.web2.chinh.config;

import com.web2.chinh.entity.Brand;
import com.web2.chinh.entity.Category;
import com.web2.chinh.entity.Product;
import com.web2.chinh.repository.BrandRepository;
import com.web2.chinh.repository.CategoryRepository;
import com.web2.chinh.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        run();
    }

    public void run() {
        if (productRepository.count() > 0) {
            log.info("Database đã có dữ liệu ({} products), bỏ qua seed.", productRepository.count());
            return;
        }
        log.info("Bắt đầu seed dữ liệu mỹ phẩm mẫu...");

        // ============ BRANDS ============
        Brand mac = saveBrand("MAC", "Mỹ", "Thương hiệu trang điểm chuyên nghiệp",
                "https://logo.clearbit.com/maccosmetics.com", "https://www.maccosmetics.com");
        Brand dior = saveBrand("Dior", "Pháp", "Thương hiệu xa xỉ nổi tiếng thế giới",
                "https://logo.clearbit.com/dior.com", "https://www.dior.com");
        Brand laroche = saveBrand("La Roche-Posay", "Pháp", "Chuyên dược mỹ phẩm cho da nhạy cảm",
                "https://logo.clearbit.com/laroche-posay.com.vn", "https://www.laroche-posay.vn");
        Brand innisfree = saveBrand("Innisfree", "Hàn Quốc", "Mỹ phẩm thiên nhiên từ đảo Jeju",
                "https://logo.clearbit.com/innisfree.com", "https://www.innisfree.com");
        Brand chanel = saveBrand("Chanel", "Pháp", "Thương hiệu nước hoa và trang điểm hàng đầu",
                "https://logo.clearbit.com/chanel.com", "https://www.chanel.com");

        // ============ CATEGORIES ============
        Category son = saveCategory("Son môi", "Các loại son thỏi, son dưỡng, son lì", "son-moi");
        Category kemDuong = saveCategory("Kem dưỡng da", "Kem dưỡng ẩm, dưỡng trắng, chống lão hóa", "kem-duong-da");
        Category nuocHoa = saveCategory("Nước hoa", "Nước hoa nam nữ các loại", "nuoc-hoa");
        Category serum = saveCategory("Serum", "Tinh chất dưỡng da đặc trị", "serum");
        Category matNa = saveCategory("Mặt nạ", "Mặt nạ giấy, mặt nạ ngủ, mặt nạ đất sét", "mat-na");

        // ============ PRODUCTS ============
        saveProduct("Son MAC Ruby Woo", "Son lì màu đỏ ruby huyền thoại", new BigDecimal("650000"), 50, son, mac);
        saveProduct("Son Dior Addict 999", "Son đỏ biểu tượng của Dior", new BigDecimal("950000"), 30, son, dior);
        saveProduct("Son Chanel Rouge Allure", "Son lì cao cấp", new BigDecimal("1200000"), 25, son, chanel);

        saveProduct("Kem La Roche-Posay Cicaplast", "Kem phục hồi da, giảm kích ứng", new BigDecimal("450000"), 80,
                kemDuong, laroche);
        saveProduct("Kem dưỡng Innisfree Green Tea", "Kem dưỡng chiết xuất trà xanh", new BigDecimal("380000"), 100,
                kemDuong, innisfree);

        saveProduct("Nước hoa Chanel No.5", "Biểu tượng nước hoa nữ kinh điển", new BigDecimal("3500000"), 15, nuocHoa,
                chanel);
        saveProduct("Nước hoa Dior Sauvage", "Nước hoa nam huyền thoại", new BigDecimal("3200000"), 20, nuocHoa, dior);

        saveProduct("Serum La Roche-Posay Hyalu B5", "Serum phục hồi và cấp ẩm sâu", new BigDecimal("850000"), 60,
                serum, laroche);
        saveProduct("Serum Innisfree Vitamin C", "Serum sáng da, mờ thâm", new BigDecimal("520000"), 70, serum,
                innisfree);

        saveProduct("Mặt nạ Innisfree Green Tea", "Mặt nạ giấy trà xanh dưỡng ẩm", new BigDecimal("35000"), 200, matNa,
                innisfree);

        log.info("Seed dữ liệu mỹ phẩm thành công! Brand: {}, Category: {}, Product: {}",
                brandRepository.count(), categoryRepository.count(), productRepository.count());
    }

    private Brand saveBrand(String name, String country, String desc, String logo, String web) {
        return brandRepository.save(Brand.builder()
                .name(name).country(country).description(desc).logo(logo).website(web)
                .build());
    }

    private Category saveCategory(String name, String desc, String slug) {
        return categoryRepository.save(Category.builder()
                .name(name).description(desc).slug(slug)
                .build());
    }

    private void saveProduct(String name, String desc, BigDecimal price, int qty, Category cat, Brand brand) {
        productRepository.save(Product.builder()
                .name(name).description(desc).price(price).quantity(qty)
                .category(cat).brand(brand)
                .build());
    }
}
