-- ============================================================
-- 1. Tạo bảng membership
-- ============================================================
CREATE TABLE IF NOT EXISTS membership (
  id            INT PRIMARY KEY AUTO_INCREMENT,
  user_id       INT NOT NULL UNIQUE,
  tier          ENUM('REGULAR','SILVER','GOLD','PLATINUM') NOT NULL DEFAULT 'REGULAR',
  total_spent   DECIMAL(15,2) NOT NULL DEFAULT 0,
  since         DATETIME,
  CONSTRAINT fk_membership_user FOREIGN KEY (user_id) REFERENCES user(UserID) ON DELETE CASCADE
);

-- ============================================================
-- 2. Tạo membership REGULAR cho tất cả user hiện có
--    (Những user đăng ký trước khi có tính năng này)
-- ============================================================
INSERT INTO membership (user_id, tier, total_spent, since)
SELECT UserID, 'REGULAR', 0, NOW()
FROM user
WHERE UserID NOT IN (SELECT user_id FROM membership);

-- ============================================================
-- 3. Thêm permissions còn thiếu cho WAREHOUSE_STAFF
--    (Nếu chưa có VIEW_PRODUCT / UPDATE_STOCK thì chạy phần này)
-- ============================================================
-- INSERT IGNORE INTO permissions (name, description) VALUES
-- ('VIEW_PRODUCT', 'Xem danh sách sản phẩm');
-- INSERT IGNORE INTO role_permission (role_name, permission_name) VALUES
-- ('WAREHOUSE_STAFF', 'VIEW_PRODUCT');
