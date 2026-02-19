package com.exam.service;

import com.exam.model.Question;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class PDFService {
    
    public static void generateResultPDF(String username, String subjectName, int score, int total, 
                                         double percentage, Map<Integer, String> studentAnswers, 
                                         List<Question> questions) {
        try {
            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Result PDF");
            fileChooser.setInitialFileName("Exam_Result_" + username + "_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            
            File file = fileChooser.showSaveDialog(new Stage());
            if (file == null) {
                return; // User cancelled
            }
            
            PdfWriter writer = new PdfWriter(file.getAbsolutePath());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Title
            Paragraph title = new Paragraph("EXAM RESULT")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
            document.add(title);
            
            // Student Info
            Paragraph studentInfo = new Paragraph()
                .add("Student Name: " + username + "\n")
                .add("Subject: " + subjectName + "\n")
                .add("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n")
                .setMarginBottom(15);
            document.add(studentInfo);
            
            // Score Summary
            Paragraph scoreSummary = new Paragraph()
                .add("SCORE SUMMARY\n")
                .setBold()
                .setMarginBottom(10);
            document.add(scoreSummary);
            
            Table summaryTable = new Table(2);
            summaryTable.addCell("Total Questions");
            summaryTable.addCell(String.valueOf(total));
            summaryTable.addCell("Correct Answers");
            summaryTable.addCell(String.valueOf(score));
            summaryTable.addCell("Wrong Answers");
            summaryTable.addCell(String.valueOf(total - score));
            summaryTable.addCell("Percentage");
            summaryTable.addCell(String.format("%.2f%%", percentage));
            summaryTable.addCell("Status");
            summaryTable.addCell(percentage >= 60 ? "PASSED" : "FAILED");
            document.add(summaryTable);
            
            // Detailed Answers
            Paragraph detailsTitle = new Paragraph()
                .add("\nDETAILED ANSWERS\n")
                .setBold()
                .setMarginTop(20)
                .setMarginBottom(10);
            document.add(detailsTitle);
            
            Table detailsTable = new Table(4);
            detailsTable.addHeaderCell("Question");
            detailsTable.addHeaderCell("Your Answer");
            detailsTable.addHeaderCell("Correct Answer");
            detailsTable.addHeaderCell("Status");
            
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                String studentAnswer = studentAnswers.getOrDefault(i, "Not Answered");
                String correctAnswer = q.getCorrectOption();
                boolean isCorrect = studentAnswer.equals(correctAnswer);
                
                detailsTable.addCell("Q" + (i + 1) + ": " + q.getQuestion());
                detailsTable.addCell(studentAnswer);
                detailsTable.addCell(correctAnswer);
                detailsTable.addCell(isCorrect ? "✓ Correct" : "✗ Wrong");
            }
            
            document.add(detailsTable);
            
            // Footer
            Paragraph footer = new Paragraph()
                .add("\n\nThis is a computer-generated document.")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic();
            document.add(footer);
            
            document.close();
            
            javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText(null);
            successAlert.setContentText("PDF saved successfully to:\n" + file.getAbsolutePath());
            successAlert.showAndWait();
            
        } catch (FileNotFoundException e) {
            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Failed to save PDF: " + e.getMessage());
            errorAlert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("An error occurred while generating PDF: " + e.getMessage());
            errorAlert.showAndWait();
        }
    }
}
