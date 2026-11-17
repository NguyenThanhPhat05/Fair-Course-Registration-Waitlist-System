package com.example.DangKyHocPhan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DangKyHocPhan.model.Waitlist;

@Repository
public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {
    List<Waitlist> findBySectionIdOrderByJoinedAtAscPriorityScoreDesc(Long sectionId);
    List<Waitlist> findByStudentId(Long studentId);
    Optional<Waitlist> findByStudentIdAndSectionId(Long studentId, Long sectionId);
}