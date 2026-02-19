package com.exam.view;

import com.exam.dao.ExamDAO;
import com.exam.dao.SubjectDAO;
import com.exam.model.Exam;
import com.exam.model.Subject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class CreateExamView {
    private Stage stage;
    private ComboBox<Subject> subjectComboBox;
    private TextField titleField;
    private TextField durationField;
    private TextField questionCountField;
    private Label generatedCodeLabel;
    private Label statusLabel;

    public CreateExamView() {
        this.stage = new Stage();
        this.stage.setTitle("Create Exam");
        this.stage.setResizable(true);
        this.stage.setMinWidth(500);
        this.stage.setMinHeight(450);
    }

    public void show() {
        BorderPane rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: #f5f5f5;");

        HBox topBar = createTopBar();
        rootLayout.setTop(topBar);

        VBox centerContent = createCenterContent();
        rootLayout.setCenter(centerContent);

        Scene scene = new Scene(rootLayout, 500, 450);
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

        Label titleLabel = new Label("Create Exam");
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

        Label title = new Label("Create New Exam");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#2c3e50"));

        GridPane formGrid = createFormGrid();

        Button generateBtn = new Button("Generate Exam Code");
        generateBtn.setPrefSize(200, 40);
        generateBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        generateBtn.setOnMouseEntered(e -> generateBtn.setStyle("-fx-background-color: #229954; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));
        generateBtn.setOnMouseExited(e -> generateBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));

        generatedCodeLabel = new Label("");
        generatedCodeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        generatedCodeLabel.setTextFill(Color.web("#e74c3c"));

        statusLabel = new Label("");
        statusLabel.setTextFill(Color.DARKGREEN);
        statusLabel.setFont(Font.font("Arial", 12));

        generateBtn.setOnAction(e -> generateExam());

        content.getChildren().addAll(title, formGrid, generateBtn, generatedCodeLabel, statusLabel);
        return content;
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        Label subjectLabel = new Label("Subject:");
        subjectLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(subjectLabel, 0, 0);

        subjectComboBox = new ComboBox<>();
        subjectComboBox.setPromptText("Select Subject");
        subjectComboBox.setPrefWidth(200);
        loadSubjects();
        grid.add(subjectComboBox, 1, 0);

        Label titleLabel = new Label("Exam Title:");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(titleLabel, 0, 1);

        titleField = new TextField();
        titleField.setPromptText("Enter exam title");
        titleField.setPrefWidth(200);
        grid.add(titleField, 1, 1);

        Label durationLabel = new Label("Duration (minutes):");
        durationLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(durationLabel, 0, 2);

        durationField = new TextField();
        durationField.setPromptText("Enter duration");
        durationField.setPrefWidth(200);
        grid.add(durationField, 1, 2);

        Label questionCountLabel = new Label("Number of Questions:");
        questionCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(questionCountLabel, 0, 3);

        questionCountField = new TextField();
        questionCountField.setPromptText("Enter number of questions");
        questionCountField.setPrefWidth(200);
        grid.add(questionCountField, 1, 3);

        return grid;
    }

    private void loadSubjects() {
        List<Subject> subjects = SubjectDAO.getAllSubjects();
        subjectComboBox.getItems().addAll(subjects);
    }

    private void generateExam() {
        Subject selectedSubject = subjectComboBox.getValue();
        String title = titleField.getText().trim();
        String durationText = durationField.getText().trim();
        String questionCountText = questionCountField.getText().trim();

        if (selectedSubject == null || title.isEmpty() || durationText.isEmpty() || questionCountText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
            return;
        }

        try {
            int duration = Integer.parseInt(durationText);
            int questionCount = Integer.parseInt(questionCountText);

            if (duration <= 0 || questionCount <= 0) {
                showAlert(Alert.AlertType.WARNING, "Duration and question count must be positive numbers.");
                return;
            }

            String examCode;
            do {
                examCode = ExamDAO.generateExamCode();
            } while (!ExamDAO.isExamCodeUnique(examCode));

            Exam exam = new Exam(examCode, title, selectedSubject.getId(), duration, questionCount, "admin");
            ExamDAO.createExam(exam);

            generatedCodeLabel.setText("Exam Code: " + examCode);
            statusLabel.setText("Exam created successfully! Share this code with students.");
            statusLabel.setTextFill(Color.DARKGREEN);

            clearForm();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Please enter valid numbers for duration and question count.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error creating exam: " + e.getMessage());
        }
    }

    private void clearForm() {
        titleField.clear();
        durationField.clear();
        questionCountField.clear();
        subjectComboBox.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
