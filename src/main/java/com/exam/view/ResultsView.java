package com.exam.view;

import com.exam.dao.ResultDAO;
import com.exam.service.PDFService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class ResultsView {
    private Stage stage;
    private String studentName;

    public ResultsView(String studentName) {
        this.stage = new Stage();
        this.studentName = studentName;
        this.stage.setTitle("Exam Results History - " + studentName);
        this.stage.setResizable(true);
    }

    public void show() {
        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(20));
        mainBox.setStyle("-fx-background-color: #f5f5f5;");

        Label header = new Label("Your Exam Results History");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        header.setTextFill(Color.web("#2c3e50"));
        header.setAlignment(Pos.CENTER);

        // Results table
        TableView<ResultRow> table = new TableView<>();
        table.setEditable(false);

        TableColumn<ResultRow, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
        subjectCol.setPrefWidth(200);

        TableColumn<ResultRow, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreCol.setPrefWidth(100);

        TableColumn<ResultRow, Integer> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setPrefWidth(100);

        TableColumn<ResultRow, String> percentageCol = new TableColumn<>("Percentage");
        percentageCol.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        percentageCol.setPrefWidth(120);

        TableColumn<ResultRow, String> dateCol = new TableColumn<>("Date Taken");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(200);

        table.getColumns().addAll(subjectCol, scoreCol, totalCol, percentageCol, dateCol);

        List<Map<String, Object>> results = ResultDAO.getStudentResults(studentName);
        ObservableList<ResultRow> resultList = FXCollections.observableArrayList();

        if (results.isEmpty()) {
            resultList.add(new ResultRow("No exams taken yet", 0, 0, "-", "-"));
        } else {
            for (Map<String, Object> result : results) {
                resultList.add(new ResultRow(
                    (String) result.get("subject"),
                    (Integer) result.get("score"),
                    (Integer) result.get("total"),
                    String.format("%.2f%%", (Double) result.get("percentage")),
                    (String) result.get("date")
                ));
            }
        }

        table.setItems(resultList);

        // Statistics panel
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(15));
        statsBox.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10;");

        int totalAttempts = results.size();
        double avgPercentage = results.isEmpty() ? 0 : results.stream()
            .mapToDouble(r -> (Double) r.get("percentage"))
            .average()
            .orElse(0);
        
        int bestScore = results.isEmpty() ? 0 : results.stream()
            .mapToInt(r -> (Integer) r.get("score"))
            .max()
            .orElse(0);

        Label attemptsLabel = createStatLabel("Total Attempts: " + totalAttempts);
        Label avgLabel = createStatLabel("Avg Score: " + String.format("%.2f", avgPercentage) + "%");
        Label bestLabel = createStatLabel("Best Score: " + bestScore);

        statsBox.getChildren().addAll(attemptsLabel, avgLabel, bestLabel);

        // Bottom panel
        HBox bottomBox = new HBox(15);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));

        Button downloadBtn = createStyledButton("Download All Results PDF", "#16a085");
        downloadBtn.setOnAction(e -> downloadAllResultsPDF());

        Button backBtn = createStyledButton("Back to Dashboard", "#3498db");
        backBtn.setOnAction(e -> stage.close());

        bottomBox.getChildren().addAll(downloadBtn, backBtn);

        mainBox.getChildren().addAll(header, table, statsBox, bottomBox);

        Scene scene = new Scene(mainBox, 900, 550);
        stage.setScene(scene);
        stage.show();
    }

    private Label createStatLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setTextFill(Color.web("#2c3e50"));
        label.setPadding(new Insets(10));
        return label;
    }

    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefWidth(200);
        btn.setPrefHeight(40);
        btn.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;",
            color
        ));
        btn.setOnMouseEntered(e -> btn.setStyle(String.format(
            "-fx-background-color: derive(%s, -20%%); -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;",
            color
        )));
        btn.setOnMouseExited(e -> btn.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5;",
            color
        )));
        return btn;
    }

    // Inner class for table data
    public static class ResultRow {
        private String subject;
        private Integer score;
        private Integer total;
        private String percentage;
        private String date;

        public ResultRow(String subject, Integer score, Integer total, String percentage, String date) {
            this.subject = subject;
            this.score = score;
            this.total = total;
            this.percentage = percentage;
            this.date = date;
        }

        public String getSubject() { return subject; }
        public Integer getScore() { return score; }
        public Integer getTotal() { return total; }
        public String getPercentage() { return percentage; }
        public String getDate() { return date; }
    }
    
    private void downloadAllResultsPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Results PDF");
            fileChooser.setInitialFileName("All_Results_" + studentName + "_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            
            File file = fileChooser.showSaveDialog(stage);
            if (file == null) return;
            
            PdfWriter writer = new PdfWriter(file.getAbsolutePath());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            Paragraph title = new Paragraph("EXAM RESULTS HISTORY")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
            document.add(title);
            
            Paragraph studentInfo = new Paragraph("Student: " + studentName + "\n")
                .setMarginBottom(15);
            document.add(studentInfo);
            
            Table table = new Table(5);
            table.addHeaderCell("Subject");
            table.addHeaderCell("Score");
            table.addHeaderCell("Total");
            table.addHeaderCell("Percentage");
            table.addHeaderCell("Date");
            
            List<Map<String, Object>> results = ResultDAO.getStudentResults(studentName);
            for (Map<String, Object> result : results) {
                table.addCell((String) result.get("subject"));
                table.addCell(String.valueOf(result.get("score")));
                table.addCell(String.valueOf(result.get("total")));
                table.addCell(String.format("%.2f%%", (Double) result.get("percentage")));
                table.addCell((String) result.get("date"));
            }
            
            document.add(table);
            document.close();
            
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("PDF saved successfully!");
            alert.showAndWait();
            
        } catch (Exception e) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to generate PDF: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
