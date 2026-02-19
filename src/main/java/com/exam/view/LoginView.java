package com.exam.view;

import com.exam.dao.UserDAO;
import com.exam.model.User;
import com.exam.service.GoogleOAuthService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;

public class LoginView {
    private Stage stage;
    private String userRole;

    public LoginView() {
        this.stage = new Stage();
        this.stage.setTitle("Assess Wise - Examination and Evaluation System");
        this.stage.setResizable(false);
    }

    public void show() {
        showRoleSelection();
        stage.show();
    }

    private void showRoleSelection() {
        VBox mainBox = new VBox(30);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(40));
        
        // Modern gradient background
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#3498db")),
            new Stop(1, Color.web("#2980b9"))
        );
        mainBox.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        // Header with modern styling - split into two lines
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        
        // Subtle logo icon
        Label logoIcon = new Label("◎");
        logoIcon.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 48));
        logoIcon.setTextFill(Color.web("#ffffff", 0.7));
        logoIcon.setEffect(new DropShadow(5, Color.web("#000000", 0.2)));
        
        Label headerMain = new Label("Assess Wise");
        headerMain.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        headerMain.setTextFill(Color.WHITE);
        headerMain.setEffect(new DropShadow(10, Color.web("#000000", 0.3)));
        
        Label headerSub = new Label("Examination and Evaluation System");
        headerSub.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 24));
        headerSub.setTextFill(Color.web("#ffffff", 0.9));
        headerSub.setEffect(new DropShadow(5, Color.web("#000000", 0.2)));
        
        headerBox.getChildren().addAll(logoIcon, headerMain, headerSub);
        
        // Subtitle
        Label subtitle = new Label("Choose your role to continue");
        subtitle.setFont(Font.font("Segoe UI", 14));
        subtitle.setTextFill(Color.web("#ffffff", 0.8));

        // Role selection buttons with modern card design
        HBox roleBox = new HBox(30);
        roleBox.setAlignment(Pos.CENTER);

        Button adminBtn = createModernButton("Admin Login", "#3498db", "#2980b9");
        adminBtn.setOnAction(e -> {
            userRole = "ADMIN";
            animateTransition(() -> showLoginDialog());
        });

        Button studentBtn = createModernButton("Student Login", "#3498db", "#2980b9");
        studentBtn.setOnAction(e -> {
            userRole = "STUDENT";
            animateTransition(() -> showLoginDialog());
        });

        Button signUpBtn = createModernButton("Sign Up", "#3498db", "#2980b9");
        signUpBtn.setOnAction(e -> animateTransition(() -> showSignUpDialog()));

        roleBox.getChildren().addAll(adminBtn, studentBtn);

        // Add fade-in animation
        mainBox.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), mainBox);
        fadeIn.setToValue(1);
        fadeIn.play();

        mainBox.getChildren().addAll(headerBox, subtitle, roleBox, signUpBtn);

        Scene scene = new Scene(mainBox, 600, 400);
        stage.setScene(scene);
    }

    private void showLoginDialog() {
        // Create root stack pane for layering
        StackPane rootPane = new StackPane();
        
        if ("ADMIN".equals(userRole)) {
            // Professional gradient background for admin
            LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#2c3e50")),
                new Stop(1, Color.web("#34495e"))
            );
            rootPane.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            // Background image layer with blur for students
            Image backgroundImage = new Image(getClass().getResourceAsStream("/student-img.png"));
            ImageView backgroundView = new ImageView(backgroundImage);
            backgroundView.setPreserveRatio(false);
            backgroundView.fitWidthProperty().bind(rootPane.widthProperty());
            backgroundView.fitHeightProperty().bind(rootPane.heightProperty());
            backgroundView.setSmooth(true);
            
            // Add slight blur effect to background only
            GaussianBlur blur = new GaussianBlur(3.0);
            backgroundView.setEffect(blur);
            
            rootPane.getChildren().add(backgroundView);
        }
        
        // Main content VBox
        VBox mainBox = new VBox(25);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(40));
        if (!"ADMIN".equals(userRole)) {
            mainBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1);"); // Semi-transparent overlay only for students
        }

        // Header with back button
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Button backBtn = createBackButton();
        backBtn.setOnAction(e -> animateTransition(() -> showRoleSelection()));
        
        VBox headerContent = new VBox(10);
        headerContent.setAlignment(Pos.CENTER);
        
        Label header = new Label(userRole + " Login");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        header.setTextFill(Color.WHITE);
        header.setEffect(new DropShadow(10, Color.web("#000000", 0.3)));

        Label roleLabel = new Label("Role: " + userRole);
        roleLabel.setFont(Font.font("Segoe UI", 14));
        roleLabel.setTextFill(Color.web("#ffffff", 0.8));
        
        headerContent.getChildren().addAll(header, roleLabel);
        HBox.setHgrow(headerContent, Priority.ALWAYS);
        headerBox.getChildren().addAll(backBtn, headerContent);

        // Modern card for form
        VBox formCard = createModernCard();
        formCard.setAlignment(Pos.CENTER);
        formCard.setPadding(new Insets(30));
        formCard.setMaxWidth(350);

        // Form fields with modern styling
        VBox formBox = new VBox(20);
        formBox.setAlignment(Pos.CENTER);

        TextField userField = createModernTextField("Username");
        PasswordField passField = createModernPasswordField("Password");

        // Buttons
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button loginBtn = createModernButton("Login", "#3498db", "#2980b9");
        loginBtn.setOnAction(e -> login(userField, passField));
        loginBtn.setPrefWidth(250);

        Button googleBtn = createModernButton("Login with Google", "#db4437", "#c23321");
        googleBtn.setPrefWidth(250);
        googleBtn.setOnAction(e -> loginWithGoogle());

        buttonBox.getChildren().addAll(loginBtn, googleBtn);

        formBox.getChildren().addAll(userField, passField, buttonBox);
        formCard.getChildren().add(formBox);

        mainBox.getChildren().addAll(headerBox, formCard);
        
        // Add content to root pane (background already added conditionally above)
        if ("ADMIN".equals(userRole)) {
            rootPane.getChildren().add(mainBox);
        } else {
            rootPane.getChildren().add(mainBox);
        }

        Scene scene = new Scene(rootPane, 500, 500);
        stage.setScene(scene);

        // Press Enter to login
        passField.setOnAction(e -> login(userField, passField));
    }

    private Button createModernButton(String text, String primaryColor, String hoverColor) {
        Button btn = new Button(text);
        btn.setPrefWidth(180);
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
    
    private Button createBackButton() {
        Button btn = new Button("← Back");
        btn.setFont(Font.font("Segoe UI", 12));
        btn.setTextFill(Color.WHITE);
        btn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        
        btn.setOnMouseEntered(e -> {
            btn.setTextFill(Color.web("#ffffff", 0.7));
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), btn);
            scale.setToX(1.1);
            scale.play();
        });
        
        btn.setOnMouseExited(e -> {
            btn.setTextFill(Color.WHITE);
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), btn);
            scale.setToX(1.0);
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
    
    private TextField createModernTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(45);
        field.setFont(Font.font("Segoe UI", 14));
        field.setStyle(
            "-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-radius: 10; " +
            "-fx-border-color: #e9ecef; -fx-border-width: 2; -fx-padding: 10 15;"
        );
        
        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                field.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; " +
                    "-fx-border-color: #3498db; -fx-border-width: 2; -fx-padding: 10 15;"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-radius: 10; " +
                    "-fx-border-color: #e9ecef; -fx-border-width: 2; -fx-padding: 10 15;"
                );
            }
        });
        
        return field;
    }
    
    private PasswordField createModernPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefHeight(45);
        field.setFont(Font.font("Segoe UI", 14));
        field.setStyle(
            "-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-radius: 10; " +
            "-fx-border-color: #e9ecef; -fx-border-width: 2; -fx-padding: 10 15;"
        );
        
        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                field.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; " +
                    "-fx-border-color: #3498db; -fx-border-width: 2; -fx-padding: 10 15;"
                );
            } else {
                field.setStyle(
                    "-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-radius: 10; " +
                    "-fx-border-color: #e9ecef; -fx-border-width: 2; -fx-padding: 10 15;"
                );
            }
        });
        
        return field;
    }
    
    private void animateTransition(Runnable action) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            action.run();
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void login(TextField userField, PasswordField passField) {
        String username = userField.getText().trim();
        String password = passField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please enter both username and password");
            return;
        }

        User user = UserDAO.authenticate(username, password);

        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Credentials");
            return;
        }

        // Check if user role matches selected role
        if (!user.getRole().equals(userRole)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid credentials for " + userRole);
            return;
        }

        // Successful login
        stage.close();
        if (user.getRole().equals("ADMIN")) {
            new AdminView(user.getUsername()).show();
        } else if (user.getRole().equals("STUDENT")) {
            new StudentView(user.getUsername()).show();
        }
    }

    private void loginWithGoogle() {
        // Show loading dialog
        Alert loadingAlert = new Alert(Alert.AlertType.INFORMATION);
        loadingAlert.setTitle("Google OAuth");
        loadingAlert.setHeaderText("Authenticating with Google...");
        loadingAlert.setContentText("Please wait while we authenticate you with Google.");
        loadingAlert.show();

        // Run OAuth in a separate thread to avoid blocking UI
        new Thread(() -> {
            try {
                String email = GoogleOAuthService.getUserEmail();
                
                Platform.runLater(() -> {
                    loadingAlert.close();
                    
                    if (email != null && !email.isEmpty()) {
                        // Validate email domain - only allow @apsit.edu.in emails
                        if (!GoogleOAuthService.isValidEmailDomain(email, "apsit.edu.in")) {
                            showAlert(Alert.AlertType.ERROR, "Access Denied", 
                                "Only APSIT college email addresses (@apsit.edu.in) are allowed.\n" +
                                "Your email: " + email + "\n\nPlease use your college email or use traditional login.");
                            return;
                        }
                        
                        // Check if user exists in database with this email as username
                        User user = UserDAO.getUserByUsername(email);
                        
                        if (user == null) {
                            // User doesn't exist - auto-create as STUDENT for valid college emails
                            if (userRole.equals("STUDENT")) {
                                boolean created = UserDAO.createUser(email, "STUDENT");
                                if (created) {
                                    user = new User(email, "STUDENT");
                                    showAlert(Alert.AlertType.INFORMATION, "Account Created", 
                                        "Your account has been automatically created!\nEmail: " + email + "\nRole: STUDENT");
                                } else {
                                    showAlert(Alert.AlertType.ERROR, "Error", 
                                        "Failed to create account. Please contact administrator.");
                                    return;
                                }
                            } else {
                                // For ADMIN role, don't auto-create
                                showAlert(Alert.AlertType.ERROR, "Error", 
                                    "No account found with email: " + email + "\nPlease contact administrator to create an admin account.");
                                return;
                            }
                        }
                        
                        if (!user.getRole().equals(userRole)) {
                            showAlert(Alert.AlertType.ERROR, "Error", 
                                "This Google account is registered as " + user.getRole() + ", not " + userRole);
                        } else {
                            // Successful login
                            stage.close();
                            if (user.getRole().equals("ADMIN")) {
                                new AdminView(user.getUsername()).show();
                            } else if (user.getRole().equals("STUDENT")) {
                                new StudentView(user.getUsername()).show();
                            }
                        }
                    } else {
                        // Check if credentials.json is missing
                        String errorMsg = "Google authentication failed.\n\n";
                        errorMsg += "Possible reasons:\n";
                        errorMsg += "1. credentials.json file is missing from src/main/resources/\n";
                        errorMsg += "2. Invalid OAuth credentials\n";
                        errorMsg += "3. Network connection issues\n\n";
                        errorMsg += "Please check GOOGLE_OAUTH_SETUP.md for setup instructions.\n";
                        errorMsg += "You can use traditional login instead.";
                        showAlert(Alert.AlertType.ERROR, "Google OAuth Error", errorMsg);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingAlert.close();
                    String errorMsg = "Google authentication error: " + e.getMessage() + "\n\n";
                    if (e.getMessage() != null && e.getMessage().contains("credentials")) {
                        errorMsg += "Please ensure credentials.json exists in src/main/resources/\n";
                        errorMsg += "See GOOGLE_OAUTH_SETUP.md for instructions.";
                    }
                    showAlert(Alert.AlertType.ERROR, "Error", errorMsg);
                });
            }
        }).start();
    }

    private void showSignUpDialog() {
        Stage signUpStage = new Stage();
        signUpStage.setTitle("Sign Up");
        signUpStage.setResizable(false);

        VBox mainBox = new VBox(20);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(30));
        mainBox.setStyle("-fx-background-color: #f5f5f5;");

        Label header = new Label("Create New Account");
        header.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        header.setTextFill(Color.web("#2c3e50"));

        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(300);

        Label userLabel = new Label("Username:");
        userLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        TextField userField = new TextField();
        userField.setPrefWidth(250);
        userField.setStyle("-fx-font-size: 14px;");

        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        PasswordField passField = new PasswordField();
        passField.setPrefWidth(250);
        passField.setStyle("-fx-font-size: 14px;");

        Label confirmPassLabel = new Label("Confirm Password:");
        confirmPassLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        PasswordField confirmPassField = new PasswordField();
        confirmPassField.setPrefWidth(250);
        confirmPassField.setStyle("-fx-font-size: 14px;");

        Label roleLabel = new Label("Role:");
        roleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("STUDENT", "ADMIN");
        roleBox.setValue("STUDENT");
        roleBox.setPrefWidth(250);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button cancelBtn = createModernButton("Cancel", "#95a5a6", "#7f8c8d");
        cancelBtn.setOnAction(e -> signUpStage.close());

        Button signUpBtn = createModernButton("Sign Up", "#27ae60", "#229954");
        signUpBtn.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText();
            String confirmPassword = confirmPassField.getText();
            String role = roleBox.getValue();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Please fill all fields!");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match!");
                return;
            }

            if (password.length() < 6) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Password must be at least 6 characters long!");
                return;
            }

            // Check if username already exists
            if (UserDAO.getUserByUsername(username) != null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Username already exists! Please choose a different username.");
                return;
            }

            // Create user
            boolean created = UserDAO.createUser(username, role);
            if (created) {
                // Update password
                UserDAO.updatePassword(username, password);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully! You can now login.");
                signUpStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create account. Please try again.");
            }
        });

        buttonBox.getChildren().addAll(cancelBtn, signUpBtn);

        formBox.getChildren().addAll(
            userLabel, userField,
            passLabel, passField,
            confirmPassLabel, confirmPassField,
            roleLabel, roleBox,
            buttonBox
        );

        mainBox.getChildren().addAll(header, formBox);

        Scene scene = new Scene(mainBox, 400, 450);
        signUpStage.setScene(scene);
        signUpStage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
