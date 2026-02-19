package com.exam.model;

public class Question {

    private int id;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctOption;
    private int subjectId;
    private String subjectName;

    // Constructor without id (for AddQuestionFrame)
    public Question(String question, String optionA, String optionB,
                    String optionC, String optionD, String correctOption) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
    }

    // Constructor with id (for ViewQuestionsFrame)
    public Question(int id, String question, String optionA, String optionB,
                    String optionC, String optionD, String correctOption) {
        this.id = id;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
    }
    
    // Constructor with subject info
    public Question(int id, String question, String optionA, String optionB,
                    String optionC, String optionD, String correctOption, 
                    int subjectId, String subjectName) {
        this.id = id;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }
    
    public int getSubjectId() {
        return subjectId;
    }
    
    public String getSubjectName() {
        return subjectName;
    }
}