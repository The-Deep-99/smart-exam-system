package com.exam.view;

import com.exam.dao.ResultDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class StudentStatsView {
    private Stage stage;
    private String studentName;

    public StudentStatsView(String studentName) {
        this.stage = new Stage();
        this.studentName = studentName;
        this.stage.setTitle("My Statistics - " + studentName);
        this.stage.setResizable(true);
    }

    public void show() {
        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: #f5f5f5;");

        Label header = new Label("My Performance Statistics");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        header.setTextFill(Color.web("#2c3e50"));

        List<Map<String, Object>> results = ResultDAO.getStudentResults(studentName);

        // Statistics Cards
        HBox statsBox = new HBox(15);
        statsBox.setAlignment(Pos.CENTER);

        int totalAttempts = results.size();
        double avgPercentage = results.isEmpty() ? 0 : results.stream()
            .mapToDouble(r -> (Double) r.get("percentage"))
            .average()
            .orElse(0);
        
        int bestScore = results.isEmpty() ? 0 : results.stream()
            .mapToInt(r -> (Integer) r.get("score"))
            .max()
            .orElse(0);
        
        int totalQuestions = results.isEmpty() ? 0 : results.stream()
            .mapToInt(r -> (Integer) r.get("total"))
            .sum();
        
        int totalCorrect = results.isEmpty() ? 0 : results.stream()
            .mapToInt(r -> (Integer) r.get("score"))
            .sum();

        statsBox.getChildren().addAll(
            createStatCard("Total Attempts", String.valueOf(totalAttempts), "#3498db"),
            createStatCard("Average Score", String.format("%.1f%%", avgPercentage), "#2ecc71"),
            createStatCard("Best Score", String.valueOf(bestScore), "#e74c3c"),
            createStatCard("Total Correct", String.valueOf(totalCorrect) + "/" + totalQuestions, "#9b59b6")
        );

        // Performance Chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("Percentage");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Performance by Subject");
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(400);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map<String, Object> result : results) {
            String subject = (String) result.get("subject");
            Double percentage = (Double) result.get("percentage");
            series.getData().add(new XYChart.Data<>(subject, percentage));
        }
        barChart.getData().add(series);

        mainBox.getChildren().addAll(header, statsBox, barChart);

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
}
