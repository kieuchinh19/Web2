-- ====================================================
-- KIỀU CHINH COSMETICS - Database Initialization
-- Chạy script này trong MySQL để tạo database + dữ liệu mẫu
-- ====================================================

DROP DATABASE IF EXISTS web2;
CREATE DATABASE web2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE web2;

-- ====================================================
-- 1. BẢNG USERS
-- ====================================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(255),
    avatar VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME
) ENGINE=InnoDB;

-- ====================================================
-- 2. BẢNG BRANDS
-- ====================================================
CREATE TABLE brands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    country VARCHAR(100),
    description VARCHAR(500),
    logo VARCHAR(255),
    website VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME
) ENGINE=InnoDB;

-- ====================================================
-- 3. BẢNG CATEGORIES
-- ====================================================
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    slug VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME
) ENGINE=InnoDB;

-- ====================================================
-- 4. BẢNG PRODUCTS
-- ====================================================
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    image VARCHAR(500),
    price DECIMAL(15,2) NOT NULL,
    quantity INT NOT NULL,
    category_id BIGINT,
    brand_id BIGINT,
    created_at DATETIME,
    updated_at DATETIME,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (brand_id) REFERENCES brands(id)
) ENGINE=InnoDB;

-- ====================================================
-- 5. BẢNG NEWS
-- ====================================================
CREATE TABLE news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    summary VARCHAR(500),
    content TEXT,
    image VARCHAR(500),
    author VARCHAR(100),
    is_published BOOLEAN,
    view_count BIGINT,
    published_at DATETIME,
    created_at DATETIME,
    updated_at DATETIME
) ENGINE=InnoDB;

-- ====================================================
-- 6. BẢNG ORDERS
-- ====================================================
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_code VARCHAR(30) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    shipping_address VARCHAR(255),
    note VARCHAR(500),
    total_amount DECIMAL(15,2) NOT NULL,
    shipping_fee DECIMAL(15,2) DEFAULT 0,
    discount_amount DECIMAL(15,2) DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(20) NOT NULL DEFAULT 'COD',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    order_date DATETIME,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- ====================================================
-- 7. BẢNG ORDER_ITEMS
-- ====================================================
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    product_image VARCHAR(500),
    price DECIMAL(15,2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(15,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB;

-- ====================================================
-- INSERT DỮ LIỆU MẪU
-- ====================================================

-- USERS (password đã hash bằng BCrypt)
-- admin / 123456 -> $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- user1 / 123456
INSERT INTO users (username, email, password, full_name, phone, address, role, enabled, created_at, updated_at) VALUES
('admin', 'admin@kieuchinh.vn', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Kiều Chinh', '0123456789', 'Hà Nội', 'ADMIN', TRUE, NOW(), NOW()),
('user1', 'user1@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nguyễn Thị A', '0987654321', 'TP.HCM', 'USER', TRUE, NOW(), NOW()),
('user2', 'user2@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Trần Thị B', '0912345678', 'Đà Nẵng', 'USER', TRUE, NOW(), NOW()),
('mod1', 'mod1@kieuchinh.vn', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Moderator', '0933333333', 'Hà Nội', 'MODERATOR', TRUE, NOW(), NOW());

-- BRANDS
INSERT INTO brands (name, country, description, logo, website, created_at, updated_at) VALUES
('MAC', 'Mỹ', 'Thương hiệu trang điểm chuyên nghiệp hàng đầu', 'https://logo.clearbit.com/maccosmetics.com', 'https://www.maccosmetics.com', NOW(), NOW()),
('Dior', 'Pháp', 'Thương hiệu xa xỉ nổi tiếng thế giới', 'https://logo.clearbit.com/dior.com', 'https://www.dior.com', NOW(), NOW()),
('Chanel', 'Pháp', 'Thương hiệu nước hoa và trang điểm hàng đầu', 'https://logo.clearbit.com/chanel.com', 'https://www.chanel.com', NOW(), NOW()),
('La Roche-Posay', 'Pháp', 'Chuyên dược mỹ phẩm cho da nhạy cảm', 'https://logo.clearbit.com/laroche-posay.com.vn', 'https://www.laroche-posay.vn', NOW(), NOW()),
('Innisfree', 'Hàn Quốc', 'Mỹ phẩm thiên nhiên từ đảo Jeju', 'https://logo.clearbit.com/innisfree.com', 'https://www.innisfree.com', NOW(), NOW()),
('SK-II', 'Nhật Bản', 'Thương hiệu cao cấp với công nghệ Pitera', 'https://logo.clearbit.com/sk-ii.com.vn', 'https://www.sk-ii.com.vn', NOW(), NOW()),
('Shiseido', 'Nhật Bản', 'Mỹ phẩm Nhật Bản lâu đời', 'https://logo.clearbit.com/shiseido.com', 'https://www.shiseido.co.jp', NOW(), NOW()),
('3CE', 'Hàn Quốc', 'Thương hiệu trang điểm Hàn Quốc trendy', 'https://logo.clearbit.com/3cecosmetics.com', 'https://www.3cecosmetics.com', NOW(), NOW());

-- CATEGORIES
INSERT INTO categories (name, description, slug, created_at, updated_at) VALUES
('Son môi', 'Các loại son thỏi, son dưỡng, son lì', 'son-moi', NOW(), NOW()),
('Kem dưỡng da', 'Kem dưỡng ẩm, dưỡng trắng, chống lão hóa', 'kem-duong-da', NOW(), NOW()),
('Nước hoa', 'Nước hoa nam nữ các loại', 'nuoc-hoa', NOW(), NOW()),
('Serum', 'Tinh chất dưỡng da đặc trị', 'serum', NOW(), NOW()),
('Mặt nạ', 'Mặt nạ giấy, mặt nạ ngủ, mặt nạ đất sét', 'mat-na', NOW(), NOW()),
('Tẩy trang', 'Nước tẩy trang, dầu tẩy trang, sáp tẩy trang', 'tay-trang', NOW(), NOW());

-- PRODUCTS
INSERT INTO products (name, description, image, price, quantity, category_id, brand_id, created_at, updated_at) VALUES
('Son MAC Ruby Woo', 'Son lì màu đỏ ruby huyền thoại, biểu tượng của MAC', 'https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=500', 650000, 50, 1, 1, NOW(), NOW()),
('Son Dior Addict 999', 'Son đỏ biểu tượng của Dior, mịn môi cả ngày', 'https://images.unsplash.com/photo-1631730486782-d8b7b6cf4d84?w=500', 950000, 30, 1, 2, NOW(), NOW()),
('Son Chanel Rouge Allure', 'Son lì cao cấp với 12 màu thời thượng', 'https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=500', 1200000, 25, 1, 3, NOW(), NOW()),
('Son 3CE Lip Killer', 'Son tint Hàn Quốc lên màu chuẩn', 'https://images.unsplash.com/photo-1631730486782-d8b7b6cf4d84?w=500', 380000, 80, 1, 8, NOW(), NOW()),
('Kem La Roche-Posay Cicaplast', 'Kem phục hồi da, giảm kích ứng, lành tổn thương', 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500', 450000, 80, 2, 4, NOW(), NOW()),
('Kem dưỡng Innisfree Green Tea', 'Kem dưỡng chiết xuất trà xanh, dưỡng ẩm sâu', 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500', 380000, 100, 2, 5, NOW(), NOW()),
('Kem SK-II R.N.A. Power', 'Kem chống lão hóa cao cấp với Pitera', 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500', 4500000, 15, 2, 6, NOW(), NOW()),
('Nước hoa Chanel No.5', 'Biểu tượng nước hoa nữ kinh điển, sang trọng', 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=500', 3500000, 15, 3, 3, NOW(), NOW()),
('Nước hoa Dior Sauvage', 'Nước hoa nam huyền thoại, nam tính mạnh mẽ', 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=500', 3200000, 20, 3, 2, NOW(), NOW()),
('Serum La Roche-Posay Hyalu B5', 'Serum phục hồi và cấp ẩm sâu với Hyaluronic Acid', 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500', 850000, 60, 4, 4, NOW(), NOW()),
('Serum Innisfree Vitamin C', 'Serum sáng da, mờ thâm, đều màu da', 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500', 520000, 70, 4, 5, NOW(), NOW()),
('Serum Shiseido Ultimune', 'Tinh chất tăng cường sức đề kháng cho da', 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500', 2800000, 30, 4, 7, NOW(), NOW()),
('Mặt nạ Innisfree Green Tea', 'Mặt nạ giấy trà xanh dưỡng ẩm, 20 miếng/hộp', 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=500', 35000, 200, 5, 5, NOW(), NOW()),
('Mặt nạ SK-II Pitera', 'Mặt nạ cao cấp với tinh chất Pitera', 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=500', 1800000, 25, 5, 6, NOW(), NOW());

-- NEWS
INSERT INTO news (title, summary, content, image, author, is_published, view_count, published_at, created_at, updated_at) VALUES
('Bí quyết chăm sóc da mùa hè', 'Mùa hè nóng bức khiến da dễ đổ dầu và nổi mụn. Hãy cùng tìm hiểu những bí quyết chăm sóc da hiệu quả nhất.', 'Nội dung chi tiết về cách chăm sóc da mùa hè: làm sạch, dưỡng ẩm, chống nắng đúng cách...', 'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=500', 'Kiều Chinh', TRUE, 1250, NOW(), NOW(), NOW()),
('Top 5 thương hiệu son môi được yêu thích nhất 2026', 'Cùng điểm qua 5 thương hiệu son môi đang làm mưa làm gió trong năm 2026.', 'MAC, Dior, Chanel, 3CE, YSL... là những thương hiệu được yêu thích nhất hiện nay...', 'https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=500', 'Admin', TRUE, 980, NOW(), NOW(), NOW()),
('Hướng dẫn chọn nước hoa phù hợp với tính cách', 'Nước hoa là phụ kiện không thể thiếu. Làm sao để chọn được mùi hương phù hợp?', 'Mỗi mùi hương mang một phong cách riêng: hoa cỏ nhẹ nhàng, gỗ ấm áp, cam chanh tươi mát...', 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=500', 'Chuyên gia', TRUE, 2100, NOW(), NOW(), NOW()),
('Khuyến mãi lớn mùa tựu trường - Giảm giá đến 50%', 'Chương trình khuyến mãi lớn nhất năm với hàng ngàn sản phẩm giảm giá sâu.', 'Từ ngày 1/9 đến 30/9, toàn bộ sản phẩm giảm giá đến 50%. Đặc biệt nhiều ưu đãi hấp dẫn...', 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=500', 'Marketing', TRUE, 3500, NOW(), NOW(), NOW()),
('Cách phân biệt mỹ phẩm thật - giả', 'Trên thị trường có rất nhiều mỹ phẩm giả, kém chất lượng. Hãy trang bị kiến thức để bảo vệ bản thân.', 'Kiểm tra mã vạch, bao bì, tem chống giả, mua tại đại lý ủy quyền...', 'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=500', 'Kiều Chinh', TRUE, 1750, NOW(), NOW(), NOW());

-- ORDERS
INSERT INTO orders (order_code, user_id, full_name, phone, email, shipping_address, note, total_amount, shipping_fee, discount_amount, status, payment_method, payment_status, order_date, created_at, updated_at) VALUES
('ORD2026001', 2, 'Nguyễn Thị A', '0987654321', 'user1@gmail.com', '123 Nguyễn Huệ, Q1, TP.HCM', 'Giao giờ hành chính', 1130000, 30000, 0, 'COMPLETED', 'COD', 'PAID', NOW(), NOW(), NOW()),
('ORD2026002', 3, 'Trần Thị B', '0912345678', 'user2@gmail.com', '456 Lê Lợi, Hải Châu, Đà Nẵng', '', 3500000, 0, 200000, 'SHIPPING', 'BANKING', 'PAID', NOW(), NOW(), NOW()),
('ORD2026003', 2, 'Nguyễn Thị A', '0987654321', 'user1@gmail.com', '123 Nguyễn Huệ, Q1, TP.HCM', '', 850000, 30000, 0, 'PENDING', 'MOMO', 'UNPAID', NOW(), NOW(), NOW()),
('ORD2026004', 3, 'Trần Thị B', '0912345678', 'user2@gmail.com', '456 Lê Lợi, Hải Châu, Đà Nẵng', '', 520000, 0, 0, 'CONFIRMED', 'COD', 'UNPAID', NOW(), NOW(), NOW());

-- ORDER ITEMS
INSERT INTO order_items (order_id, product_id, product_name, product_image, price, quantity, subtotal) VALUES
(1, 1, 'Son MAC Ruby Woo', 'https://images.unsplash.com/photo-1586495777744-4413f21062fa?w=500', 650000, 1, 650000),
(1, 5, 'Kem La Roche-Posay Cicaplast', 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500', 450000, 1, 450000),
(2, 8, 'Nước hoa Chanel No.5', 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=500', 3500000, 1, 3500000),
(3, 10, 'Serum La Roche-Posay Hyalu B5', 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500', 850000, 1, 850000),
(4, 11, 'Serum Innisfree Vitamin C', 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=500', 520000, 1, 520000);

-- ====================================================
-- HOÀN TẤT
-- ====================================================
SELECT 'Database web2 đã được khởi tạo thành công!' AS status;
SELECT COUNT(*) AS total_users FROM users;
SELECT COUNT(*) AS total_brands FROM brands;
SELECT COUNT(*) AS total_categories FROM categories;
SELECT COUNT(*) AS total_products FROM products;
SELECT COUNT(*) AS total_news FROM news;
SELECT COUNT(*) AS total_orders FROM orders;
