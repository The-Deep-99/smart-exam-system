package com.exam.view;

import com.exam.dao.QuestionDAO;
import com.exam.dao.SubjectDAO;
import com.exam.model.Question;
import com.exam.model.Subject;
import com.exam.util.FileProcessor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class FileUploadView {
    private Stage stage;
    private ComboBox<Subject> subjectComboBox;
    private Label statusLabel;

    public FileUploadView() {
        this.stage = new Stage();
        this.stage.setTitle("Upload Questions");
        this.stage.setResizable(true);
        this.stage.setMinWidth(600);
        this.stage.setMinHeight(400);
    }

    public void show() {
        BorderPane rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: #f5f5f5;");

        HBox topBar = createTopBar();
        rootLayout.setTop(topBar);

        VBox centerContent = createCenterContent();
        rootLayout.setCenter(centerContent);

        Scene scene = new Scene(rootLayout, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        backBtn.setTooltip(new Tooltip("Back to Admin Panel"));
        backBtn.setOnAction(e -> {
            stage.close();
            new AdminView("admin").show();
        });

        Label titleLabel = new Label("Upload Questions");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        topBar.getChildren().addAll(backBtn, spacer, titleLabel);
        return topBar;
    }

    private VBox createCenterContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setAlignment(Pos.CENTER);

        Label title = new Label("Upload Questions from File");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#2c3e50"));

        subjectComboBox = new ComboBox<>();
        subjectComboBox.setPromptText("Select Subject");
        subjectComboBox.setPrefWidth(300);
        loadSubjects();

        Button csvUploadBtn = createUploadButton("Upload CSV File", "CSV files (*.csv)", "*.csv");
        Button excelUploadBtn = createUploadButton("Upload Excel File", "Excel files (*.xlsx)", "*.xlsx");
        Button pdfUploadBtn = createUploadButton("Upload PDF & Generate MCQs", "PDF files (*.pdf)", "*.pdf");

        statusLabel = new Label("");
        statusLabel.setTextFill(Color.DARKGREEN);
        statusLabel.setFont(Font.font("Arial", 12));

        content.getChildren().addAll(
            title,
            new Label("Select Subject:"),
            subjectComboBox,
            csvUploadBtn,
            excelUploadBtn,
            pdfUploadBtn,
            statusLabel
        );

        return content;
    }

    private Button createUploadButton(String text, String description, String extension) {
        Button button = new Button(text);
        button.setPrefSize(250, 40);
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));

        button.setOnAction(e -> {
            Subject selectedSubject = subjectComboBox.getValue();
            if (selectedSubject == null) {
                showAlert(Alert.AlertType.WARNING, "Please select a subject first.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose " + description);
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extension));
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                processFile(selectedFile, selectedSubject.getId(), text);
            }
        });

        return button;
    }

    private void processFile(File file, int subjectId, String uploadType) {
        try {
            List<Question> questions = null;
            
            if (uploadType.contains("CSV")) {
                questions = FileProcessor.processCSVFile(file, subjectId);
            } else if (uploadType.contains("Excel")) {
                questions = FileProcessor.processExcelFile(file, subjectId);
            } else if (uploadType.contains("PDF")) {
                String pdfText = FileProcessor.extractTextFromPDF(file);
                questions = FileProcessor.generateMCQsFromText(pdfText, subjectId);
            }

            if (questions != null && !questions.isEmpty()) {
                for (Question question : questions) {
                    QuestionDAO.addQuestion(question, subjectId);
                }
                statusLabel.setText("Successfully uploaded " + questions.size() + " questions!");
                statusLabel.setTextFill(Color.DARKGREEN);
            } else {
                statusLabel.setText("No questions found in the file.");
                statusLabel.setTextFill(Color.RED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error processing file: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }

    private void loadSubjects() {
        List<Subject> subjects = SubjectDAO.getAllSubjects();
        subjectComboBox.getItems().addAll(subjects);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
