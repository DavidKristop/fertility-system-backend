-- Add sample services and drugs

-- Services
INSERT INTO service (id, name, description, price, unit, is_active) VALUES
('6499268d-7f84-4f88-9f6c-09c8d9b4c2a1', 'IVF', 'Kỹ thuật thụ tinh trong ống nghiệm', 5000.00, 'buổi', TRUE),
('6499268d-7f84-4f88-9f6c-09c8d9b4c2a2', 'IUI', 'Kỹ thuật bơm tinh trùng vào tử cung', 1500.00, 'buổi', TRUE),
('6499268d-7f84-4f88-9f6c-09c8d9b4c2a3', 'Kích Thích Buồng Trứng', 'Dùng thuốc kích thích buồng trứng', 800.00, 'chu kỳ', TRUE),
('6499268d-7f84-4f88-9f6c-09c8d9b4c2a4', 'Di Chuyển Phôi', 'Di chuyển phôi vào tử cung', 1200.00, 'buổi', TRUE),
('6499268d-7f84-4f88-9f6c-09c8d9b4c2a5', 'Phân Tích Tinh Trùng', 'Phân tích chất lượng tinh trùng', 150.00, 'lần', TRUE),
('6499268d-7f84-4f88-9f6c-09c8d9b4c2a6', 'Kiểm Tra Hormone', 'Kiểm tra các hormone sinh sản', 200.00, 'lần', TRUE);

-- Drugs
INSERT INTO drug (id, name, description, price, unit, is_active) VALUES
('6499268d-7f84-4f88-9f6c-09c8d9b4c2a7', 'Follistim', 'Thuốc kích thích buồng trứng', 250.00, 'lọ', TRUE),
('6499268d-7f84-4f88-9f6c-09c8d9b4c2a8', 'Gonal-f', 'Thuốc kích thích buồng trứng', 200.00, 'lọ', TRUE),
('6499268d-7f84-4f88-9f6c-09c8d9b4c2a9', 'Ovidrel', 'Thuốc hỗ trợ rụng trứng', 150.00, 'lọ', TRUE),
('6499268d-7f84-4f88-9f6c-09c8d9b4c2aa', 'Progesterone', 'Thuốc hỗ trợ mang thai', 50.00, 'viên', TRUE),
('6499268d-7f84-4f88-9f6c-09c8d9b4c2ab', 'Clomid', 'Thuốc kích thích rụng trứng', 30.00, 'viên', TRUE),
('6499268d-7f84-4f88-9f6c-09c8d9b4c2ac', 'Letrozole', 'Thuốc kích thích rụng trứng', 40.00, 'viên', TRUE);
