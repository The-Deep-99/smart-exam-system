package com.exam.view;

import com.exam.dao.UserDAO;
import com.exam.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private String userRole; // Will be set to either "ADMIN" or "STUDENT"

    public LoginFrame() {
        showRoleSelection();
    }

    private void showRoleSelection() {
        this.setTitle("Assess Wise - Examination and Evaluation System");
        this.setSize(500, 250);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Header
        JLabel header = new JLabel("Assess Wise - Examination and Evaluation System");
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));

        // Role selection panel
        JPanel rolePanel = new JPanel(new GridLayout(1, 2, 30, 0));

        JButton adminBtn = new JButton("Admin Login");
        adminBtn.setFont(new Font("Arial", Font.BOLD, 16));
        adminBtn.setBackground(new Color(70, 130, 180));
        adminBtn.setForeground(Color.WHITE);
        adminBtn.addActionListener(e -> {
            userRole = "ADMIN";
            showLoginDialog();
        });

        JButton studentBtn = new JButton("Student Login");
        studentBtn.setFont(new Font("Arial", Font.BOLD, 16));
        studentBtn.setBackground(new Color(34, 139, 34));
        studentBtn.setForeground(Color.WHITE);
        studentBtn.addActionListener(e -> {
            userRole = "STUDENT";
            showLoginDialog();
        });

        rolePanel.add(adminBtn);
        rolePanel.add(studentBtn);

        // Add to main panel
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(rolePanel, BorderLayout.CENTER);

        this.add(mainPanel);
        this.setVisible(true);
    }

    private void showLoginDialog() {
        // Clear the frame
        this.getContentPane().removeAll();
        this.setTitle("Assess Wise - Examination and Evaluation System - " + userRole + " Login");
        this.setSize(450, 320);
        this.setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with role info
        JLabel header = new JLabel(userRole + " Login");
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel roleLabel = new JLabel("Role: " + userRole);
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(100, 100, 100));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(header, BorderLayout.NORTH);
        headerPanel.add(roleLabel, BorderLayout.SOUTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(20);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(20);

        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Back");

        // Add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(userField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(backBtn, gbc);

        gbc.gridx = 1;
        formPanel.add(loginBtn, gbc);

        // Add to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        this.add(mainPanel);
        this.revalidate();
        this.repaint();

        // Action Listeners
        loginBtn.addActionListener(e -> login(userField, passField));
        backBtn.addActionListener(e -> {
            this.getContentPane().removeAll();
            showRoleSelection();
        });

        // Press Enter to login
        passField.addActionListener(e -> login(userField, passField));
    }

    private void login(JTextField userField, JPasswordField passField) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = UserDAO.authenticate(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Invalid Credentials",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if user role matches selected role
        if (!user.getRole().equals(userRole)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid credentials for " + userRole,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Successful login
        dispose();
        if (user.getRole().equals("ADMIN")) {
            new AdminFrame(user.getUsername());
        } else if (user.getRole().equals("STUDENT")) {
            new StudentFrame(user.getUsername());
        }
    }
}