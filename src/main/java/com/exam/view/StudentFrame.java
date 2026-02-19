package com.exam.view;

import com.exam.dao.SubjectDAO;
import com.exam.model.Subject;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentFrame extends JFrame {

    private String studentName;

    public StudentFrame(String studentName) {
        this.studentName = studentName;

        setTitle("Student Panel - " + studentName);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel header = new JLabel("Select Subject for MCQ Test");
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));

        // Subjects panel
        JPanel subjectsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        subjectsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<Subject> subjects = SubjectDAO.getAllSubjects();

        if (subjects.isEmpty()) {
            JLabel noSubjectsLabel = new JLabel("No subjects available");
            noSubjectsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            subjectsPanel.add(noSubjectsLabel);
        } else {
            for (Subject subject : subjects) {
                JButton subjectBtn = new JButton(subject.getName() + " - " + subject.getDescription());
                subjectBtn.setFont(new Font("Arial", Font.PLAIN, 14));
                subjectBtn.addActionListener(e -> startExam(subject.getId(), subject.getName()));
                subjectsPanel.add(subjectBtn);
            }
        }

        JScrollPane scrollPane = new JScrollPane(subjectsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton resultsBtn = new JButton("View Results History");
        resultsBtn.addActionListener(e -> new ResultsFrame(studentName));
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });
        
        bottomPanel.add(resultsBtn);
        bottomPanel.add(logoutBtn);

        // Add to main panel
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void startExam(int subjectId, String subjectName) {
        dispose();
        new ExamFrame(studentName, subjectId, subjectName);
    }
}
