USE DangKyHocPhan;
GO

-- 1. Thêm danh sách nhiều sinh viên với các mức GPA, Tín chỉ và năm cuối khác nhau
-- (Giúp test thuật toán Tie-breaker / Điểm ưu tiên phong phú hơn)
INSERT INTO students (student_code, full_name, password, role, gpa, total_credits, is_senior) VALUES 
('SV01', N'Nguyễn Thành Phát', '123', 'STUDENT', 3.8, 120, 1),
('SV02', N'Nguyễn Văn Hoàng Nhật', '123', 'STUDENT', 3.2, 90, 0),
('SV03', N'Trần Thị Bảo Ngọc', '123', 'STUDENT', 3.9, 130, 1),
('SV04', N'Lê Minh Hoàng', '123', 'STUDENT', 2.8, 75, 0),
('SV05', N'Phạm Thị Mai Linh', '123', 'STUDENT', 3.5, 110, 1),
('SV06', N'Hoàng Quốc Việt', '123', 'STUDENT', 3.1, 85, 0),
('ADMIN', N'Quản Trị Viên Đào Tạo', 'admin123', 'ADMIN', 4.0, 150, 1);
GO

-- 2. Thêm nhiều lớp học phần với sức chứa (capacity) nhỏ (để dễ test đầy lớp và vào Waitlist)
INSERT INTO course_sections (section_code, course_name, lecturer_name, room_name, capacity, enrolled_count) VALUES 
('SE301.L01', N'Thiết kế Hệ thống Hướng đối tượng', N'ThS. Đinh Thị Hồng Loan', 'A10.02', 1, 0),
('WEB202.L01', N'Lập trình Web nâng cao (React & Spring Boot)', N'TS. Nguyễn Văn An', 'B20.05', 2, 0),
('AI401.L01', N'Trí tuệ nhân tạo và Học máy', N'PGS.TS. Trần Văn Bình', 'C30.01', 1, 0),
('DB101.L02', N'Hệ quản trị Cơ sở dữ liệu nâng cao', N'ThS. Lê Thị Hồng', 'A10.04', 3, 0),
('MOB303.L01', N'Phát triển ứng dụng di động đa nền tảng', N'ThS. Phạm Hoàng Long', 'B20.08', 2, 0);
GO


USE DangKyHocPhan;
GO

-- 1. Thêm danh sách sinh viên bổ sung (Đa dạng GPA, tín chỉ và năm cuối)
INSERT INTO students (student_code, full_name, password, role, gpa, total_credits, is_senior) VALUES 
('SV07', N'Đỗ Minh Quân', '123', 'STUDENT', 3.6, 115, 1),
('SV08', N'Bùi Thị Thanh Hằng', '123', 'STUDENT', 2.9, 80, 0),
('SV09', N'Vũ Đức Anh', '123', 'STUDENT', 3.4, 105, 1),
('SV10', N'Ngô Thị Quỳnh Như', '123', 'STUDENT', 3.7, 125, 1),
('SV11', N'Dương Tấn Tài', '123', 'STUDENT', 2.5, 60, 0),
('SV12', N'Lương Gia Hân', '123', 'STUDENT', 3.3, 95, 0);
GO

-- 2. Thêm danh sách lớp học phần mới với sức chứa giới hạn (capacity nhỏ để dễ test Waitlist)
INSERT INTO course_sections (section_code, course_name, lecturer_name, room_name, capacity, enrolled_count) VALUES 
('NET302.L01', N'Lập trình Mạng nâng cao', N'TS. Hoàng Minh Tuấn', 'B10.01', 2, 0),
('SEC401.L01', N'An toàn và Bảo mật Hệ thống thông tin', N'PGS.TS. Trần Quốc Bảo', 'C20.03', 1, 0),
('SE402.L01', N'Kiểm thử và Đảm bảo Chất lượng Phần mềm', N'ThS. Đinh Thị Hồng Loan', 'A10.05', 2, 0),
('DATA501.L01', N'Khai phá Dữ liệu và Big Data', N'TS. Nguyễn Văn Nam', 'D10.02', 3, 0),
('PM301.L01', N'Quản lý Dự án Phần mềm', N'ThS. Lê Hoàng Yến', 'B20.04', 2, 0);
GO