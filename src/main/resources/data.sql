-- Khởi tạo các vai trò
INSERT INTO role (id, name) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174000', 'ROLE_ADMIN'),
    ('123e4567-e89b-12d3-a456-426614174001', 'ROLE_DOCTOR'),
    ('123e4567-e89b-12d3-a456-426614174002', 'ROLE_PATIENT'),
    ('123e4567-e89b-12d3-a456-426614174003', 'ROLE_MANAGER'),
    ('123e4567-e89b-12d3-a456-426614174036', 'ROLE_STAFF') 
ON CONFLICT DO NOTHING;

-- Khởi tạo một bệnh nhân mẫu
INSERT INTO users (id, email, full_name, date_of_birth, password_hashed, password_secret, role_id, is_verify, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174004', 'benhnhan@example.com', 'Nguyễn Văn A', '1990-01-01', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'benhnhan123', '123e4567-e89b-12d3-a456-426614174002', true, true),
    ('123e4567-e89b-12d3-a456-426614174037', 'quanly@example.com', 'Nguyễn Thị C', '1985-01-01', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'quanly123', '123e4567-e89b-12d3-a456-426614174003', true, true),
    ('123e4567-e89b-12d3-a456-426614174038', 'admin@example.com', 'Admin', '1995-01-01', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin123', '123e4567-e89b-12d3-a456-426614174000', true, true),
    ('123e4567-e89b-12d3-a456-426614174039', 'nhanvien@example.com', 'Nguyễn Văn D', '1992-01-01', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'nhanvien123', '123e4567-e89b-12d3-a456-426614174036', true, true)
ON CONFLICT DO NOTHING;

-- Khởi tạo một bác sĩ mẫu
INSERT INTO users (id, email, full_name, date_of_birth, password_hashed, password_secret, role_id,  is_verify, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174005', 'bacsi@example.com', 'Bác sĩ Nguyễn Thị B', '1980-01-01', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'bacsi123', '123e4567-e89b-12d3-a456-426614174001', true, true)
ON CONFLICT DO NOTHING;

-- Khởi tạo các loại thuốc mẫu
INSERT INTO drug (id, name, description, unit, price, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174006', 'Clomiphene', 'Thuốc hỗ trợ sinh sản', 'mg', 100000.00, true),
    ('123e4567-e89b-12d3-a456-426614174007', 'Letrozole', 'Thuốc hỗ trợ sinh sản', 'mg', 200000.00, true)
ON CONFLICT DO NOTHING;

-- Khởi tạo các dịch vụ mẫu
INSERT INTO service (id, name, description, price, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174008', 'Tư vấn', 'Dịch vụ tư vấn y tế', 0.00, true),
    ('123e4567-e89b-12d3-a456-426614174009', 'Siêu âm', 'Dịch vụ siêu âm y tế', 250000.00, true)
ON CONFLICT DO NOTHING;

-- Khởi tạo giao thức IVF tiêu chuẩn
INSERT INTO treatment_protocol (id, title, description, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174014', 'Giao thức IVF tiêu chuẩn', 'Giao thức điều trị IVF', true)
ON CONFLICT DO NOTHING;

-- Khởi tạo các giai đoạn của giao thức IVF
INSERT INTO treatment_protocol_phase (id, title, description, position, treatment_protocol_id, phase_modifier_percentage, refund_percentage)
VALUES
    ('123e4567-e89b-12d3-a456-426614174015', 'Kích thích', 'Kích thích buồng trứng', 1, '123e4567-e89b-12d3-a456-426614174014', 1.00, 0.50),
    ('123e4567-e89b-12d3-a456-426614174016', 'Lấy trứng', 'Thủ thuật lấy trứng', 2, '123e4567-e89b-12d3-a456-426614174014', 1.00, 0.30),
    ('123e4567-e89b-12d3-a456-426614174017', 'Chuyển phôi', 'Chuyển phôi vào tử cung', 3, '123e4567-e89b-12d3-a456-426614174014', 1.00, 0.20)
ON CONFLICT DO NOTHING;

-- Khởi tạo các dịch vụ của giao thức IVF
INSERT INTO service (id, name, description, price, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174018', 'Kích thích buồng trứng', 'Thuốc và theo dõi kích thích', 5000000.00, true),
    ('123e4567-e89b-12d3-a456-426614174019', 'Lấy trứng', 'Thủ thuật lấy trứng', 30000000.00, true),
    ('123e4567-e89b-12d3-a456-426614174020', 'Chuyển phôi', 'Chuyển phôi vào tử cung', 20000000.00, true)
ON CONFLICT DO NOTHING;

-- Khởi tạo các loại thuốc cho giao thức IVF
INSERT INTO treatment_protocol_drug (id, treatment_protocol_phase_id, drug_id, amount)
VALUES
    ('123e4567-e89b-12d3-a456-426614174045', '123e4567-e89b-12d3-a456-426614174015', '123e4567-e89b-12d3-a456-426614174006', 150),
    ('123e4567-e89b-12d3-a456-426614174046', '123e4567-e89b-12d3-a456-426614174015', '123e4567-e89b-12d3-a456-426614174007', 2.5)
ON CONFLICT DO NOTHING;

-- Initialize IVF Protocol Service Assignments
INSERT INTO treatment_protocol_service (id, service_id, treatment_protocol_phase_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174021', '123e4567-e89b-12d3-a456-426614174018', '123e4567-e89b-12d3-a456-426614174015'),
    ('123e4567-e89b-12d3-a456-426614174022', '123e4567-e89b-12d3-a456-426614174019', '123e4567-e89b-12d3-a456-426614174016'),
    ('123e4567-e89b-12d3-a456-426614174023', '123e4567-e89b-12d3-a456-426614174020', '123e4567-e89b-12d3-a456-426614174017')
ON CONFLICT DO NOTHING;

-- Khởi tạo giao thức IUI
INSERT INTO treatment_protocol (id, title, description, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174030', 'Giao thức IUI', 'Giao thức IUI', true)
ON CONFLICT DO NOTHING;

-- Khởi tạo các giai đoạn của giao thức IUI
INSERT INTO treatment_protocol_phase (id, title, description, position, treatment_protocol_id, phase_modifier_percentage, refund_percentage)
VALUES
    ('123e4567-e89b-12d3-a456-426614174031', 'Kích thích', 'Kích thích buồng trứng', 1, '123e4567-e89b-12d3-a456-426614174030', 1.00, 0.50),
    ('123e4567-e89b-12d3-a456-426614174032', 'Thụ tinh', 'Thụ tinh trong ống nghiệm', 2, '123e4567-e89b-12d3-a456-426614174030', 1.00, 0.30)
ON CONFLICT DO NOTHING;

-- Khởi tạo các dịch vụ của giao thức IUI
INSERT INTO service (id, name, description, price, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174033', 'IUI', 'Thụ tinh trong ống nghiệm', 20000000.00, true)
ON CONFLICT DO NOTHING;

-- Khởi tạo các loại thuốc cho giao thức IUI
INSERT INTO treatment_protocol_drug (id, treatment_protocol_phase_id, drug_id, amount)
VALUES
    ('123e4567-e89b-12d3-a456-426614174047', '123e4567-e89b-12d3-a456-426614174031', '123e4567-e89b-12d3-a456-426614174006', 100),
    ('123e4567-e89b-12d3-a456-426614174048', '123e4567-e89b-12d3-a456-426614174031', '123e4567-e89b-12d3-a456-426614174007', 2)
ON CONFLICT DO NOTHING;

-- Initialize IUI Protocol Service Assignments
INSERT INTO treatment_protocol_service (id, service_id, treatment_protocol_phase_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174034', '123e4567-e89b-12d3-a456-426614174033', '123e4567-e89b-12d3-a456-426614174031'),
    ('123e4567-e89b-12d3-a456-426614174035', '123e4567-e89b-12d3-a456-426614174033', '123e4567-e89b-12d3-a456-426614174032')
ON CONFLICT DO NOTHING;

