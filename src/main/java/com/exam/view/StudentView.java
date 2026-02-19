package com.exam.view;

import com.exam.dao.SubjectDAO;
import com.exam.dao.UserDAO;
import com.exam.model.Subject;
import com.exam.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.effect.*;
import java.io.File;
import java.util.List;

public class StudentView {
    private Stage stage;
    private String studentName;
    private BorderPane rootLayout;
    private boolean isFullscreen = false;
    private boolean isDarkMode = false;
    private String profilePicturePath = null;
    private Button profileBtn;

    public StudentView(String studentName) {
        this.stage = new Stage();
        this.studentName = studentName;
        this.stage.setTitle("Student Panel - " + studentName);
        this.stage.setResizable(true);
        this.stage.setMinWidth(600);
        this.stage.setMinHeight(400);

        // Load profile picture from database
        loadProfilePictureFromDatabase();
    }

    public void show() {
        rootLayout = new BorderPane();
        applyTheme();

        // Create top bar with profile
        HBox topBar = createTopBar();
        rootLayout.setTop(topBar);

        // Main content
        VBox mainBox = createMainContent();
        rootLayout.setCenter(mainBox);

        Scene scene = new Scene(rootLayout, 600, 500);
        stage.setScene(scene);
        stage.show();
    }

    private void loadProfilePictureFromDatabase() {
        User user = UserDAO.getUserByUsername(studentName);
        if (user != null && user.getProfilePicturePath() != null) {
            profilePicturePath = user.getProfilePicturePath();
        }
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15));
        
        // Modern gradient background
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#547792")),
            new Stop(1, Color.web("#213448"))
        );
        topBar.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Back button with modern styling
        Button backBtn = createModernTopBarButton("← Back to Login");
        backBtn.setOnAction(e -> {
            stage.close();
            new LoginView().show();
        });

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Fullscreen toggle button
        Button fullscreenBtn = createModernTopBarButton(isFullscreen ? "⛶ Exit Fullscreen" : "⛶ Fullscreen");
        fullscreenBtn.setOnAction(e -> {
            toggleFullscreen();
            fullscreenBtn.setText(isFullscreen ? "⛶ Exit Fullscreen" : "⛶ Fullscreen");
        });

        // Profile button with enhanced styling
        profileBtn = new Button();
        profileBtn.setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-border-width: 2; -fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand;");
        profileBtn.setPrefSize(32, 32);
        profileBtn.setMinSize(32, 32);
        profileBtn.setMaxSize(32, 32);
        profileBtn.setTooltip(new Tooltip("Profile Menu"));
        profileBtn.setGraphic(createProfileGraphic());
        profileBtn.setOnAction(e -> showProfileMenu(profileBtn));

        topBar.getChildren().addAll(backBtn, spacer, fullscreenBtn, profileBtn);
        HBox.setMargin(fullscreenBtn, new Insets(0, 15, 0, 0));

        return topBar;
    }

    private ImageView createProfileGraphic() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(24);
        imageView.setFitHeight(24);
        imageView.setPreserveRatio(false); // Allow stretching to fill circle

        // Make it circular using clip
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(12, 12, 12);
        imageView.setClip(clip);

        if (profilePicturePath != null) {
            try {
                Image image = new Image("file:" + profilePicturePath);
                imageView.setImage(image);
            } catch (Exception e) {
                // If image loading fails, use default icon
                imageView.setImage(null);
            }
        }

        if (imageView.getImage() == null) {
            // Default profile icon - create a simple colored circle
            imageView.setStyle("-fx-background-color: #3498db;");
        }

        return imageView;
    }

    private VBox createMainContent() {
        VBox mainBox = new VBox(30);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(40));

        // Modern gradient background
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#94b4c1")),
            new Stop(1, Color.web("#547792"))
        );
        mainBox.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        // Header with modern styling
        Label header = new Label("📚 Select Subject for MCQ Test");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        header.setTextFill(Color.web("#2c3e50"));
        header.setEffect(new DropShadow(10, Color.web("#000000", 0.2)));
        
        // Welcome message
        Label welcomeLabel = new Label("Welcome back, " + studentName + "! Choose a subject to begin.");
        welcomeLabel.setFont(Font.font("Segoe UI", 16));
        welcomeLabel.setTextFill(Color.web("#666666"));

        // Subjects panel with modern card design
        VBox subjectsCard = createModernCard();
        subjectsCard.setPadding(new Insets(25));
        subjectsCard.setMaxWidth(500);
        subjectsCard.setMinHeight(300);

        VBox subjectsBox = new VBox(15);
        subjectsBox.setAlignment(Pos.CENTER);

        List<Subject> subjects = SubjectDAO.getAllSubjects();

        if (subjects.isEmpty()) {
            Label noSubjectsLabel = new Label("📭 No subjects available");
            noSubjectsLabel.setFont(Font.font("Segoe UI", 18));
            noSubjectsLabel.setTextFill(Color.web("#666666"));
            subjectsBox.getChildren().add(noSubjectsLabel);
        } else {
            for (Subject subject : subjects) {
                Button subjectBtn = createModernSubjectButton(subject.getName() + " - " + subject.getDescription());
                subjectBtn.setOnAction(e -> startExam(subject.getId(), subject.getName()));
                subjectsBox.getChildren().add(subjectBtn);
            }
        }

        ScrollPane scrollPane = new ScrollPane(subjectsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        subjectsCard.getChildren().add(scrollPane);

        // Bottom action panel
        HBox bottomBox = new HBox(20);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20, 0, 0, 0));

        Button statsBtn = createModernActionButton("📈 My Statistics", "#16a085", "#138d75");
        statsBtn.setOnAction(e -> new StudentStatsView(studentName).show());

        Button joinExamBtn = createModernActionButton("🎯 Join Exam", "#27ae60", "#229954");
        joinExamBtn.setOnAction(e -> new JoinExamView(studentName).show());

        Button resultsBtn = createModernActionButton("📋 View Results", "#9b59b6", "#8e44ad");
        resultsBtn.setOnAction(e -> new ResultsView(studentName).show());

        Button logoutBtn = createModernActionButton("🚪 Logout", "#e74c3c", "#c0392b");
        logoutBtn.setOnAction(e -> {
            stage.close();
            new LoginView().show();
        });

        bottomBox.getChildren().addAll(statsBtn, joinExamBtn, resultsBtn, logoutBtn);

        // Add fade-in animation
        mainBox.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), mainBox);
        fadeIn.setToValue(1);
        fadeIn.play();

        mainBox.getChildren().addAll(header, welcomeLabel, subjectsCard, bottomBox);
        return mainBox;
    }

    private void applyTheme() {
        if (isDarkMode) {
            rootLayout.setStyle("-fx-background-color: #1a1a1a;");
        } else {
            // Modern gradient background
            LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#f5f7fa")),
                new Stop(1, Color.web("#c3cfe2"))
            );
            rootLayout.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    private void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        stage.setFullScreen(isFullscreen);
    }

    private void showProfileMenu(Button profileBtn) {
        ContextMenu profileMenu = new ContextMenu();

        MenuItem viewProfile = new MenuItem("View Profile");
        viewProfile.setOnAction(e -> showProfileDialog());

        MenuItem changePassword = new MenuItem("Change Password");
        changePassword.setOnAction(e -> showChangePasswordDialog());

        CheckMenuItem darkMode = new CheckMenuItem("Dark Mode");
        darkMode.setSelected(isDarkMode);
        darkMode.setOnAction(e -> {
            isDarkMode = darkMode.isSelected();
            applyTheme();
            // Re-create content to apply theme changes
            VBox mainBox = createMainContent();
            rootLayout.setCenter(mainBox);
        });

        MenuItem profilePicture = new MenuItem("Change Profile Picture");
        profilePicture.setOnAction(e -> showProfilePictureDialog());

        MenuItem settings = new MenuItem("Settings");
        settings.setOnAction(e -> showSettingsDialog());

        MenuItem logout = new MenuItem("Logout");
        logout.setOnAction(e -> {
            stage.close();
            new LoginView().show();
        });

        profileMenu.getItems().addAll(viewProfile, changePassword, new SeparatorMenuItem(), darkMode, profilePicture, new SeparatorMenuItem(), settings, new SeparatorMenuItem(), logout);
        profileMenu.show(profileBtn, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    private void showProfileDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Profile Information");
        alert.setHeaderText("Student Profile");
        alert.setContentText("Username: " + studentName + "\nRole: Student\n\nWelcome to the Smart Exam System!\n\nTake exams and track your progress.");
        alert.showAndWait();
    }

    private void showChangePasswordDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Change Student Password");

        ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField currentPassword = new PasswordField();
        currentPassword.setPromptText("Current Password");
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("New Password");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm New Password");

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(currentPassword, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPassword, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPassword, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                String current = currentPassword.getText();
                String newPass = newPassword.getText();
                String confirm = confirmPassword.getText();

                if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Warning", "All fields are required!");
                    return null;
                }

                if (!newPass.equals(confirm)) {
                    showAlert(Alert.AlertType.ERROR, "Error", "New passwords don't match!");
                    return null;
                }

                // Verify current password
                com.exam.model.User user = UserDAO.authenticate(studentName, current);
                if (user == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Current password is incorrect!");
                    return null;
                }

                // Update password in database
                if (UserDAO.updatePassword(studentName, newPass)) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Password changed successfully!");
                    return newPass;
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to change password!");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showProfilePictureDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            profilePicturePath = selectedFile.getAbsolutePath();
            // Save to database
            if (UserDAO.updateProfilePicture(studentName, profilePicturePath)) {
                profileBtn.setGraphic(createProfileGraphic());
                showAlert(Alert.AlertType.INFORMATION, "Profile Picture", "Profile picture updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save profile picture!");
            }
        }
    }

    private void showSettingsDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Settings");
        alert.setHeaderText("Application Settings");
        alert.setContentText("• Fullscreen: " + (isFullscreen ? "Enabled" : "Disabled") + "\n• Theme: " + (isDarkMode ? "Dark" : "Light") + "\n• Profile Picture: " + (profilePicturePath != null ? "Set" : "Not set") + "\n• Language: English\n\nMore settings coming soon!");
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Button createModernSubjectButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(400);
        btn.setPrefHeight(60);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btn.setTextFill(Color.WHITE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(0, 20, 0, 20));
        
        // Modern card styling
        
        String baseStyle = 
            "-fx-background-color: #3498db; -fx-background-radius: 15; -fx-border-radius: 15; " +
            "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);";
        btn.setStyle(baseStyle);
        
        // Enhanced hover animations
        btn.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
            scale.setToX(1.02);
            scale.setToY(1.05);
            scale.play();
            
            btn.setStyle(
                "-fx-background-color: #2980b9; -fx-background-radius: 15; -fx-border-radius: 15; " +
                "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 8);"
            );
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
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), btn);
            scale.setToX(0.98);
            scale.setToY(0.98);
            scale.play();
        });
        
        btn.setOnMouseReleased(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), btn);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
        
        return btn;
    }
    
    private Button createModernActionButton(String text, String primaryColor, String hoverColor) {
        Button btn = new Button(text);
        btn.setPrefWidth(160);
        btn.setPrefHeight(45);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
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
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), btn);
            scale.setToX(1.08);
            scale.setToY(1.08);
            scale.play();
            
            String hoverStyle = String.format(
                "-fx-background-color: %s; -fx-background-radius: 15; -fx-border-radius: 15; " +
                "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 8);",
                hoverColor
            );
            btn.setStyle(hoverStyle);
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
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), btn);
            scale.setToX(0.95);
            scale.setToY(0.95);
            scale.play();
        });
        
        btn.setOnMouseReleased(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), btn);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
        
        return btn;
    }
    
    private VBox createModernCard() {
        VBox card = new VBox();
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 20; -fx-border-radius: 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 10);"
        );
        return card;
    }
    
    private Button createModernTopBarButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", 12));
        btn.setTextFill(Color.WHITE);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-background-radius: 10;");
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-cursor: hand; -fx-background-radius: 10;");
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), btn);
            scale.setToX(1.05);
            scale.play();
        });
        
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-background-radius: 10;");
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), btn);
            scale.setToX(1.0);
            scale.play();
        });
        
        return btn;
    }

    private void startExam(int subjectId, String subjectName) {
        stage.close();
        new ExamView(studentName, subjectId, subjectName).show();
    }
}
