USE DangKyHocPhan;
GO

-- 1. Đảm bảo tất cả các cột văn bản chuyển sang NVARCHAR để hỗ trợ Unicode tuyệt đối
ALTER TABLE students ALTER COLUMN full_name NVARCHAR(255) NOT NULL;
ALTER TABLE course_sections ALTER COLUMN course_name NVARCHAR(255) NOT NULL;
ALTER TABLE course_sections ALTER COLUMN lecturer_name NVARCHAR(255) NOT NULL;
ALTER TABLE course_sections ALTER COLUMN room_name NVARCHAR(255) NOT NULL;
ALTER TABLE audit_logs ALTER COLUMN reason_details NVARCHAR(MAX) NOT NULL;
GO

-- 2. Xóa sạch lịch sử cũ đang bị lỗi phông để làm mới từ đầu
DELETE FROM audit_logs;
DELETE FROM waitlists;
DELETE FROM registrations;
GO

-- 3. Cập nhật lại toàn bộ tên Sinh viên chính xác có dấu
UPDATE students SET full_name = N'Nguyễn Thành Phát' WHERE student_code = 'SV01';
UPDATE students SET full_name = N'Nguyễn Văn Hoàng Nhật' WHERE student_code = 'SV02';
UPDATE students SET full_name = N'Trần Thị Bảo Ngọc' WHERE student_code = 'SV03';
UPDATE students SET full_name = N'Lê Minh Hoàng' WHERE student_code = 'SV04';
UPDATE students SET full_name = N'Phạm Thị Mai Linh' WHERE student_code = 'SV05';
UPDATE students SET full_name = N'Hoàng Quốc Việt' WHERE student_code = 'SV06';
UPDATE students SET full_name = N'Đỗ Minh Quân' WHERE student_code = 'SV07';
UPDATE students SET full_name = N'Bùi Thị Thanh Hằng' WHERE student_code = 'SV08';
UPDATE students SET full_name = N'Vũ Đức Anh' WHERE student_code = 'SV09';
UPDATE students SET full_name = N'Ngô Thị Quỳnh Như' WHERE student_code = 'SV10';
UPDATE students SET full_name = N'Dương Tấn Tài' WHERE student_code = 'SV11';
UPDATE students SET full_name = N'Lương Gia Hân' WHERE student_code = 'SV12';
UPDATE students SET full_name = N'Quản Trị Viên Đào Tạo' WHERE student_code = 'ADMIN';
GO

-- 4. Cập nhật lại danh sách Lớp học phần chuẩn tiếng Việt
UPDATE course_sections SET course_name = N'Thiết kế Hệ thống Hướng đối tượng', lecturer_name = N'ThS. Đinh Thị Hồng Loan' WHERE section_code = 'SE301.L01';
UPDATE course_sections SET course_name = N'Lập trình Web nâng cao (React & Spring Boot)', lecturer_name = N'TS. Nguyễn Văn An' WHERE section_code = 'WEB202.L01';
UPDATE course_sections SET course_name = N'Trí tuệ nhân tạo và Học máy', lecturer_name = N'PGS.TS. Trần Văn Bình' WHERE section_code = 'AI401.L01';
UPDATE course_sections SET course_name = N'Hệ quản trị Cơ sở dữ liệu nâng cao', lecturer_name = N'ThS. Lê Thị Hồng' WHERE section_code = 'DB101.L02';
UPDATE course_sections SET course_name = N'Phát triển ứng dụng di động đa nền tảng', lecturer_name = N'ThS. Phạm Hoàng Long' WHERE section_code = 'MOB303.L01';
UPDATE course_sections SET course_name = N'Lập trình Mạng nâng cao', lecturer_name = N'TS. Hoàng Minh Tuấn' WHERE section_code = 'NET302.L01';
UPDATE course_sections SET course_name = N'An toàn và Bảo mật Hệ thống thông tin', lecturer_name = N'PGS.TS. Trần Quốc Bảo' WHERE section_code = 'SEC401.L01';
UPDATE course_sections SET course_name = N'Kiểm thử và Đảm bảo Chất lượng Phần mềm', lecturer_name = N'ThS. Đinh Thị Hồng Loan' WHERE section_code = 'SE402.L01';
UPDATE course_sections SET course_name = N'Khai phá Dữ liệu và Big Data', lecturer_name = N'TS. Nguyễn Văn Nam' WHERE section_code = 'DATA501.L01';
UPDATE course_sections SET course_name = N'Quản lý Dự án Phần mềm', lecturer_name = N'ThS. Lê Hoàng Yến' WHERE section_code = 'PM301.L01';
GO