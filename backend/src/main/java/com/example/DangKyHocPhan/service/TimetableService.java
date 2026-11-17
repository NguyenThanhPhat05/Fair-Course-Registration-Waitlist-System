package com.example.DangKyHocPhan.service;

import com.example.DangKyHocPhan.dto.TimetableResponseDto;
import com.example.DangKyHocPhan.model.CourseSection;
import com.example.DangKyHocPhan.model.Registration;
import com.example.DangKyHocPhan.repository.CourseSectionRepository;
import com.example.DangKyHocPhan.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TimetableService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private CourseSectionRepository courseSectionRepository;

    public List<TimetableResponseDto> getStudentTimetable(Long studentId) {
        List<Registration> registrations = registrationRepository.findByStudentIdAndStatus(studentId, "ENROLLED");
        if (registrations.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> sectionIds = registrations.stream()
                .map(Registration::getSectionId)
                .collect(Collectors.toList());

        List<CourseSection> sections = courseSectionRepository.findByIdIn(sectionIds);
        Map<Long, CourseSection> sectionMap = sections.stream()
                .collect(Collectors.toMap(CourseSection::getId, sec -> sec));

        List<TimetableResponseDto> timetable = new ArrayList<>();
        for (Registration reg : registrations) {
            CourseSection section = sectionMap.get(reg.getSectionId());
            if (section != null) {
                timetable.add(new TimetableResponseDto(
                    section.getSectionCode(),
                    section.getCourseName(),
                    section.getLecturerName(),
                    section.getRoomName()
                ));
            }
        }

        return timetable;
    }
}