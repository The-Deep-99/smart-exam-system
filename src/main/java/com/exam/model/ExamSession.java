package com.exam.model;

import java.time.LocalDateTime;

public class ExamSession {
    private int id;
    private String examCode;
    private String studentUsername;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int score;
    private int totalQuestions;
    private String status;

    public ExamSession() {}

    public ExamSession(String examCode, String studentUsername, int totalQuestions) {
        this.examCode = examCode;
        this.studentUsername = studentUsername;
        this.totalQuestions = totalQuestions;
        this.startTime = LocalDateTime.now();
        this.status = "ONGOING";
    }

    public ExamSession(int id, String examCode, String studentUsername, 
                      LocalDateTime startTime, LocalDateTime endTime, 
                      int score, int totalQuestions, String status) {
        this.id = id;
        this.examCode = examCode;
        this.studentUsername = studentUsername;
        this.startTime = startTime;
        this.endTime = endTime;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }

    public String getStudentUsername() { return studentUsername; }
    public void setStudentUsername(String studentUsername) { this.studentUsername = studentUsername; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
