package com.example.DangKyHocPhan.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.DangKyHocPhan.model.AuditLog;
import com.example.DangKyHocPhan.model.CourseSection;
import com.example.DangKyHocPhan.model.Registration;
import com.example.DangKyHocPhan.model.Student;
import com.example.DangKyHocPhan.model.Waitlist;
import com.example.DangKyHocPhan.repository.AuditLogRepository;
import com.example.DangKyHocPhan.repository.CourseSectionRepository;
import com.example.DangKyHocPhan.repository.RegistrationRepository;
import com.example.DangKyHocPhan.repository.StudentRepository;
import com.example.DangKyHocPhan.repository.WaitlistRepository;

@Service
public class RegistrationService {

    @Autowired private StudentRepository studentRepository;
    @Autowired private CourseSectionRepository sectionRepository;
    @Autowired private RegistrationRepository registrationRepository;
    @Autowired private WaitlistRepository waitlistRepository;
    @Autowired private AuditLogRepository auditLogRepository;

    private boolean registrationOpen = true;

    public boolean isRegistrationOpen() { return registrationOpen; }
    public void setRegistrationOpen(boolean status) { this.registrationOpen = status; }

    public Student login(String studentCode, String password) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new RuntimeException("Mã đăng nhập không tồn tại!"));
        if (!student.getPassword().equals(password)) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }
        return student;
    }

    @Transactional
    public String changePassword(Long studentId, String oldPassword, String newPassword) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên!"));
        if (!student.getPassword().equals(oldPassword)) {
            throw new RuntimeException("Mật khẩu cũ không chính xác!");
        }
        student.setPassword(newPassword);
        studentRepository.save(student);
        return "Đổi mật khẩu thành công!";
    }

    @Transactional
    public synchronized String registerCourse(Long studentId, Long sectionId) {
        if (!registrationOpen) {
            throw new RuntimeException("Cổng đăng ký học phần hiện đang đóng!");
        }

        Instant requestTime = Instant.now();

        Optional<Registration> existingReg = registrationRepository.findByStudentIdAndSectionIdAndStatus(studentId, sectionId, "ENROLLED");
        if (existingReg.isPresent()) {
            return "Bạn đã đăng ký thành công môn học này trước đó rồi!";
        }

        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        if (section.getEnrolledCount() < section.getCapacity()) {
            section.setEnrolledCount(section.getEnrolledCount() + 1);
            sectionRepository.save(section);

            Registration reg = new Registration();
            reg.setStudentId(studentId);
            reg.setSectionId(sectionId);
            reg.setStatus("ENROLLED");
            reg.setRegisteredAt(requestTime);
            registrationRepository.save(reg);

            saveAudit(studentId, sectionId, "ENROLLED_SUCCESS",
                "Đăng ký thành công slot " + section.getEnrolledCount() + "/" + section.getCapacity());

            return "Đăng ký thành công môn " + section.getCourseName() + "!";
        }

        if (section.getRegistrationDeadline() == null || LocalDateTime.now().isAfter(section.getRegistrationDeadline())) {
            throw new RuntimeException("Cổng đăng ký của lớp này hiện đang ĐÓNG hoặc đã hết 5 phút đăng ký!");
        } else {
            Optional<Waitlist> existingWaitlist = waitlistRepository.findByStudentIdAndSectionId(studentId, sectionId);
            if (existingWaitlist.isPresent()) {
                return "Bạn đã có tên trong danh sách chờ của lớp này rồi!";
            }

            double priorityScore = (student.getGpa() / 4.0) * 50 + (student.getTotalCredits() / 150.0) * 50;
            if (student.isSenior()) priorityScore += 10;

            Waitlist waitlist = new Waitlist();
            waitlist.setSectionId(sectionId);
            waitlist.setStudentId(studentId);
            waitlist.setJoinedAt(requestTime);
            waitlist.setPriorityScore(priorityScore);
            waitlistRepository.save(waitlist);

            saveAudit(studentId, sectionId, "WAITLISTED",
                "Lớp đầy. Xếp vào Danh sách chờ (Điểm ưu tiên: " + String.format("%.2f", priorityScore) + ")");

            return "Lớp đã đầy. Bạn đã được đưa vào Danh sách chờ công bằng.";
        }
    }

    @Transactional
    public synchronized String dropCourse(Long studentId, Long sectionId) {
        Registration reg = registrationRepository.findByStudentIdAndSectionIdAndStatus(studentId, sectionId, "ENROLLED")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đăng ký để hủy"));

        reg.setStatus("DROPPED");
        registrationRepository.save(reg);

        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần để hủy"));

        saveAudit(studentId, sectionId, "DROPPED", "Hủy đăng ký lớp " + section.getSectionCode());

        List<Waitlist> waitlists = waitlistRepository.findBySectionIdOrderByJoinedAtAscPriorityScoreDesc(sectionId);
        if (!waitlists.isEmpty()) {
            Waitlist topWaitlist = waitlists.get(0);

            Registration newReg = new Registration();
            newReg.setStudentId(topWaitlist.getStudentId());
            newReg.setSectionId(sectionId);
            newReg.setStatus("ENROLLED");
            newReg.setRegisteredAt(Instant.now());
            registrationRepository.save(newReg);

            waitlistRepository.delete(topWaitlist);

            saveAudit(topWaitlist.getStudentId(), sectionId, "PROMOTED_FROM_WAITLIST",
                "Tự động đôn từ Danh sách chờ lên Chính thức do có sinh viên hủy lớp.");
        } else {
            section.setEnrolledCount(Math.max(0, section.getEnrolledCount() - 1));
            sectionRepository.save(section);
        }

        return "Hủy lớp học phần thành công!";
    }

    public List<Map<String, Object>> getStudentEnrolledCourses(Long studentId) {
        List<Registration> regs = registrationRepository.findByStudentId(studentId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Registration reg : regs) {
            if ("ENROLLED".equals(reg.getStatus())) {
                CourseSection sec = sectionRepository.findById(reg.getSectionId()).orElse(null);
                if (sec != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("sectionId", sec.getId());
                    map.put("sectionCode", sec.getSectionCode());
                    map.put("courseName", sec.getCourseName());
                    map.put("lecturerName", sec.getLecturerName());
                    map.put("roomName", sec.getRoomName());
                    map.put("registeredAt", reg.getRegisteredAt());
                    result.add(map);
                }
            }
        }
        return result;
    }

    public List<Map<String, Object>> getStudentWaitlists(Long studentId) {
        List<Waitlist> waitlists = waitlistRepository.findByStudentId(studentId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Waitlist w : waitlists) {
            CourseSection sec = sectionRepository.findById(w.getSectionId()).orElse(null);
            if (sec != null) {
                List<Waitlist> allWaitlistForSec = waitlistRepository.findBySectionIdOrderByJoinedAtAscPriorityScoreDesc(sec.getId());
                int position = 1;
                for (int i = 0; i < allWaitlistForSec.size(); i++) {
                    if (allWaitlistForSec.get(i).getId().equals(w.getId())) {
                        position = i + 1;
                        break;
                    }
                }

                Map<String, Object> map = new HashMap<>();
                map.put("sectionId", sec.getId());
                map.put("sectionCode", sec.getSectionCode());
                map.put("courseName", sec.getCourseName());
                map.put("queuePosition", position);
                map.put("priorityScore", w.getPriorityScore());
                map.put("joinedAt", w.getJoinedAt());
                result.add(map);
            }
        }
        return result;
    }

    public List<Map<String, Object>> getEnrolledStudentsBySection(Long sectionId) {
        List<Registration> regs = registrationRepository.findAll().stream()
                .filter(r -> r.getSectionId().equals(sectionId) && "ENROLLED".equals(r.getStatus()))
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Registration reg : regs) {
            Student st = studentRepository.findById(reg.getStudentId()).orElse(null);
            if (st != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("studentCode", st.getStudentCode());
                map.put("fullName", st.getFullName());
                map.put("gpa", st.getGpa());
                map.put("totalCredits", st.getTotalCredits());
                map.put("registeredAt", reg.getRegisteredAt());
                result.add(map);
            }
        }
        return result;
    }

    public List<Map<String, Object>> getWaitlistStudentsBySection(Long sectionId) {
        List<Waitlist> waits = waitlistRepository.findBySectionIdOrderByJoinedAtAscPriorityScoreDesc(sectionId);
        List<Map<String, Object>> result = new ArrayList<>();
        int pos = 1;
        for (Waitlist w : waits) {
            Student st = studentRepository.findById(w.getStudentId()).orElse(null);
            if (st != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("queuePosition", pos++);
                map.put("studentCode", st.getStudentCode());
                map.put("fullName", st.getFullName());
                map.put("priorityScore", w.getPriorityScore());
                map.put("joinedAt", w.getJoinedAt());
                result.add(map);
            }
        }
        return result;
    }

    public List<AuditLog> getAuditLogs() {
        return auditLogRepository.findTop20ByOrderByIdDesc();
    }

    public List<CourseSection> getAllSections() {
        return sectionRepository.findAll();
    }

    private void saveAudit(Long studentId, Long sectionId, String action, String details) {
        AuditLog log = new AuditLog();
        log.setStudentId(studentId);
        log.setSectionId(sectionId);
        log.setActionType(action);
        log.setReasonDetails(details);
        log.setCreatedAt(Instant.now());
        auditLogRepository.save(log);
    }

    public List<CourseSection> getSectionsByLecturer(String lecturerFullName) {
        return sectionRepository.findAll().stream()
                .filter(s -> s.getLecturerName() != null && s.getLecturerName().contains(lecturerFullName))
                .collect(Collectors.toList());
    }

    @Transactional
    public String openRegistrationWindow(Long userId, Long sectionId, int minutes) {
        Student user = studentRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
        if (!"ADMIN".equals(user.getRole()) && !"LECTURER".equals(user.getRole())) {
            throw new RuntimeException("Chỉ Admin hoặc Giảng viên mới có quyền mở cổng đăng ký!");
        }

        CourseSection section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần!"));

        if ("LECTURER".equals(user.getRole())
                && (section.getLecturerName() == null || !section.getLecturerName().equalsIgnoreCase(user.getFullName()))) {
            throw new RuntimeException("Bạn chỉ được mở đăng ký cho lớp do chính mình giảng dạy!");
        }

        LocalDateTime deadline = LocalDateTime.now().plusMinutes(minutes);
        section.setRegistrationDeadline(deadline);
        sectionRepository.save(section);

        saveAudit(userId, sectionId, "OPEN_REGISTRATION",
            user.getRole() + " (" + user.getFullName() + ") đã mở cổng đăng ký lớp " + section.getSectionCode() + " trong " + minutes + " phút.");

        return "Đã mở cổng đăng ký lớp " + section.getSectionCode() + " trong " + minutes + " phút!";
    }

    public ByteArrayInputStream exportEnrolledStudentsToExcel(Long sectionId) {
        try {
            CourseSection section = sectionRepository.findById(sectionId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học phần!"));

            List<Map<String, Object>> enrolledStudents = getEnrolledStudentsBySection(sectionId);

            try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                Sheet sheet = workbook.createSheet("Danh_Sach_SV");

                Row titleRow = sheet.createRow(0);
                titleRow.createCell(0).setCellValue("DANH SÁCH SINH VIÊN CHÍNH THỨC (ENROLLED)");

                Row infoRow = sheet.createRow(1);
                infoRow.createCell(0).setCellValue("Mã lớp: " + section.getSectionCode() + " - Môn học: " + section.getCourseName());

                Row headerRow = sheet.createRow(3);
                String[] columns = {"STT", "Mã Sinh Viên", "Họ và Tên", "Số Tín Chỉ", "GPA", "Trạng Thái"};

                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerStyle);
                }

                int rowIdx = 4;
                int stt = 1;
                for (Map<String, Object> sv : enrolledStudents) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(stt++);
                    row.createCell(1).setCellValue((String) sv.get("studentCode"));
                    row.createCell(2).setCellValue((String) sv.get("fullName"));

                    Object creditsObj = sv.get("totalCredits");
                    row.createCell(3).setCellValue(creditsObj != null ? ((Number) creditsObj).intValue() : 30);

                    Object gpaObj = sv.get("gpa");
                    row.createCell(4).setCellValue(gpaObj != null ? ((Number) gpaObj).doubleValue() : 3.2);

                    row.createCell(5).setCellValue("Enrolled");
                }

                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file Excel: " + e.getMessage());
        }
    }
}