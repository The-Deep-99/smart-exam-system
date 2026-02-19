package com.exam.view;

import com.exam.dao.QuestionDAO;
import com.exam.dao.SubjectDAO;
import com.exam.model.Question;
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

public class AddQuestionView {
    private Stage stage;

    public AddQuestionView() {
        this.stage = new Stage();
        this.stage.setTitle("Add Question");
        this.stage.setResizable(true);
        this.stage.setMinWidth(500);
        this.stage.setMinHeight(400);
    }

    public void show() {
        BorderPane rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: #f5f5f5;");

        // Create top bar with back button
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        backBtn.setTooltip(new Tooltip("Back to Admin Panel"));
        backBtn.setOnAction(e -> {
            stage.close();
            new AdminView("admin").show(); // Assuming admin user
        });

        topBar.getChildren().add(backBtn);
        rootLayout.setTop(topBar);

        // Main content
        VBox mainBox = createMainContent();
        rootLayout.setCenter(mainBox);

        Scene scene = new Scene(rootLayout, 500, 600);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createMainContent() {
        VBox mainBox = new VBox(20);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(30));

        Label header = new Label("Add New Question");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        header.setTextFill(Color.web("#2c3e50"));

        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(15);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));

        // Subject dropdown
        List<Subject> subjects = SubjectDAO.getAllSubjects();
        ComboBox<Subject> subjectBox = new ComboBox<>();
        subjectBox.getItems().addAll(subjects);
        subjectBox.setPrefWidth(300);
        
        // Set cell factory for dropdown items
        subjectBox.setCellFactory(param -> new ListCell<Subject>() {
            @Override
            protected void updateItem(Subject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - " + item.getDescription());
                }
            }
        });
        
        // Set button cell for selected item display
        subjectBox.setButtonCell(new ListCell<Subject>() {
            @Override
            protected void updateItem(Subject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " - " + item.getDescription());
                }
            }
        });

        TextField qField = new TextField();
        qField.setPrefWidth(300);
        TextField aField = new TextField();
        aField.setPrefWidth(300);
        TextField bField = new TextField();
        bField.setPrefWidth(300);
        TextField cField = new TextField();
        cField.setPrefWidth(300);
        TextField dField = new TextField();
        dField.setPrefWidth(300);

        ComboBox<String> correctBox = new ComboBox<>();
        correctBox.getItems().addAll("A", "B", "C", "D");
        correctBox.setPrefWidth(300);
        correctBox.getSelectionModel().selectFirst();

        Button addBtn = createStyledButton("Add Question", "#27ae60");
        addBtn.setOnAction(e -> {
            if (subjectBox.getSelectionModel().getSelectedItem() == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please select a subject!");
                return;
            }

            Subject selectedSubject = subjectBox.getSelectionModel().getSelectedItem();

            if (qField.getText().trim().isEmpty() || aField.getText().trim().isEmpty() ||
                bField.getText().trim().isEmpty() || cField.getText().trim().isEmpty() ||
                dField.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please fill all fields!");
                return;
            }

            Question q = new Question(
                qField.getText(),
                aField.getText(),
                bField.getText(),
                cField.getText(),
                dField.getText(),
                correctBox.getSelectionModel().getSelectedItem()
            );

            QuestionDAO.addQuestion(q, selectedSubject.getId());
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "Question Added to " + selectedSubject.getName() + "!");

            // Clear fields
            qField.clear();
            aField.clear();
            bField.clear();
            cField.clear();
            dField.clear();
            
            // Note: ViewQuestionsView will auto-refresh when reopened
        });

        // Add labels and fields to grid
        formGrid.add(new Label("Subject:"), 0, 0);
        formGrid.add(subjectBox, 1, 0);
        formGrid.add(new Label("Question:"), 0, 1);
        formGrid.add(qField, 1, 1);
        formGrid.add(new Label("Option A:"), 0, 2);
        formGrid.add(aField, 1, 2);
        formGrid.add(new Label("Option B:"), 0, 3);
        formGrid.add(bField, 1, 3);
        formGrid.add(new Label("Option C:"), 0, 4);
        formGrid.add(cField, 1, 4);
        formGrid.add(new Label("Option D:"), 0, 5);
        formGrid.add(dField, 1, 5);
        formGrid.add(new Label("Correct Option:"), 0, 6);
        formGrid.add(correctBox, 1, 6);
        formGrid.add(addBtn, 1, 7);

        mainBox.getChildren().addAll(header, formGrid);
        return mainBox;
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
