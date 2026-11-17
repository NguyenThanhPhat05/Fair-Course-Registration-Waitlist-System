USE DangKyHocPhan;
GO

-- 1. Sửa bảng students
ALTER TABLE students ALTER COLUMN full_name NVARCHAR(255) NOT NULL;
ALTER TABLE students ALTER COLUMN student_code VARCHAR(255) NOT NULL;

-- 2. Sửa bảng course_sections
ALTER TABLE course_sections ALTER COLUMN section_code VARCHAR(255) NOT NULL;
ALTER TABLE course_sections ALTER COLUMN course_name NVARCHAR(255) NOT NULL;
ALTER TABLE course_sections ALTER COLUMN lecturer_name NVARCHAR(255) NOT NULL;
ALTER TABLE course_sections ALTER COLUMN room_name NVARCHAR(255) NOT NULL;

-- 3. Sửa bảng registrations
ALTER TABLE registrations ALTER COLUMN status VARCHAR(255) NOT NULL;

-- 4. Sửa bảng audit_logs
ALTER TABLE audit_logs ALTER COLUMN action_type VARCHAR(255) NOT NULL;
ALTER TABLE audit_logs ALTER COLUMN reason_details NVARCHAR(MAX) NOT NULL;