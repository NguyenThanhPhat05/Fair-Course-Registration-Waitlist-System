package com.example.DangKyHocPhan.controller;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DangKyHocPhan.model.AuditLog;
import com.example.DangKyHocPhan.model.CourseSection;
import com.example.DangKyHocPhan.model.Student;
import com.example.DangKyHocPhan.repository.CourseSectionRepository;
import com.example.DangKyHocPhan.repository.StudentRepository;
import com.example.DangKyHocPhan.service.RegistrationService;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private CourseSectionRepository sectionRepository;
    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String studentCode, @RequestParam String password) {
        try {
            Student student = registrationService.login(studentCode, password);
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/sections")
    public ResponseEntity<List<CourseSection>> getSections() {
        return ResponseEntity.ok(registrationService.getAllSections());
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerCourse(@RequestParam Long studentId, @RequestParam Long sectionId) {
        try {
            String message = registrationService.registerCourse(studentId, sectionId);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/drop")
    public ResponseEntity<String> dropCourse(@RequestParam Long studentId, @RequestParam Long sectionId) {
        try {
            String message = registrationService.dropCourse(studentId, sectionId);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/my-courses")
    public ResponseEntity<?> getMyCourses(@RequestParam Long studentId) {
        return ResponseEntity.ok(registrationService.getStudentEnrolledCourses(studentId));
    }

    @GetMapping("/my-waitlists")
    public ResponseEntity<?> getMyWaitlists(@RequestParam Long studentId) {
        return ResponseEntity.ok(registrationService.getStudentWaitlists(studentId));
    }

    @PostMapping("/admin/add-section")
    public ResponseEntity<?> addSection(
            @RequestParam Long adminId,
            @RequestParam String sectionCode,
            @RequestParam String courseName,
            @RequestParam String dayOfWeek,
            @RequestParam int credits,
            @RequestParam String lecturerName,
            @RequestParam String roomName,
            @RequestParam int capacity) {

        try {
            if (sectionRepository.existsBySectionCode(sectionCode)) {
                return ResponseEntity.badRequest().body("Lỗi: Mã lớp học phần '" + sectionCode + "' đã tồn tại trong hệ thống!");
            }

            CourseSection section = new CourseSection();
            section.setSectionCode(sectionCode);
            section.setCourseName(courseName);
            section.setDayOfWeek(dayOfWeek);
            section.setCredits(credits);
            section.setLecturerName(lecturerName);
            section.setRoomName(roomName);
            section.setCapacity(capacity);
            section.setEnrolledCount(0);

            sectionRepository.save(section);
            return ResponseEntity.ok("Thêm lớp học phần thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi hệ thống khi tạo lớp học phần: " + e.getMessage());
        }
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLog>> getAuditLogs() {
        return ResponseEntity.ok(registrationService.getAuditLogs());
    }

    @GetMapping("/lecturer/sections")
    public ResponseEntity<List<CourseSection>> getLecturerSections(@RequestParam String lecturerName) {
        return ResponseEntity.ok(registrationService.getSectionsByLecturer(lecturerName));
    }

    @GetMapping("/lecturer/enrolled-students")
    public ResponseEntity<?> getEnrolledStudents(@RequestParam Long sectionId) {
        return ResponseEntity.ok(registrationService.getEnrolledStudentsBySection(sectionId));
    }

    @GetMapping("/lecturer/waitlist-students")
    public ResponseEntity<?> getWaitlistStudents(@RequestParam Long sectionId) {
        return ResponseEntity.ok(registrationService.getWaitlistStudentsBySection(sectionId));
    }

    @GetMapping("/lecturer/export-excel")
    public ResponseEntity<InputStreamResource> exportEnrolledExcel(@RequestParam Long sectionId) {
        try {
            CourseSection section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần!"));

            ByteArrayInputStream in = registrationService.exportEnrolledStudentsToExcel(sectionId);

            HttpHeaders headers = new HttpHeaders();
            String fileName = "DanhSachSV_" + section.getSectionCode() + ".xlsx";
            headers.add("Content-Disposition", "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(in));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/open-window")
    public ResponseEntity<String> openWindow(
            @RequestParam Long userId,
            @RequestParam Long sectionId,
            @RequestParam(defaultValue = "5") int minutes) {
        try {
            return ResponseEntity.ok(registrationService.openRegistrationWindow(userId, sectionId, minutes));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/stress-test")
    public ResponseEntity<?> runStressTest(
        @RequestParam(required = false) Long sectionId,
        @RequestParam(required = false, defaultValue = "10") Integer numberOfThreads) {

        if (sectionId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Chưa chọn lớp học phần!"));
        }

        for (int i = 1; i <= numberOfThreads; i++) {
            long studentId = 8000L + i;
            if (!studentRepository.existsById(studentId)) {
                try {
                    Student testStudent = new Student();
                    testStudent.setId(studentId);
                    testStudent.setStudentCode("TEST" + studentId);
                    testStudent.setFullName("Sinh Viên Test " + i);
                    testStudent.setPassword("123456");
                    testStudent.setTotalCredits(30);
                    testStudent.setGpa(3.2);
                    studentRepository.save(testStudent);
                } catch (Exception e) {
                    // Bỏ qua
                }
            }
        }

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger waitlistCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= numberOfThreads; i++) {
            final long studentId = 8000L + i;

            executor.submit(() -> {
                try {
                    String result = registrationService.registerCourse(studentId, sectionId);
                    String msg = result != null ? result.toLowerCase() : "";

                    // registerCourse KHÔNG ném lỗi khi vào danh sách chờ, mà trả về chuỗi thông báo
                    if (msg.contains("danh sách chờ") || msg.contains("lớp đã đầy")) {
                        waitlistCount.incrementAndGet();
                    } else {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

                    if (errorMsg.contains("waitlist") ||
                        errorMsg.contains("chờ") ||
                        errorMsg.contains("danh sách chờ") ||
                        errorMsg.contains("đầy") ||
                        errorMsg.contains("hết chỗ")) {

                        waitlistCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long executionTime = System.currentTimeMillis() - startTime;

        Map<String, Object> response = new HashMap<>();
        response.put("total", numberOfThreads);
        response.put("success", successCount.get());
        response.put("waitlist", waitlistCount.get());
        response.put("failed", failCount.get());
        response.put("timeMs", executionTime);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/sections/{id}")
    public ResponseEntity<?> updateSection(@PathVariable Long id, @RequestBody CourseSection updatedSection) {
        try {
            CourseSection sec = sectionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần với ID: " + id));

            sec.setSectionCode(updatedSection.getSectionCode());
            sec.setCourseName(updatedSection.getCourseName());
            sec.setDayOfWeek(updatedSection.getDayOfWeek());
            sec.setCredits(updatedSection.getCredits());
            sec.setLecturerName(updatedSection.getLecturerName());
            sec.setRoomName(updatedSection.getRoomName());
            sec.setCapacity(updatedSection.getCapacity());

            sectionRepository.save(sec);
            return ResponseEntity.ok("Cập nhật lớp học phần thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi cập nhật: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam Long studentId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

            if (!student.getPassword().equals(oldPassword)) {
                return ResponseEntity.badRequest().body("Mật khẩu cũ không chính xác!");
            }

            student.setPassword(newPassword);
            studentRepository.save(student);

            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi đổi mật khẩu: " + e.getMessage());
        }
    }
}