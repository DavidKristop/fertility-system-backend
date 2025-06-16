-- Drop all tables in reverse dependency order

-- Drop tables with no dependencies first
DROP TABLE IF EXISTS blog_attachment;
DROP TABLE IF EXISTS schedule_result_attachment;
DROP TABLE IF EXISTS patient_drug;
DROP TABLE IF EXISTS schedule_service;

-- Drop tables with single dependencies
DROP TABLE IF EXISTS blog;

-- Drop user_info table (after blog since blog depends on it)
DROP TABLE IF EXISTS user_info;

-- Drop remaining tables with single dependencies
DROP TABLE IF EXISTS schedule_result;
DROP TABLE IF EXISTS refund;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS contract;

-- Drop tables with multiple dependencies
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS payment_history;

-- Drop tables with user dependencies
DROP TABLE IF EXISTS treatment;
DROP TABLE IF EXISTS schedule;
DROP TABLE IF EXISTS doctor_profile;
DROP TABLE IF EXISTS patient_profile;

-- Drop remaining tables
DROP TABLE IF EXISTS service;
DROP TABLE IF EXISTS drug;
DROP TABLE IF EXISTS role;
DROP TABLE IF EXISTS "user";


-- Drop tables with user dependencies
DROP TABLE IF EXISTS treatment;
DROP TABLE IF EXISTS schedule;
DROP TABLE IF EXISTS doctor_profile;
DROP TABLE IF EXISTS patient_profile;
