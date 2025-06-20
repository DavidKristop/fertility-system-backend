
ALTER TABLE request_appointment
DROP CONSTRAINT IF EXISTS request_appointment_status_check;

ALTER TABLE request_appointment
ADD CONSTRAINT request_appointment_status_check
CHECK (status IN ('Accept', 'Denied', 'Pending'));