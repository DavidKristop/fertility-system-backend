-- Cho phép NULL trong schedule_id
ALTER TABLE request_appointment
ALTER COLUMN schedule_id DROP NOT NULL;

-- Thêm cột appointment_datetime để ghi lại thời gian khách hàng muốn gặp
ALTER TABLE request_appointment
ADD COLUMN appointment_datetime TIMESTAMP NOT NULL;

ALTER TABLE request_appointment
DROP CONSTRAINT request_appointment_status_check;

ALTER TABLE request_appointment
ADD CONSTRAINT request_appointment_status_check
CHECK (status IN ('Accept', 'Denied', 'Pending'));