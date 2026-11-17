CREATE DATABASE DangKyHocPhan;
GO
USE DangKyHocPhan;
GO

-- 1. Bảng Sinh viên
CREATE TABLE students (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    student_code VARCHAR(20) NOT NULL UNIQUE,
    full_name NVARCHAR(120) NOT NULL,
    gpa DECIMAL(3,2) NOT NULL DEFAULT 0.0,
    total_credits INT NOT NULL DEFAULT 0,
    is_senior BIT NOT NULL DEFAULT 0 -- True nếu là sinh viên năm cuối
);

-- 2. Bảng Lớp học phần (Có tên giảng viên, phòng học, sức chứa)
CREATE TABLE course_sections (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    section_code VARCHAR(30) NOT NULL UNIQUE,
    course_name NVARCHAR(150) NOT NULL,
    lecturer_name NVARCHAR(120) NOT NULL,
    room_name NVARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    enrolled_count INT NOT NULL DEFAULT 0
);

-- 3. Bảng Đăng ký chính thức
CREATE TABLE registrations (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    student_id BIGINT NOT NULL,
    section_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL, -- 'ENROLLED', 'WAITLISTED'
    registered_at DATETIME2(7) NOT NULL,
    CONSTRAINT FK_Reg_Student FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT FK_Reg_Section FOREIGN KEY (section_id) REFERENCES course_sections(id)
);

-- 4. Bảng Danh sách chờ (Waitlist) với Index tối ưu công bằng
CREATE TABLE waitlists (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    section_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    joined_at DATETIME2(7) NOT NULL,
    priority_score DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    CONSTRAINT FK_Wait_Sec FOREIGN KEY (section_id) REFERENCES course_sections(id),
    CONSTRAINT FK_Wait_Stu FOREIGN KEY (student_id) REFERENCES students(id)
);

CREATE INDEX IX_Waitlist_Fairness 
ON waitlists (section_id, joined_at ASC, priority_score DESC);

-- 5. Bảng Nhật ký quyết định (Audit Log) minh bạch
CREATE TABLE audit_logs (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    student_id BIGINT NOT NULL,
    section_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    reason_details NVARCHAR(500) NOT NULL,
    created_at DATETIME2(7) NOT NULL
);

-- Thêm dữ liệu mẫu để test
INSERT INTO students (student_code, full_name, gpa, total_credits, is_senior) VALUES 
('SV01', N'Nguyễn Thành Phát', 3.8, 120, 1),
('SV02', N'Nguyễn Văn Hoàng Nhật', 3.2, 90, 0);

INSERT INTO course_sections (section_code, course_name, lecturer_name, room_name, capacity, enrolled_count) VALUES 
('SE301.L01', N'Thiết kế Hệ thống Hướng đối tượng', N'ThS. Đinh Thị Hồng Loan', N'A10.02', 1, 0);