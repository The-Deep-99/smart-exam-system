package com.exam.view;

import com.exam.dao.QuestionDAO;
import com.exam.dao.ResultDAO;
import com.exam.model.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamFrame extends JFrame {

    private String username;
    private int subjectId;
    private String subjectName;
    private List<Question> questions;
    private Map<Integer, String> studentAnswers;
    private int currentQuestionIndex = 0;

    private JLabel progressLabel;
    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup buttonGroup;
    private JButton nextBtn;
    private JButton prevBtn;
    private JButton submitBtn;
    private JProgressBar progressBar;

    public ExamFrame(String username, int subjectId, String subjectName) {
        this.username = username;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.studentAnswers = new HashMap<>();
        this.questions = QuestionDAO.getQuestionsBySubject(subjectId);

        initializeUI();
        disableAntiCheat();

        // Disable focus traversal with Tab
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getKeyCode() == KeyEvent.VK_TAB) {
                return true; // Consume the event
            }
            return false;
        });
    }

    private void initializeUI() {
        setTitle("Online Exam - " + subjectName);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top panel - Header and Progress
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel headerLabel = new JLabel("Exam: " + subjectName + " - Student: " + username);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));

        progressLabel = new JLabel();
        updateProgressLabel();

        topPanel.add(headerLabel, BorderLayout.WEST);
        topPanel.add(progressLabel, BorderLayout.EAST);

        progressBar = new JProgressBar(0, questions.size());
        progressBar.setValue(1);
        progressBar.setStringPainted(true);

        JPanel progressPanelContainer = new JPanel(new BorderLayout());
        progressPanelContainer.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        progressPanelContainer.add(progressBar, BorderLayout.CENTER);

        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.add(topPanel, BorderLayout.NORTH);
        headerContainer.add(progressPanelContainer, BorderLayout.SOUTH);

        // Middle panel - Question and Options
        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        questionLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        optionButtons = new JRadioButton[4];
        buttonGroup = new ButtonGroup();

        String[] optionLabels = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton(optionLabels[i] + ". ");
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 12));
            buttonGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }

        questionPanel.add(questionLabel, BorderLayout.NORTH);
        questionPanel.add(optionsPanel, BorderLayout.CENTER);

        // Bottom panel - Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        prevBtn = new JButton("Previous");
        prevBtn.addActionListener(e -> previousQuestion());
        prevBtn.setEnabled(false);

        nextBtn = new JButton("Next");
        nextBtn.addActionListener(e -> nextQuestion());

        submitBtn = new JButton("Submit Exam");
        submitBtn.addActionListener(e -> submitExam());

        buttonPanel.add(prevBtn);
        buttonPanel.add(nextBtn);
        buttonPanel.add(submitBtn);

        // Add panels to main
        mainPanel.add(headerContainer, BorderLayout.NORTH);
        mainPanel.add(questionPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        if (!questions.isEmpty()) {
            displayQuestion(0);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No questions available for this subject",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }

        setVisible(true);
    }

    private void displayQuestion(int index) {
        if (index < 0 || index >= questions.size()) {
            return;
        }

        currentQuestionIndex = index;
        Question q = questions.get(index);

        // Update question text
        questionLabel.setText("<html><b>Q" + (index + 1) + ":</b> " + q.getQuestion() + "</html>");

        // Update option buttons
        optionButtons[0].setText("A. " + q.getOptionA());
        optionButtons[1].setText("B. " + q.getOptionB());
        optionButtons[2].setText("C. " + q.getOptionC());
        optionButtons[3].setText("D. " + q.getOptionD());

        // Restore student's previous answer if exists
        buttonGroup.clearSelection();
        if (studentAnswers.containsKey(index)) {
            String answer = studentAnswers.get(index);
            int answerIndex = answer.charAt(0) - 'A';
            if (answerIndex >= 0 && answerIndex < 4) {
                optionButtons[answerIndex].setSelected(true);
            }
        }

        updateProgressLabel();
        updateButtonStates();
        progressBar.setValue(index + 1);
    }

    private void saveCurrentAnswer() {
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected()) {
                studentAnswers.put(currentQuestionIndex, String.valueOf((char)('A' + i)));
                return;
            }
        }
        // If no answer selected, remove it from map
        studentAnswers.remove(currentQuestionIndex);
    }

    private void nextQuestion() {
        saveCurrentAnswer();
        if (currentQuestionIndex < questions.size() - 1) {
            displayQuestion(currentQuestionIndex + 1);
        }
    }

    private void previousQuestion() {
        saveCurrentAnswer();
        if (currentQuestionIndex > 0) {
            displayQuestion(currentQuestionIndex - 1);
        }
    }

    private void updateProgressLabel() {
        progressLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());
    }

    private void updateButtonStates() {
        prevBtn.setEnabled(currentQuestionIndex > 0);
        nextBtn.setEnabled(currentQuestionIndex < questions.size() - 1);
    }

    private void submitExam() {
        saveCurrentAnswer();

        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to submit the exam?\nYou have answered " + studentAnswers.size() + " out of " + questions.size() + " questions.",
                "Submit Exam",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            calculateAndDisplayScore();
        }
    }

    private void calculateAndDisplayScore() {
        int score = 0;

        for (int i = 0; i < questions.size(); i++) {
            if (studentAnswers.containsKey(i)) {
                Question q = questions.get(i);
                String studentAnswer = studentAnswers.get(i);
                if (studentAnswer.equals(q.getCorrectOption())) {
                    score++;
                }
            }
        }

        double percentage = (double) score / questions.size() * 100;

        // Save result to database
        ResultDAO.saveResult(username, subjectId, score, questions.size());

        JOptionPane.showMessageDialog(this,
                "Exam Submitted!\n\nScore: " + score + " out of " + questions.size() + "\nPercentage: " + String.format("%.2f", percentage) + "%",
                "Exam Result",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
        new StudentFrame(username);
    }

    private void disableAntiCheat() {
        // Disable copy, cut, paste using KeyListener
        KeyListener antiCheatListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Disable Ctrl+C (Copy)
                if ((e.getKeyCode() == KeyEvent.VK_C) && (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
                    e.consume();
                    JOptionPane.showMessageDialog(ExamFrame.this, "Copy is disabled during exam!", "Anti-Cheat", JOptionPane.WARNING_MESSAGE);
                }
                // Disable Ctrl+V (Paste)
                else if ((e.getKeyCode() == KeyEvent.VK_V) && (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
                    e.consume();
                    JOptionPane.showMessageDialog(ExamFrame.this, "Paste is disabled during exam!", "Anti-Cheat", JOptionPane.WARNING_MESSAGE);
                }
                // Disable Ctrl+X (Cut)
                else if ((e.getKeyCode() == KeyEvent.VK_X) && (e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
                    e.consume();
                    JOptionPane.showMessageDialog(ExamFrame.this, "Cut is disabled during exam!", "Anti-Cheat", JOptionPane.WARNING_MESSAGE);
                }
                // Disable Alt+Tab and Ctrl+Tab
                else if ((e.getKeyCode() == KeyEvent.VK_TAB) && ((e.getModifiers() & KeyEvent.ALT_MASK) != 0 || (e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    e.consume();
                    JOptionPane.showMessageDialog(ExamFrame.this, "Tab switching is disabled during exam!", "Anti-Cheat", JOptionPane.WARNING_MESSAGE);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        };

        // Add listener to frame and all components
        addKeyListener(antiCheatListener);
        setFocusable(true);
        
        for (JRadioButton btn : optionButtons) {
            btn.addKeyListener(antiCheatListener);
        }
        nextBtn.addKeyListener(antiCheatListener);
        prevBtn.addKeyListener(antiCheatListener);
        submitBtn.addKeyListener(antiCheatListener);
    }
}
