package com.exam.view;

import javax.swing.*;
import java.awt.*;

public class AdminFrame extends JFrame {

    public AdminFrame(String adminName) {
        setTitle("Admin Panel - " + adminName);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JButton addBtn = new JButton("Add Question");
        JButton viewBtn = new JButton("View All Questions");
        JButton logoutBtn = new JButton("Logout");

        addBtn.addActionListener(e -> new AddQuestionFrame());
        viewBtn.addActionListener(e -> new ViewQuestionsFrame());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        panel.add(addBtn);
        panel.add(viewBtn);
        panel.add(logoutBtn);

        add(panel);
        setVisible(true);
    }
}