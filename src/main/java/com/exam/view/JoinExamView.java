package com.exam.view;

import com.exam.dao.ExamDAO;
import com.exam.dao.ExamSessionDAO;
import com.exam.model.Exam;
import com.exam.model.ExamSession;
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
import javafx.stage.Stage;

public class JoinExamView {
    private Stage stage;
    private TextField examCodeField;
    private Label examInfoLabel;
    private Button startExamButton;
    private Exam currentExam;
    private String studentUsername;

    public JoinExamView(String studentUsername) {
        this.stage = new Stage();
        this.studentUsername = studentUsername;
        this.stage.setTitle("Join Exam");
        this.stage.setResizable(true);
        this.stage.setMinWidth(400);
        this.stage.setMinHeight(300);
    }

    public void show() {
        BorderPane rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: #f5f5f5;");

        HBox topBar = createTopBar();
        rootLayout.setTop(topBar);

        VBox centerContent = createCenterContent();
        rootLayout.setCenter(centerContent);

        Scene scene = new Scene(rootLayout, 400, 300);
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
        backBtn.setTooltip(new Tooltip("Back to Student Dashboard"));
        backBtn.setOnAction(e -> {
            stage.close();
            new StudentView(studentUsername).show();
        });

        Label titleLabel = new Label("Join Exam");
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

        Label title = new Label("Enter Exam Code");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.web("#2c3e50"));

        examCodeField = new TextField();
        examCodeField.setPromptText("Enter 6-digit exam code");
        examCodeField.setPrefWidth(200);
        examCodeField.setStyle("-fx-font-size: 16px; -fx-alignment: center;");
        examCodeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[A-Z0-9]*")) {
                examCodeField.setText(newVal.toUpperCase().replaceAll("[^A-Z0-9]", ""));
            }
            if (newVal.length() > 6) {
                examCodeField.setText(newVal.substring(0, 6));
            }
            checkExamCode();
        });

        Button verifyBtn = new Button("Verify Exam");
        verifyBtn.setPrefSize(150, 40);
        verifyBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        verifyBtn.setOnMouseEntered(e -> verifyBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));
        verifyBtn.setOnMouseExited(e -> verifyBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));

        verifyBtn.setOnAction(e -> checkExamCode());

        examInfoLabel = new Label("");
        examInfoLabel.setFont(Font.font("Arial", 12));
        examInfoLabel.setTextFill(Color.DARKBLUE);

        startExamButton = new Button("Start Exam");
        startExamButton.setPrefSize(150, 40);
        startExamButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        startExamButton.setOnMouseEntered(e -> startExamButton.setStyle("-fx-background-color: #229954; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));
        startExamButton.setOnMouseExited(e -> startExamButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"));
        startExamButton.setVisible(false);
        startExamButton.setDisable(true);

        startExamButton.setOnAction(e -> startExam());

        content.getChildren().addAll(title, examCodeField, verifyBtn, examInfoLabel, startExamButton);
        return content;
    }

    private void checkExamCode() {
        String examCode = examCodeField.getText().trim();
        
        if (examCode.length() != 6) {
            examInfoLabel.setText("");
            startExamButton.setVisible(false);
            startExamButton.setDisable(true);
            return;
        }

        currentExam = ExamDAO.getExamByCode(examCode);
        
        if (currentExam == null) {
            examInfoLabel.setText("Invalid exam code. Please check and try again.");
            examInfoLabel.setTextFill(Color.RED);
            startExamButton.setVisible(false);
            startExamButton.setDisable(true);
        } else {
            if (ExamSessionDAO.hasActiveSession(examCode, studentUsername)) {
                examInfoLabel.setText("You already have an active session for this exam.");
                examInfoLabel.setTextFill(Color.ORANGE);
                startExamButton.setVisible(false);
                startExamButton.setDisable(true);
            } else {
                examInfoLabel.setText("Exam Found: " + currentExam.getTitle() + 
                    "\nSubject: " + currentExam.getSubjectName() + 
                    "\nDuration: " + currentExam.getDurationMinutes() + " minutes" +
                    "\nQuestions: " + currentExam.getQuestionCount());
                examInfoLabel.setTextFill(Color.DARKGREEN);
                startExamButton.setVisible(true);
                startExamButton.setDisable(false);
            }
        }
    }

    private void startExam() {
        if (currentExam == null) {
            showAlert(Alert.AlertType.ERROR, "No exam selected.");
            return;
        }

        try {
            ExamSession session = new ExamSession(currentExam.getExamCode(), studentUsername, currentExam.getQuestionCount());
            ExamSessionDAO.createExamSession(session);

            stage.close();
            new ExamView(studentUsername, currentExam.getExamCode()).show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error starting exam: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
