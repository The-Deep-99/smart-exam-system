package com.exam.model;

import java.time.LocalDateTime;

public class Exam {
    private int id;
    private String examCode;
    private String title;
    private int subjectId;
    private String subjectName;
    private int durationMinutes;
    private int questionCount;
    private String createdBy;
    private LocalDateTime createdAt;
    private boolean isActive;

    public Exam() {}

    public Exam(String examCode, String title, int subjectId, int durationMinutes, 
                int questionCount, String createdBy) {
        this.examCode = examCode;
        this.title = title;
        this.subjectId = subjectId;
        this.durationMinutes = durationMinutes;
        this.questionCount = questionCount;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public Exam(int id, String examCode, String title, int subjectId, String subjectName,
                int durationMinutes, int questionCount, String createdBy, 
                LocalDateTime createdAt, boolean isActive) {
        this.id = id;
        this.examCode = examCode;
        this.title = title;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.durationMinutes = durationMinutes;
        this.questionCount = questionCount;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
