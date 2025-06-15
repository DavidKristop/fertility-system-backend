-- Role table
CREATE TABLE role (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- User table
CREATE TABLE "user" (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    address VARCHAR(255),
    password_hashed VARCHAR(255) NOT NULL,
    password_secret VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(255),
    role_id UUID NOT NULL,
    FOREIGN KEY (role_id) REFERENCES role(id)
);

-- Patient Profile table
CREATE TABLE patient_profile (
    id UUID PRIMARY KEY,
    medical_history TEXT,
    user_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user"(id)
);

-- Doctor Profile table
CREATE TABLE doctor_profile (
    id UUID PRIMARY KEY,
    specialty VARCHAR(255),
    degree VARCHAR(255),
    years_of_experience DECIMAL(5,2),
    license_number VARCHAR(100),
    user_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user"(id)
);

-- Schedule table
CREATE TABLE schedule (
    id UUID PRIMARY KEY,
    doctor_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    appointment_datetime TIMESTAMP NOT NULL,
    estimated_time TIMESTAMP,
    status VARCHAR(50) CHECK (status IN ('Pending', 'Changed', 'Done')),
    FOREIGN KEY (doctor_id) REFERENCES "user"(id),
    FOREIGN KEY (patient_id) REFERENCES "user"(id)
);

-- Schedule Result table
CREATE TABLE schedule_result (
    id UUID PRIMARY KEY,
    doctors_note TEXT,
    schedule_id UUID NOT NULL,
    FOREIGN KEY (schedule_id) REFERENCES schedule(id)
);

-- Schedule Result Attachment table
CREATE TABLE schedule_result_attachment (
    id UUID PRIMARY KEY,
    attachment_url VARCHAR(255) NOT NULL,
    schedule_result_id UUID NOT NULL,
    FOREIGN KEY (schedule_result_id) REFERENCES schedule_result(id)
);

-- Request Appointment table
CREATE TABLE request_appointment (
    id UUID PRIMARY KEY,
    doctor_id UUID NOT NULL,
    patient_id UUID NOT NULL,
    schedule_id UUID NOT NULL,
    reason TEXT,
    status VARCHAR(20) CHECK (status IN ('Accept', 'Denied')),
    FOREIGN KEY (doctor_id) REFERENCES "user"(id),
    FOREIGN KEY (patient_id) REFERENCES "user"(id),
    FOREIGN KEY (schedule_id) REFERENCES schedule(id)
);

-- Treatment table
CREATE TABLE treatment (
    id UUID PRIMARY KEY,
    start_date DATE,
    end_date DATE,
    diagnosis TEXT,
    total_amount DECIMAL(10,2),
    status VARCHAR(20) CHECK (status IN ('Cancel', 'In Progress', 'Complete')),
    user_id UUID NOT NULL,
    doctor_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user"(id),
    FOREIGN KEY (doctor_id) REFERENCES "user"(id)
);

-- Treatment Phase table
CREATE TABLE treatment_phase (
    id UUID PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    total_amount DECIMAL(10,2),
    treatment_id UUID NOT NULL,
    refund_condition TEXT,
    refund_amount DECIMAL(10,2),
    FOREIGN KEY (treatment_id) REFERENCES treatment(id)
);

-- Drug table
CREATE TABLE drug (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    is_active BOOLEAN,
    unit VARCHAR(50)
);

-- Patient Drug table
CREATE TABLE patient_drug (
    id SERIAL PRIMARY KEY,
    drug_id UUID NOT NULL,
    usage_instructions TEXT,
    start_date DATE,
    end_date DATE,
    dosage VARCHAR(100),
    treatment_phase_id UUID,
    FOREIGN KEY (drug_id) REFERENCES drug(id),
    FOREIGN KEY (treatment_phase_id) REFERENCES treatment_phase(id)
);

-- Service table
CREATE TABLE service (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    unit VARCHAR(50),
    is_active BOOLEAN
);

-- Schedule Service table
CREATE TABLE schedule_service (
    id SERIAL PRIMARY KEY,
    schedule_id UUID,
    service_id UUID NOT NULL,
    notes TEXT,
    amount INTEGER,
    treatment_phase_id UUID NOT NULL,
    FOREIGN KEY (schedule_id) REFERENCES schedule(id),
    FOREIGN KEY (service_id) REFERENCES service(id),
    FOREIGN KEY (treatment_phase_id) REFERENCES treatment_phase(id)
);

-- Contract table
CREATE TABLE contract (
    id UUID PRIMARY KEY,
    is_signed BOOLEAN,
    sign_deadline TIMESTAMP,
    treatment_id UUID NOT NULL,
    contract_url VARCHAR(255),
    FOREIGN KEY (treatment_id) REFERENCES treatment(id)
);

-- Blog table
CREATE TABLE blog (
    id UUID PRIMARY KEY,
    content TEXT,
    title VARCHAR(255) NOT NULL,
    thumbnail_url VARCHAR(255),
    author_id UUID NOT NULL,
    FOREIGN KEY (author_id) REFERENCES "user"(id)
);

-- Blog Attachment table
CREATE TABLE blog_attachment (
    id UUID PRIMARY KEY,
    blog_id UUID NOT NULL,
    attachment_url VARCHAR(255) NOT NULL,
    FOREIGN KEY (blog_id) REFERENCES blog(id)
);

-- Feedback table
CREATE TABLE feedback (
    id UUID PRIMARY KEY,
    content TEXT,
    treatment_id UUID NOT NULL,
    user_id UUID NOT NULL,
    FOREIGN KEY (treatment_id) REFERENCES treatment(id),
    FOREIGN KEY (user_id) REFERENCES "user"(id)
);

-- Payment History table
CREATE TABLE payment_history (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user"(id)
);

-- Payment table
CREATE TABLE payment (
    id UUID PRIMARY KEY,
    amount DECIMAL(10,2),
    description TEXT,
    payment_date TIMESTAMP,
    payment_deadline TIMESTAMP,
    payment_method VARCHAR(50),
    status VARCHAR(50),
    payment_history_id UUID NOT NULL,
    treatment_phase_id UUID NOT NULL,
    FOREIGN KEY (payment_history_id) REFERENCES payment_history(id),
    FOREIGN KEY (treatment_phase_id) REFERENCES treatment_phase(id)
);

-- Refund table
CREATE TABLE refund (
    id UUID PRIMARY KEY,
    amount DECIMAL(10,2),
    refund_date TIMESTAMP,
    refund_method VARCHAR(50),
    payment_history_id UUID NOT NULL,
    treatment_phase_id UUID NOT NULL,
    FOREIGN KEY (payment_history_id) REFERENCES payment_history(id),
    FOREIGN KEY (treatment_phase_id) REFERENCES treatment_phase(id)
);