package com.example.DangKyHocPhan.dto;

public class TimetableResponseDto {
    private String sectionCode;
    private String courseName;
    private String lecturerName;
    private String roomName;

    public TimetableResponseDto(String sectionCode, String courseName, String lecturerName, String roomName) {
        this.sectionCode = sectionCode;
        this.courseName = courseName;
        this.lecturerName = lecturerName;
        this.roomName = roomName;
    }

    // Getters and Setters
    public String getSectionCode() { return sectionCode; }
    public void setSectionCode(String sectionCode) { this.sectionCode = sectionCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getLecturerName() { return lecturerName; }
    public void setLecturerName(String lecturerName) { this.lecturerName = lecturerName; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
}