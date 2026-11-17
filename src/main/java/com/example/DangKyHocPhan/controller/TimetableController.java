package com.example.DangKyHocPhan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.DangKyHocPhan.dto.TimetableResponseDto;
import com.example.DangKyHocPhan.service.TimetableService;

@RestController
@RequestMapping("/api/v1/timetable")
public class TimetableController {

    @Autowired
    private TimetableService timetableService;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<TimetableResponseDto>> getTimetable(@PathVariable Long studentId) {
        List<TimetableResponseDto> timetable = timetableService.getStudentTimetable(studentId);
        return ResponseEntity.ok(timetable);
    }
}