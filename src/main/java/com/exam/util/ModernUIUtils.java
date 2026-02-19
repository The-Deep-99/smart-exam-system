package com.exam.util;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Utility class for modern UI components and styling
 * Provides consistent design patterns across the application
 */
public class ModernUIUtils {
    
    // Color palette
    public static final String PRIMARY_COLOR = "#3498db";
    public static final String SECONDARY_COLOR = "#2980b9";
    public static final String SUCCESS_COLOR = "#27ae60";
    public static final String WARNING_COLOR = "#f39c12";
    public static final String DANGER_COLOR = "#e74c3c";
    public static final String INFO_COLOR = "#547792";
    public static final String LIGHT_COLOR = "#f8f9fa";
    public static final String DARK_COLOR = "#2c3e50";
    
    // Typography
    public static final String PRIMARY_FONT = "Segoe UI";
    public static final double TITLE_SIZE = 32;
    public static final double HEADER_SIZE = 24;
    public static final double SUBHEADER_SIZE = 18;
    public static final double BODY_SIZE = 14;
    public static final double CAPTION_SIZE = 12;
    
    // Spacing
    public static final Insets SMALL_PADDING = new Insets(5);
    public static final Insets MEDIUM_PADDING = new Insets(10);
    public static final Insets LARGE_PADDING = new Insets(20);
    public static final Insets EXTRA_LARGE_PADDING = new Insets(40);
    
    // Corner radius
    public static final double SMALL_RADIUS = 5;
    public static final double MEDIUM_RADIUS = 10;
    public static final double LARGE_RADIUS = 15;
    public static final double EXTRA_LARGE_RADIUS = 20;
    
    /**
     * Creates a modern gradient background
     */
    public static Background createGradientBackground(String startColor, String endColor) {
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
            new Stop(0, Color.web(startColor)),
            new Stop(1, Color.web(endColor))
        );
        return new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY));
    }
    
    /**
     * Creates a card with modern styling
     */
    public static VBox createCard() {
        VBox card = new VBox();
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: " + EXTRA_LARGE_RADIUS + "; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 10);"
        );
        return card;
    }
    
    /**
     * Creates a card with custom padding
     */
    public static VBox createCard(Insets padding) {
        VBox card = createCard();
        card.setPadding(padding);
        return card;
    }
    
    /**
     * Creates a modern button with animations
     */
    public static Button createModernButton(String text, String primaryColor, String hoverColor) {
        return createModernButton(text, primaryColor, hoverColor, 180, 50);
    }
    
    /**
     * Creates a modern button with custom dimensions
     */
    public static Button createModernButton(String text, String primaryColor, String hoverColor, 
                                          double width, double height) {
        Button btn = new Button(text);
        btn.setPrefWidth(width);
        btn.setPrefHeight(height);
        btn.setFont(Font.font(PRIMARY_FONT, FontWeight.BOLD, 14));
        btn.setTextFill(Color.WHITE);
        
        String baseStyle = String.format(
            "-fx-background-color: %s; -fx-background-radius: " + LARGE_RADIUS + "; " +
            "-fx-border-radius: " + LARGE_RADIUS + "; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);",
            primaryColor
        );
        btn.setStyle(baseStyle);
        
        // Hover animation
        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) {
                animateButtonHover(btn, 1.05, hoverColor, 15, 8);
            }
        });
        
        btn.setOnMouseExited(e -> {
            animateButtonExit(btn, baseStyle);
        });
        
        // Press animation
        btn.setOnMousePressed(e -> {
            if (!btn.isDisabled()) {
                animateButtonPress(btn, 0.95);
            }
        });
        
        btn.setOnMouseReleased(e -> {
            animateButtonRelease(btn, 1.0);
        });
        
        return btn;
    }
    
    /**
     * Creates a modern text field with focus effects
     */
    public static TextField createModernTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(45);
        field.setFont(Font.font(PRIMARY_FONT, BODY_SIZE));
        
        String baseStyle = String.format(
            "-fx-background-color: %s; -fx-background-radius: " + MEDIUM_RADIUS + "; " +
            "-fx-border-radius: " + MEDIUM_RADIUS + "; -fx-border-color: #e9ecef; " +
            "-fx-border-width: 2; -fx-padding: 10 15;",
            LIGHT_COLOR
        );
        field.setStyle(baseStyle);
        
        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                field.setStyle(
                    "-fx-background-color: white; -fx-background-radius: " + MEDIUM_RADIUS + "; " +
                    "-fx-border-radius: " + MEDIUM_RADIUS + "; -fx-border-color: " + INFO_COLOR + "; " +
                    "-fx-border-width: 2; -fx-padding: 10 15;"
                );
            } else {
                field.setStyle(baseStyle);
            }
        });
        
        return field;
    }
    
    /**
     * Creates a modern password field with focus effects
     */
    public static PasswordField createModernPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setPrefHeight(45);
        field.setFont(Font.font(PRIMARY_FONT, BODY_SIZE));
        
        String baseStyle = String.format(
            "-fx-background-color: %s; -fx-background-radius: " + MEDIUM_RADIUS + "; " +
            "-fx-border-radius: " + MEDIUM_RADIUS + "; -fx-border-color: #e9ecef; " +
            "-fx-border-width: 2; -fx-padding: 10 15;",
            LIGHT_COLOR
        );
        field.setStyle(baseStyle);
        
        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                field.setStyle(
                    "-fx-background-color: white; -fx-background-radius: " + MEDIUM_RADIUS + "; " +
                    "-fx-border-radius: " + MEDIUM_RADIUS + "; -fx-border-color: " + INFO_COLOR + "; " +
                    "-fx-border-width: 2; -fx-padding: 10 15;"
                );
            } else {
                field.setStyle(baseStyle);
            }
        });
        
        return field;
    }
    
    /**
     * Creates a modern radio button with hover effects
     */
    public static RadioButton createModernRadioButton() {
        RadioButton radio = new RadioButton();
        radio.setFont(Font.font(PRIMARY_FONT, BODY_SIZE));
        radio.setWrapText(true);
        radio.setTextFill(Color.web(DARK_COLOR));
        
        String baseStyle = String.format(
            "-fx-font-size: %f; -fx-padding: 10; -fx-spacing: 10; " +
            "-fx-background-color: %s; -fx-background-radius: " + MEDIUM_RADIUS + "; " +
            "-fx-border-color: #e9ecef; -fx-border-radius: " + MEDIUM_RADIUS + "; " +
            "-fx-border-width: 2;",
            BODY_SIZE, LIGHT_COLOR
        );
        radio.setStyle(baseStyle);
        
        radio.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                radio.setStyle(String.format(
                    "-fx-font-size: %f; -fx-padding: 10; -fx-spacing: 10; " +
                    "-fx-background-color: #e3f2fd; -fx-background-radius: " + MEDIUM_RADIUS + "; " +
                    "-fx-border-color: " + INFO_COLOR + "; -fx-border-radius: " + MEDIUM_RADIUS + "; " +
                    "-fx-border-width: 2;",
                    BODY_SIZE
                ));
            } else {
                radio.setStyle(baseStyle);
            }
        });
        
        radio.hoverProperty().addListener((obs, wasHovered, isHovered) -> {
            if (isHovered && !radio.isSelected()) {
                radio.setStyle(String.format(
                    "-fx-font-size: %f; -fx-padding: 10; -fx-spacing: 10; " +
                    "-fx-background-color: #f1f3f4; -fx-background-radius: " + MEDIUM_RADIUS + "; " +
                    "-fx-border-color: " + INFO_COLOR + "; -fx-border-radius: " + MEDIUM_RADIUS + "; " +
                    "-fx-border-width: 2;",
                    BODY_SIZE
                ));
            } else if (!radio.isSelected()) {
                radio.setStyle(baseStyle);
            }
        });
        
        return radio;
    }
    
    /**
     * Creates a modern label with shadow effect
     */
    public static Label createModernLabel(String text, double size, FontWeight weight, String color) {
        Label label = new Label(text);
        label.setFont(Font.font(PRIMARY_FONT, weight, size));
        label.setTextFill(Color.web(color));
        label.setEffect(new DropShadow(10, Color.web("#000000", 0.2)));
        return label;
    }
    
    /**
     * Creates a responsive grid layout
     */
    public static GridPane createResponsiveGrid(double hgap, double vgap, Pos alignment) {
        GridPane grid = new GridPane();
        grid.setHgap(hgap);
        grid.setVgap(vgap);
        grid.setAlignment(alignment);
        return grid;
    }
    
    /**
     * Adds fade-in animation to a node
     */
    public static void addFadeInAnimation(Node node, Duration duration) {
        node.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(duration, node);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    /**
     * Adds slide-in animation to a node
     */
    public static void addSlideInAnimation(Node node, Duration duration) {
        node.setTranslateY(-50);
        node.setOpacity(0);
        
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(node.translateYProperty(), -50),
                new KeyValue(node.opacityProperty(), 0)
            ),
            new KeyFrame(duration, 
                new KeyValue(node.translateYProperty(), 0),
                new KeyValue(node.opacityProperty(), 1)
            )
        );
        timeline.play();
    }
    
    /**
     * Creates a modern progress bar
     */
    public static ProgressBar createModernProgressBar() {
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(12);
        progressBar.setStyle(
            "-fx-accent: " + INFO_COLOR + "; -fx-control-inner-background: #e9ecef; " +
            "-fx-background-radius: 6; -fx-background-insets: 0;"
        );
        return progressBar;
    }
    
    /**
     * Creates a modern scroll pane
     */
    public static ScrollPane createModernScrollPane(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }
    
    // Private animation helper methods
    private static void animateButtonHover(Button btn, double scale, String color, int shadowRadius, int shadowOffset) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), btn);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.play();
        
        String hoverStyle = String.format(
            "-fx-background-color: %s; -fx-background-radius: " + LARGE_RADIUS + "; " +
            "-fx-border-radius: " + LARGE_RADIUS + "; -fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), %d, 0, 0, %d);",
            color, shadowRadius, shadowOffset
        );
        btn.setStyle(hoverStyle);
    }
    
    private static void animateButtonExit(Button btn, String baseStyle) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), btn);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.play();
        btn.setStyle(baseStyle);
    }
    
    private static void animateButtonPress(Button btn, double scale) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), btn);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.play();
    }
    
    private static void animateButtonRelease(Button btn, double scale) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), btn);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);
        scaleTransition.play();
    }
    
    /**
     * Creates a responsive layout that adapts to window size
     */
    public static VBox createResponsiveLayout(double spacing) {
        VBox layout = new VBox(spacing);
        layout.setAlignment(Pos.CENTER);
        layout.setFillWidth(true);
        
        // Add listener for responsive behavior
        layout.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            if (newWidth.doubleValue() < 600) {
                // Mobile layout
                layout.setSpacing(spacing * 0.7);
                layout.setPadding(new Insets(10));
            } else if (newWidth.doubleValue() < 1200) {
                // Tablet layout
                layout.setSpacing(spacing * 0.85);
                layout.setPadding(new Insets(20));
            } else {
                // Desktop layout
                layout.setSpacing(spacing);
                layout.setPadding(new Insets(40));
            }
        });
        
        return layout;
    }
}
