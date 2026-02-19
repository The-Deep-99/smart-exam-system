package com.exam.view;

import com.exam.dao.QuestionDAO;
import com.exam.dao.ResultDAO;
import com.exam.dao.SubjectDAO;
import com.exam.model.Subject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exam.util.DBConnection;

public class AdminAnalyticsView {
    private Stage stage;

    public AdminAnalyticsView() {
        this.stage = new Stage();
        this.stage.setTitle("Analytics Dashboard");
        this.stage.setResizable(true);
    }

    public void show() {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: #f5f5f5;");

        Label header = new Label("Analytics Dashboard");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        header.setTextFill(Color.web("#2c3e50"));

        // Statistics Cards
        HBox statsBox = new HBox(15);
        statsBox.setAlignment(Pos.CENTER);

        int totalQuestions = QuestionDAO.getAllQuestions().size();
        int totalSubjects = SubjectDAO.getAllSubjects().size();
        int totalResults = getTotalResults();
        double avgScore = getAverageScore();

        statsBox.getChildren().addAll(
            createStatCard("Total Questions", String.valueOf(totalQuestions), "#3498db"),
            createStatCard("Total Subjects", String.valueOf(totalSubjects), "#2ecc71"),
            createStatCard("Total Exams Taken", String.valueOf(totalResults), "#e74c3c"),
            createStatCard("Average Score", String.format("%.1f%%", avgScore), "#9b59b6")
        );

        // Subject Distribution Chart
        PieChart subjectChart = new PieChart();
        subjectChart.setTitle("Questions by Subject");
        subjectChart.setPrefHeight(400);

        List<Subject> subjects = SubjectDAO.getAllSubjects();
        for (Subject subject : subjects) {
            int count = getQuestionCountBySubject(subject.getId());
            if (count > 0) {
                subjectChart.getData().add(new PieChart.Data(subject.getName(), count));
            }
        }

        mainBox.getChildren().addAll(header, statsBox, subjectChart);

        Scene scene = new Scene(mainBox, 900, 700);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle(String.format(
            "-fx-background-color: %s; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);",
            color
        ));
        card.setPrefWidth(180);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 12));
        titleLabel.setTextFill(Color.WHITE);

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.setTextFill(Color.WHITE);

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private int getTotalResults() {
        String sql = "SELECT COUNT(*) as total FROM results";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double getAverageScore() {
        String sql = "SELECT AVG(score * 100.0 / total_questions) as avg FROM results";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("avg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getQuestionCountBySubject(int subjectId) {
        String sql = "SELECT COUNT(*) as count FROM questions WHERE subject_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
