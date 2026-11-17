USE DangKyHocPhan;
GO

-- Cập nhật cột password và role cho sinh viên
ALTER TABLE students ADD password VARCHAR(100) DEFAULT '123456';
ALTER TABLE students ADD role VARCHAR(20) DEFAULT 'STUDENT';
GO

UPDATE students SET password = '123', role = 'STUDENT' WHERE student_code = 'SV01';
UPDATE students SET password = '123', role = 'STUDENT' WHERE student_code = 'SV02';
UPDATE students SET password = '123', role = 'STUDENT' WHERE student_code = 'SV03';
UPDATE students SET password = '123', role = 'STUDENT' WHERE student_code = 'SV04';

-- Thêm tài khoản Admin
INSERT INTO students (student_code, full_name, password, role, gpa, total_credits, is_senior) 
VALUES ('ADMIN', N'Quản Trị Viên Đào Tạo', 'admin123', 'ADMIN', 4.0, 150, 1);

USE DangKyHocPhan;
GO

-- Thêm tài khoản Giảng viên mẫu
INSERT INTO students (student_code, full_name, password, role, gpa, total_credits, is_senior) VALUES 
('GV01', N'TS. Hoàng Minh Tuấn', '123', 'LECTURER', 4.0, 0, 1);
GO