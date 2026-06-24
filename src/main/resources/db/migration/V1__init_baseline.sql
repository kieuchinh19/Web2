-- =============================================================
-- V1: Baseline - Đánh dấu schema hiện tại đã tồn tại.
-- Flyway sẽ không chạy lại nội dung trong file này nếu dùng
-- 'baseline-on-migrate=true'. File chỉ để chốt version đầu tiên.
-- =============================================================

-- Không có DDL. Spring sẽ tự tạo baseline_version = 1.
SELECT 1;
