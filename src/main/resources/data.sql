-- Initialize Roles
INSERT INTO role (id, name) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174000', 'ROLE_ADMIN'),
    ('123e4567-e89b-12d3-a456-426614174001', 'ROLE_DOCTOR'),
    ('123e4567-e89b-12d3-a456-426614174002', 'ROLE_PATIENT'),
    ('123e4567-e89b-12d3-a456-426614174003', 'ROLE_MANAGER')
ON CONFLICT DO NOTHING;

-- Initialize a sample patient
INSERT INTO users (id, email, full_name, date_of_birth, password_hashed, password_secret, role_id) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174004', 'patient@example.com', 'John Doe', '1990-01-01', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'patient123secret', '123e4567-e89b-12d3-a456-426614174002')
ON CONFLICT DO NOTHING;

-- Initialize a sample doctor
INSERT INTO users (id, email, full_name, date_of_birth, password_hashed, password_secret, role_id) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174005', 'doctor@example.com', 'Dr. Smith', '1980-01-01', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'doctor123secret', '123e4567-e89b-12d3-a456-426614174001')
ON CONFLICT DO NOTHING;

-- Initialize sample drugs
INSERT INTO drug (id, name, description, unit, price, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174006', 'Clomiphene', 'Fertility medication', 'mg', 100000.00, true),
    ('123e4567-e89b-12d3-a456-426614174007', 'Letrozole', 'Fertility medication', 'mg', 200000.00, true)
ON CONFLICT DO NOTHING;

-- Initialize sample services
INSERT INTO service (id, name, description, price, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174008', 'Consultation', 'Medical consultation service', 10000.00, true),
    ('123e4567-e89b-12d3-a456-426614174009', 'Ultrasound Scan', 'Medical ultrasound scanning service', 2500.00, true)
ON CONFLICT DO NOTHING;

-- Initialize consultation protocol
INSERT INTO treatment_protocol (id, title, description, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174010', 'Consultation Protocol', 'Standard consultation protocol including consultation and ultrasound scan', true)
ON CONFLICT DO NOTHING;

-- Initialize consultation protocol phase
INSERT INTO treatment_protocol_phase (id, title, description, total_amount, position, treatment_protocol_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174011', 'Consultation Phase', 'Initial consultation with ultrasound scan', 10000.00, 1, '123e4567-e89b-12d3-a456-426614174010')
ON CONFLICT DO NOTHING;

-- Initialize protocol services
INSERT INTO treatment_protocol_service (id, service_id, treatment_protocol_phase_id, amount)
VALUES
    ('123e4567-e89b-12d3-a456-426614174012', '123e4567-e89b-12d3-a456-426614174008', '123e4567-e89b-12d3-a456-426614174011',1),
    ('123e4567-e89b-12d3-a456-426614174013', '123e4567-e89b-12d3-a456-426614174009', '123e4567-e89b-12d3-a456-426614174011',1)
ON CONFLICT DO NOTHING;

-- Initialize sample treatment protocol
INSERT INTO treatment_protocol (id, title, description, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174010', 'Standard IVF Protocol', 'Standard IVF treatment protocol', true)
ON CONFLICT DO NOTHING;

