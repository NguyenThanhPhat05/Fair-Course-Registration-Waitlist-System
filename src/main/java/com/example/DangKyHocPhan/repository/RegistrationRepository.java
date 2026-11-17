package com.example.DangKyHocPhan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DangKyHocPhan.model.Registration;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByStudentId(Long studentId);
    Optional<Registration> findByStudentIdAndSectionIdAndStatus(Long studentId, Long sectionId, String status);
    
    // Thêm dòng này để Spring Data JPA tự động sinh câu query lọc theo studentId và status ("ENROLLED")
    List<Registration> findByStudentIdAndStatus(Long studentId, String status);
}