package com.exam.view;

import com.exam.dao.QuestionDAO;
import com.exam.dao.ResultDAO;
import com.exam.dao.ExamDAO;
import com.exam.model.Question;
import com.exam.model.Exam;
import com.exam.service.PDFService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.effect.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ExamView {
    private Stage stage;
    private String username;
    private int subjectId;
    private String subjectName;
    private List<Question> questions;
    private Map<Integer, String> studentAnswers;
    private int currentQuestionIndex = 0;

    private Label progressLabel;
    private Label questionLabel;
    private ToggleGroup optionGroup;
    private RadioButton[] optionButtons;
    private Button nextBtn;
    private Button prevBtn;
    private Button submitBtn;
    private ProgressBar progressBar;
    private Label timerLabel;
    private int examDurationMinutes = 30; // 30 minutes default
    private AtomicInteger timeRemainingSeconds;
    private Timeline timer;

    public ExamView(String username, int subjectId, String subjectName) {
        this.stage = new Stage();
        this.username = username;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.studentAnswers = new HashMap<>();
        this.questions = QuestionDAO.getQuestionsBySubject(subjectId);
        this.timeRemainingSeconds = new AtomicInteger(examDurationMinutes * 60);
        
        initializeUI();
    }

    public ExamView(String username, String examCode) {
        this.stage = new Stage();
        this.username = username;
        this.studentAnswers = new HashMap<>();
        
        Exam exam = ExamDAO.getExamByCode(examCode);
        if (exam != null) {
            this.subjectId = exam.getSubjectId();
            this.subjectName = exam.getSubjectName();
            this.examDurationMinutes = exam.getDurationMinutes();
            this.questions = QuestionDAO.getQuestionsBySubject(exam.getSubjectId());
            this.timeRemainingSeconds = new AtomicInteger(examDurationMinutes * 60);
        } else {
            this.questions = List.of();
            this.timeRemainingSeconds = new AtomicInteger(0);
        }
        
        initializeUI();
    }

    public void show() {
        if (questions.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Info");
            alert.setHeaderText(null);
            alert.setContentText("No questions available for this subject");
            alert.showAndWait();
            return;
        }
        
        displayQuestion(0);
        setupSecurityFeatures(); // Setup security after scene is created
        startTimer(); // Start the countdown timer
        stage.show();
    }
    
    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int remaining = timeRemainingSeconds.decrementAndGet();
            updateTimerDisplay();
            if (remaining <= 0) {
                // Time's up - auto submit
                timer.stop();
                Alert timeUpAlert = new Alert(Alert.AlertType.WARNING);
                timeUpAlert.setTitle("Time's Up!");
                timeUpAlert.setHeaderText("Exam Time Expired");
                timeUpAlert.setContentText("Your exam has been automatically submitted.");
                timeUpAlert.showAndWait();
                calculateAndDisplayScore();
            } else if (remaining <= 300) { // Last 5 minutes - change color to red
                timerLabel.setTextFill(Color.web("#e74c3c"));
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    
    private void updateTimerDisplay() {
        int totalSeconds = timeRemainingSeconds.get();
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
    }

    private void initializeUI() {
        stage.setTitle("📝 Online Exam - " + subjectName);
        stage.setWidth(950);
        stage.setHeight(750);
        stage.setResizable(false);
        stage.setFullScreen(false);
        stage.setAlwaysOnTop(true); // Keep window on top during exam
        
        // Prevent window closing during exam
        stage.setOnCloseRequest(e -> {
            e.consume();
            showSecurityWarning("Cannot close window during exam!");
        });
        
        // Prevent window iconification (minimization)
        stage.iconifiedProperty().addListener((obs, wasIconified, isNowIconified) -> {
            if (isNowIconified) {
                Platform.runLater(() -> {
                    stage.setIconified(false);
                    stage.requestFocus();
                    showSecurityWarning("Minimizing the window is not allowed during exam!");
                });
            }
        });

        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(25));
        
        // Modern gradient background
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#94b4c1")),
            new Stop(1, Color.web("#547792"))
        );
        mainBox.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        // Modern top panel with enhanced styling
        VBox topBox = createModernTopPanel();

        // Progress bar with modern styling
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(12);
        progressBar.setStyle(
            "-fx-accent: #3498db; -fx-control-inner-background: #e9ecef; " +
            "-fx-background-radius: 6; -fx-background-insets: 0;"
        );

        // Question panel with modern card design
        VBox questionCard = createModernQuestionCard();

        // Bottom panel with modern button styling
        HBox buttonBox = createModernButtonPanel();

        mainBox.getChildren().addAll(topBox, progressBar, questionCard, buttonBox);

        Scene scene = new Scene(mainBox);
        stage.setScene(scene);
    }

    private void displayQuestion(int index) {
        if (index < 0 || index >= questions.size()) {
            return;
        }

        currentQuestionIndex = index;
        Question q = questions.get(index);

        // Update question text
        questionLabel.setText("Q" + (index + 1) + ": " + q.getQuestion());

        // Update option buttons
        optionButtons[0].setText("A. " + q.getOptionA());
        optionButtons[1].setText("B. " + q.getOptionB());
        optionButtons[2].setText("C. " + q.getOptionC());
        optionButtons[3].setText("D. " + q.getOptionD());

        // Restore student's previous answer if exists
        optionGroup.selectToggle(null);
        if (studentAnswers.containsKey(index)) {
            String answer = studentAnswers.get(index);
            int answerIndex = answer.charAt(0) - 'A';
            if (answerIndex >= 0 && answerIndex < 4) {
                optionButtons[answerIndex].setSelected(true);
            }
        }

        updateProgressLabel();
        updateButtonStates();
        progressBar.setProgress((double) (index + 1) / questions.size());
    }

    private void saveCurrentAnswer() {
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected()) {
                studentAnswers.put(currentQuestionIndex, String.valueOf((char) ('A' + i)));
                return;
            }
        }
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
        prevBtn.setDisable(currentQuestionIndex == 0);
        nextBtn.setDisable(currentQuestionIndex == questions.size() - 1);
    }

    private void submitExam() {
        saveCurrentAnswer();

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Submit Exam");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to submit the exam?\nYou have answered " + 
            studentAnswers.size() + " out of " + questions.size() + " questions.");
        
        // Fix z-index issue
        confirmAlert.initOwner(stage);
        confirmAlert.initModality(Modality.APPLICATION_MODAL);

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                calculateAndDisplayScore();
            }
        });
    }

    private void calculateAndDisplayScore() {
        // Stop timer
        if (timer != null) {
            timer.stop();
        }
        
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

        // Show result with PDF download option
        showResultDialog(score, questions.size(), percentage);
    }
    
    private void showResultDialog(int score, int total, double percentage) {
        Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
        resultAlert.setTitle("Exam Result");
        resultAlert.setHeaderText("Exam Submitted Successfully!");
        
        String resultText = String.format(
            "Subject: %s\n" +
            "Score: %d out of %d\n" +
            "Percentage: %.2f%%\n" +
            "Status: %s\n\n" +
            "Would you like to download your result as PDF?",
            subjectName, score, total, percentage,
            percentage >= 60 ? "PASSED" : "FAILED"
        );
        
        resultAlert.setContentText(resultText);
        
        ButtonType downloadBtn = new ButtonType("Download PDF");
        ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        resultAlert.getButtonTypes().setAll(downloadBtn, closeBtn);
        
        resultAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == downloadBtn) {
                PDFService.generateResultPDF(username, subjectName, score, total, percentage, studentAnswers, questions);
            }
        });

        stage.close();
        new StudentView(username).show();
    }

    private VBox createModernTopPanel() {
        VBox topBox = new VBox(15);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(20));
        topBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(15), Insets.EMPTY)));
        topBox.setEffect(new DropShadow(10, Color.web("#000000", 0.1)));

        // Header row
        HBox headerRow = new HBox(20);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label headerLabel = new Label("📝 " + subjectName + " - 👤 " + username);
        headerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        headerLabel.setTextFill(Color.web("#2c3e50"));

        progressLabel = new Label();
        updateProgressLabel();
        progressLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        progressLabel.setTextFill(Color.web("#666666"));
        progressLabel.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-padding: 5 15;");

        // Timer with modern styling
        timerLabel = new Label();
        timerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        timerLabel.setTextFill(Color.WHITE);
        timerLabel.setStyle(
            "-fx-background-color: #e74c3c; -fx-background-radius: 25; -fx-padding: 8 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 4);"
        );
        updateTimerDisplay();

        HBox.setHgrow(headerLabel, Priority.ALWAYS);
        headerRow.getChildren().addAll(headerLabel, progressLabel, timerLabel);

        topBox.getChildren().add(headerRow);
        return topBox;
    }
    
    private VBox createModernQuestionCard() {
        VBox questionCard = new VBox(25);
        questionCard.setPadding(new Insets(30));
        questionCard.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(20), Insets.EMPTY)));
        questionCard.setEffect(new DropShadow(15, Color.web("#000000", 0.15)));

        questionLabel = new Label();
        questionLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        questionLabel.setWrapText(true);
        questionLabel.setTextFill(Color.web("#2c3e50"));

        VBox optionsBox = new VBox(20);
        optionsBox.setPadding(new Insets(10, 0, 0, 0));

        optionGroup = new ToggleGroup();
        optionButtons = new RadioButton[4];

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createModernRadioButton();
            optionButtons[i].setToggleGroup(optionGroup);
            optionsBox.getChildren().add(optionButtons[i]);
        }

        questionCard.getChildren().addAll(questionLabel, optionsBox);
        return questionCard;
    }
    
    private RadioButton createModernRadioButton() {
        RadioButton radio = new RadioButton();
        radio.setFont(Font.font("Segoe UI", 14));
        radio.setWrapText(true);
        radio.setTextFill(Color.web("#2c3e50"));
        radio.setStyle(
            "-fx-font-size: 14px; -fx-padding: 10; -fx-spacing: 10; " +
            "-fx-background-color: #f8f9fa; -fx-background-radius: 10; " +
            "-fx-border-color: #e9ecef; -fx-border-radius: 10; -fx-border-width: 2;"
        );
        
        radio.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                radio.setStyle(
                    "-fx-font-size: 14px; -fx-padding: 10; -fx-spacing: 10; " +
                    "-fx-background-color: #e3f2fd; -fx-background-radius: 10; " +
                    "-fx-border-color: #3498db; -fx-border-radius: 10; -fx-border-width: 2;"
                );
            } else {
                radio.setStyle(
                    "-fx-font-size: 14px; -fx-padding: 10; -fx-spacing: 10; " +
                    "-fx-background-color: #f8f9fa; -fx-background-radius: 10; " +
                    "-fx-border-color: #e9ecef; -fx-border-radius: 10; -fx-border-width: 2;"
                );
            }
        });
        
        radio.hoverProperty().addListener((obs, wasHovered, isHovered) -> {
            if (isHovered && !radio.isSelected()) {
                radio.setStyle(
                    "-fx-font-size: 14px; -fx-padding: 10; -fx-spacing: 10; " +
                    "-fx-background-color: #f1f3f4; -fx-background-radius: 10; " +
                    "-fx-border-color: #3498db; -fx-border-radius: 10; -fx-border-width: 2;"
                );
            } else if (!radio.isSelected()) {
                radio.setStyle(
                    "-fx-font-size: 14px; -fx-padding: 10; -fx-spacing: 10; " +
                    "-fx-background-color: #f8f9fa; -fx-background-radius: 10; " +
                    "-fx-border-color: #e9ecef; -fx-border-radius: 10; -fx-border-width: 2;"
                );
            }
        });
        
        return radio;
    }
    
    private HBox createModernButtonPanel() {
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(25));

        prevBtn = createModernExamButton("⬅ Previous", "#95a5a6", "#7f8c8d");
        prevBtn.setOnAction(e -> previousQuestion());
        prevBtn.setDisable(true);

        nextBtn = createModernExamButton("Next ➡", "#3498db", "#2980b9");
        nextBtn.setOnAction(e -> nextQuestion());

        submitBtn = createModernExamButton("🚩 Submit Exam", "#e74c3c", "#c0392b");
        submitBtn.setOnAction(e -> submitExam());

        buttonBox.getChildren().addAll(prevBtn, nextBtn, submitBtn);
        return buttonBox;
    }
    
    private Button createModernExamButton(String text, String primaryColor, String hoverColor) {
        Button btn = new Button(text);
        btn.setPrefWidth(150);
        btn.setPrefHeight(50);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btn.setTextFill(Color.WHITE);
        
        // Modern styling with rounded corners and shadow
        String baseStyle = String.format(
            "-fx-background-color: %s; -fx-background-radius: 15; -fx-border-radius: 15; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);",
            primaryColor
        );
        btn.setStyle(baseStyle);
        
        // Smooth hover animations
        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
                scale.setToX(1.05);
                scale.setToY(1.05);
                scale.play();
                
                String hoverStyle = String.format(
                    "-fx-background-color: %s; -fx-background-radius: 15; -fx-border-radius: 15; " +
                    "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 8);",
                    hoverColor
                );
                btn.setStyle(hoverStyle);
            }
        });
        
        btn.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
            btn.setStyle(baseStyle);
        });
        
        // Press animation
        btn.setOnMousePressed(e -> {
            if (!btn.isDisabled()) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(100), btn);
                scale.setToX(0.95);
                scale.setToY(0.95);
                scale.play();
            }
        });
        
        btn.setOnMouseReleased(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), btn);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
        
        return btn;
    }

    private void setupSecurityFeatures() {
        Scene scene = stage.getScene();
        if (scene == null) {
            return;
        }

        // Disable copy, cut, paste shortcuts
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            // Disable Ctrl+C (Copy)
            if (e.isControlDown() && e.getCode() == KeyCode.C) {
                e.consume();
                showSecurityWarning("Copy is disabled during exam!");
            }
            // Disable Ctrl+V (Paste)
            else if (e.isControlDown() && e.getCode() == KeyCode.V) {
                e.consume();
                showSecurityWarning("Paste is disabled during exam!");
            }
            // Disable Ctrl+X (Cut)
            else if (e.isControlDown() && e.getCode() == KeyCode.X) {
                e.consume();
                showSecurityWarning("Cut is disabled during exam!");
            }
            // Disable Ctrl+A (Select All)
            else if (e.isControlDown() && e.getCode() == KeyCode.A) {
                e.consume();
                showSecurityWarning("Select All is disabled during exam!");
            }
            // Disable Alt+Tab and Ctrl+Tab (Tab switching)
            else if ((e.isAltDown() || e.isControlDown()) && e.getCode() == KeyCode.TAB) {
                e.consume();
                showSecurityWarning("Tab switching is disabled during exam!");
            }
            // Disable F5 (Refresh)
            else if (e.getCode() == KeyCode.F5) {
                e.consume();
                showSecurityWarning("Refresh is disabled during exam!");
            }
            // Disable Windows key combinations
            else if (e.isMetaDown() || e.isShortcutDown()) {
                if (e.getCode() == KeyCode.TAB || e.getCode() == KeyCode.D) {
                    e.consume();
                    showSecurityWarning("Window switching is disabled during exam!");
                }
            }
        });

        // Disable context menu (right-click)
        scene.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
            if (e.isSecondaryButtonDown()) {
                e.consume();
                showSecurityWarning("Right-click is disabled during exam!");
            }
        });

        // Prevent window focus loss
        stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (wasFocused && !isNowFocused) {
                Platform.runLater(() -> {
                    stage.requestFocus();
                    showSecurityWarning("Please stay focused on the exam window!");
                });
            }
        });

        // Disable text selection on all components
        scene.getRoot().setStyle("-fx-user-select: none;");
    }

    private void showSecurityWarning(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Anti-Cheat");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
