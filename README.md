# 🎓 Fair Course Registration & Waitlist System

> Hệ thống đăng ký học phần trực tuyến tích hợp cơ chế xếp hàng chờ công bằng (Fair Waitlist) dựa trên điểm ưu tiên và xử lý tranh chấp đồng thời (Concurrency Control).

---

## 🚀 1. Giới thiệu dự án
Đồ án xây dựng hệ thống quản lý và đăng ký tín chỉ dành cho trường đại học, giải quyết các vấn đề quá tải hệ thống trong giờ cao điểm, đảm bảo phân bổ suất học phần minh bạch, công bằng dựa trên GPA, số tín chỉ tích lũy và đối tượng ưu tiên.

## 🧰 2. Cấu trúc Repository
- **`/backend`**: Chứa toàn bộ mã nguồn Spring Boot (Java), cấu hình Maven và các service xử lý nghiệp vụ.
- **`/database`**: Chứa file script SQL Server khởi tạo cơ sở dữ liệu và dữ liệu mẫu.
- **`/docs`**: Chứa báo cáo đồ án, slide thuyết trình và hình ảnh sơ đồ thiết kế hệ thống (ERD, Use Case vẽ từ draw.io).

## 🛠️ 3. Công nghệ sử dụng
- **Backend:** Java 25+, Spring Boot, Maven
- **Database:** Microsoft SQL Server
- **Frontend:** HTML5, CSS3, JavaScript
- **Bảo mật & Toàn vẹn:** SHA-256 Hash-chain Audit Logging, Optimistic Locking

## ⚙️ 4. Hướng dẫn cài đặt và chạy chương trình
1. **Clone repository:**
   ```bash
   git clone [https://github.com/NguyenThanhPhat05/Fair-Course-Registration-Waitlist-System.git](https://github.com/NguyenThanhPhat05/Fair-Course-Registration-Waitlist-System.git)

2.Cơ sở dữ liệu: Import file .sql từ thư mục /database vào Microsoft SQL Server.

3.Chạy Backend: Mở thư mục /backend bằng IntelliJ IDEA hoặc VS Code, cấu hình thông tin kết nối database trong file application.properties rồi khởi chạy ứng dụng Spring Boot.
