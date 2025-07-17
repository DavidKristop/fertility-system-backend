-- Initialize Roles
INSERT INTO role (id, name) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174000', 'ROLE_ADMIN'),
    ('123e4567-e89b-12d3-a456-426614174001', 'ROLE_DOCTOR'),
    ('123e4567-e89b-12d3-a456-426614174002', 'ROLE_PATIENT'),
    ('123e4567-e89b-12d3-a456-426614174003', 'ROLE_MANAGER')
ON CONFLICT DO NOTHING;

-- Initialize a sample patient
INSERT INTO users (id, email, full_name, date_of_birth, password_hashed, password_secret, role_id, is_verify) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174004', 'patient@example.com', 'John Doe', '1990-01-01', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'patient123secret', '123e4567-e89b-12d3-a456-426614174002', true)
ON CONFLICT DO NOTHING;

-- Initialize a sample doctor
INSERT INTO users (id, email, full_name, date_of_birth, password_hashed, password_secret, role_id, is_verify) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174005', 'doctor@example.com', 'Dr. Smith', '1980-01-01', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'doctor123secret', '123e4567-e89b-12d3-a456-426614174001', true)
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
INSERT INTO treatment_protocol_phase (id, title, description, position, treatment_protocol_id,phase_modifier_percentage,refund_percentage)
VALUES
    ('123e4567-e89b-12d3-a456-426614174011', 'Consultation Phase', 'Initial consultation with ultrasound scan', 1, '123e4567-e89b-12d3-a456-426614174010',1.00,0.30)
ON CONFLICT DO NOTHING;

-- Initialize protocol services
INSERT INTO treatment_protocol_service (id, service_id, treatment_protocol_phase_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174012', '123e4567-e89b-12d3-a456-426614174008', '123e4567-e89b-12d3-a456-426614174011'),
    ('123e4567-e89b-12d3-a456-426614174013', '123e4567-e89b-12d3-a456-426614174009', '123e4567-e89b-12d3-a456-426614174011')
ON CONFLICT DO NOTHING;

-- Initialize Standard IVF Protocol
INSERT INTO treatment_protocol (id, title, description, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174014', 'Standard IVF Protocol', 'Standard IVF treatment protocol including stimulation, egg retrieval, and embryo transfer', true)
ON CONFLICT DO NOTHING;

-- Initialize IVF Protocol Phases
INSERT INTO treatment_protocol_phase (id, title, description, position, treatment_protocol_id, phase_modifier_percentage, refund_percentage)
VALUES
    ('123e4567-e89b-12d3-a456-426614174015', 'Stimulation Phase', 'Ovarian stimulation and monitoring', 1, '123e4567-e89b-12d3-a456-426614174014', 1.00, 0.50),
    ('123e4567-e89b-12d3-a456-426614174016', 'Egg Retrieval Phase', 'Egg retrieval procedure', 2, '123e4567-e89b-12d3-a456-426614174014', 1.00, 0.30),
    ('123e4567-e89b-12d3-a456-426614174017', 'Embryo Transfer Phase', 'Embryo transfer procedure', 3, '123e4567-e89b-12d3-a456-426614174014', 1.00, 0.20)
ON CONFLICT DO NOTHING;

-- Initialize IVF Protocol Services
INSERT INTO service (id, name, description, price, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174018', 'Ovarian Stimulation', 'Medication and monitoring for ovarian stimulation', 5000000.00, true),
    ('123e4567-e89b-12d3-a456-426614174019', 'Egg Retrieval', 'Procedure for egg retrieval', 30000000.00, true),
    ('123e4567-e89b-12d3-a456-426614174020', 'Embryo Transfer', 'Procedure for embryo transfer', 20000000.00, true)
ON CONFLICT DO NOTHING;

-- Initialize IVF Protocol Service Assignments
INSERT INTO treatment_protocol_service (id, service_id, treatment_protocol_phase_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174021', '123e4567-e89b-12d3-a456-426614174018', '123e4567-e89b-12d3-a456-426614174015'),
    ('123e4567-e89b-12d3-a456-426614174022', '123e4567-e89b-12d3-a456-426614174019', '123e4567-e89b-12d3-a456-426614174016'),
    ('123e4567-e89b-12d3-a456-426614174023', '123e4567-e89b-12d3-a456-426614174020', '123e4567-e89b-12d3-a456-426614174017')
ON CONFLICT DO NOTHING;

-- Initialize IUI Protocol
INSERT INTO treatment_protocol (id, title, description, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174030', 'IUI Protocol', 'Intrauterine Insemination treatment protocol', true)
ON CONFLICT DO NOTHING;

-- Initialize IUI Protocol Phases
INSERT INTO treatment_protocol_phase (id, title, description, position, treatment_protocol_id, phase_modifier_percentage, refund_percentage)
VALUES
    ('123e4567-e89b-12d3-a456-426614174031', 'IUI Stimulation Phase', 'Ovarian stimulation for IUI', 1, '123e4567-e89b-12d3-a456-426614174030', 1.00, 0.50),
    ('123e4567-e89b-12d3-a456-426614174032', 'IUI Procedure Phase', 'IUI procedure', 2, '123e4567-e89b-12d3-a456-426614174030', 1.00, 0.30)
ON CONFLICT DO NOTHING;

-- Initialize IUI Protocol Services
INSERT INTO service (id, name, description, price, is_active) 
VALUES 
    ('123e4567-e89b-12d3-a456-426614174033', 'IUI Procedure', 'Intrauterine insemination procedure', 20000000.00, true)
ON CONFLICT DO NOTHING;

-- Initialize IUI Protocol Service Assignments
INSERT INTO treatment_protocol_service (id, service_id, treatment_protocol_phase_id)
VALUES
    ('123e4567-e89b-12d3-a456-426614174034', '123e4567-e89b-12d3-a456-426614174018', '123e4567-e89b-12d3-a456-426614174031'),
    ('123e4567-e89b-12d3-a456-426614174035', '123e4567-e89b-12d3-a456-426614174033', '123e4567-e89b-12d3-a456-426614174032')
ON CONFLICT DO NOTHING;

