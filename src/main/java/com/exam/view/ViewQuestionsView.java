package com.exam.view;

import com.exam.dao.QuestionDAO;
import com.exam.dao.SubjectDAO;
import com.exam.model.Question;
import com.exam.model.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

public class ViewQuestionsView {
    private Stage stage;

    public ViewQuestionsView() {
        this.stage = new Stage();
        this.stage.setTitle("All Questions");
        this.stage.setResizable(true);
    }

    private TableView<Question> table;
    private ObservableList<Question> questionList;
    private FilteredList<Question> filteredList;
    private ComboBox<Subject> subjectFilter;

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

        Scene scene = new Scene(rootLayout, 900, 600);
        stage.setScene(scene);
        stage.show();

        // Refresh questions every time the window is shown
        stage.setOnShown(e -> refreshQuestions());
    }

    private VBox createMainContent() {
        VBox mainBox = new VBox(15);
        mainBox.setPadding(new Insets(20));

        Label header = new Label("All Questions");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        header.setTextFill(Color.web("#2c3e50"));

        table = new TableView<>();
        table.setEditable(false);

        TableColumn<Question, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Question, String> questionCol = new TableColumn<>("Question");
        questionCol.setCellValueFactory(new PropertyValueFactory<>("question"));
        questionCol.setPrefWidth(250);

        TableColumn<Question, String> optionACol = new TableColumn<>("A");
        optionACol.setCellValueFactory(new PropertyValueFactory<>("optionA"));
        optionACol.setPrefWidth(150);

        TableColumn<Question, String> optionBCol = new TableColumn<>("B");
        optionBCol.setCellValueFactory(new PropertyValueFactory<>("optionB"));
        optionBCol.setPrefWidth(150);

        TableColumn<Question, String> optionCCol = new TableColumn<>("C");
        optionCCol.setCellValueFactory(new PropertyValueFactory<>("optionC"));
        optionCCol.setPrefWidth(150);

        TableColumn<Question, String> optionDCol = new TableColumn<>("D");
        optionDCol.setCellValueFactory(new PropertyValueFactory<>("optionD"));
        optionDCol.setPrefWidth(150);

        TableColumn<Question, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        subjectCol.setPrefWidth(150);

        TableColumn<Question, String> correctCol = new TableColumn<>("Correct");
        correctCol.setCellValueFactory(new PropertyValueFactory<>("correctOption"));
        correctCol.setPrefWidth(80);

        table.getColumns().addAll(idCol, subjectCol, questionCol, optionACol, optionBCol, optionCCol, optionDCol, correctCol);

        // Subject filter
        Label filterLabel = new Label("Filter by Subject:");
        filterLabel.setFont(Font.font("Arial", 12));
        
        subjectFilter = new ComboBox<>();
        subjectFilter.getItems().add(null); // "All Subjects" option
        subjectFilter.getItems().addAll(SubjectDAO.getAllSubjects());
        subjectFilter.setPrefWidth(200);
        subjectFilter.setButtonCell(new javafx.scene.control.ListCell<Subject>() {
            @Override
            protected void updateItem(Subject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("All Subjects");
                } else {
                    setText(item.getName());
                }
            }
        });
        subjectFilter.setCellFactory(param -> new javafx.scene.control.ListCell<Subject>() {
            @Override
            protected void updateItem(Subject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("All Subjects");
                } else {
                    setText(item.getName());
                }
            }
        });
        subjectFilter.getSelectionModel().selectFirst();
        subjectFilter.setOnAction(e -> applyFilter());
        
        // Refresh button
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5 15 5 15;");
        refreshBtn.setOnAction(e -> refreshQuestions());
        
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(header, filterLabel, subjectFilter, refreshBtn);
        
        // Refresh questions when view is shown
        refreshQuestions();

        mainBox.getChildren().addAll(headerBox, table);
        return mainBox;
    }
    
    private void refreshQuestions() {
        List<Question> questions = QuestionDAO.getAllQuestions();
        questionList = FXCollections.observableArrayList(questions);
        filteredList = new FilteredList<>(questionList, p -> true);
        table.setItems(filteredList);
        applyFilter(); // Apply current filter
    }
    
    private void applyFilter() {
        Subject selectedSubject = subjectFilter.getSelectionModel().getSelectedItem();
        if (selectedSubject == null) {
            // Show all questions
            filteredList.setPredicate(question -> true);
        } else {
            // Filter by selected subject
            filteredList.setPredicate(question -> 
                question.getSubjectId() == selectedSubject.getId());
        }
    }
}
