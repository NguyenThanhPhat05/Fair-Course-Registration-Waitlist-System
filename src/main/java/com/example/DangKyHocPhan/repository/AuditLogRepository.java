package com.example.DangKyHocPhan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DangKyHocPhan.model.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findTop20ByOrderByIdDesc();
    List<AuditLog> findByStudentIdOrderByIdDesc(Long studentId);
}