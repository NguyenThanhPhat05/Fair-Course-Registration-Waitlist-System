package com.example.DangKyHocPhan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DangKyHocPhan.model.CourseSection;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    List<CourseSection> findByIdIn(List<Long> ids);
    boolean existsBySectionCode(String sectionCode);
}