package com.exam.view;

import com.exam.dao.ResultDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ResultsFrame extends JFrame {

    private String studentName;

    public ResultsFrame(String studentName) {
        this.studentName = studentName;

        setTitle("Exam Results History - " + studentName);
        setSize(900, 450);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JLabel header = new JLabel("Your Exam Results History");
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));

        // Results table
        String[] columns = {"Subject", "Score", "Total", "Percentage", "Date Taken"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Percentage column

        List<Map<String, Object>> results = ResultDAO.getStudentResults(studentName);

        if (results.isEmpty()) {
            model.addRow(new Object[]{"No exams taken yet", "-", "-", "-", "-"});
        } else {
            for (Map<String, Object> result : results) {
                model.addRow(new Object[]{
                        result.get("subject"),
                        result.get("score"),
                        result.get("total"),
                        result.get("percentage") + "%",
                        result.get("date")
                });
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statsPanel.setBackground(new Color(240, 240, 240));

        int totalAttempts = results.size();
        double avgPercentage = results.isEmpty() ? 0 : results.stream()
                .mapToDouble(r -> (Double) r.get("percentage"))
                .average()
                .orElse(0);
        
        int bestScore = results.isEmpty() ? 0 : results.stream()
                .mapToInt(r -> (Integer) r.get("score"))
                .max()
                .orElse(0);

        JLabel attemptsLabel = new JLabel("Total Attempts: " + totalAttempts);
        attemptsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        attemptsLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel avgLabel = new JLabel("Avg Score: " + String.format("%.2f", avgPercentage) + "%");
        avgLabel.setFont(new Font("Arial", Font.BOLD, 12));
        avgLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel bestLabel = new JLabel("Best Score: " + bestScore);
        bestLabel.setFont(new Font("Arial", Font.BOLD, 12));
        bestLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        statsPanel.add(attemptsLabel);
        statsPanel.add(avgLabel);
        statsPanel.add(bestLabel);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> {
            dispose();
        });
        bottomPanel.add(backBtn);

        // Add to main panel
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statsPanel, BorderLayout.SOUTH);
        
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(mainPanel, BorderLayout.CENTER);
        containerPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(containerPanel);
        setVisible(true);
    }
}
