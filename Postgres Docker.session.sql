-- SELECT * FROM users;
-- SELECT * FROM role;
-- SELECT * FROM patient_profile;
-- SELECT * FROM treatment;
-- SELECT * FROM contract
-- SELECT * FROM doctor_profile;



-- tạo treatment mẫu
-- INSERT INTO treatment (
--     id, start_date, end_date, diagnosis, total_amount, status, user_id, doctor_id
-- ) VALUES (
--     'cccccccc-cccc-cccc-cccc-cccccccccccc',
--     '2025-06-01',
--     '2025-06-30',
--     'Infertility diagnosis',
--     5000.00,
--     'In Progress',
--     '5bf08b23-112e-44ab-87e4-54b612dacc27',  -- patient_id
--     'd7f25fda-3f57-400b-8695-ff01d30a2ed2'   -- doctor_id
-- );


-- đổi role của một user thành doctor
-- UPDATE users
-- SET role_id = '29342bd3-d107-4981-a0a8-9df63b215c2a'
-- WHERE id = 'd7f25fda-3f57-400b-8695-ff01d30a2ed2';


-- INSERT INTO patient_profile (id, medical_history, user_id)
-- VALUES (
--     'cccccccc-cccc-cccc-cccc-cccccccccccc',
--     'No major issues',
--     '33d16118-2144-4165-b8fb-5970ec91d838'
-- );


-- INSERT INTO doctor_profile (id, specialty, degree, years_of_experience, license_number, user_id)
-- VALUES (
--     'dddddddd-dddd-dddd-dddd-dddddddddddd',
--     'Cardiology', 'MD', 10.0, 'LIC123456',
--     'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'
-- );