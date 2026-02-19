package com.exam.util;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.AccessibleRole;

/**
 * Utility class for accessibility features
 * Provides enhanced accessibility support across the application
 */
public class AccessibilityUtils {
    
    // Font size scaling for accessibility
    private static final double FONT_SCALE_SMALL = 0.875;
    private static final double FONT_SCALE_NORMAL = 1.0;
    private static final double FONT_SCALE_LARGE = 1.25;
    private static final double FONT_SCALE_EXTRA_LARGE = 1.5;
    
    // High contrast color schemes
    public static final String HIGH_CONTRAST_BACKGROUND = "#000000";
    public static final String HIGH_CONTRAST_FOREGROUND = "#FFFFFF";
    public static final String HIGH_CONTRAST_ACCENT = "#FFFF00";
    
    /**
     * Sets accessible text for a node
     */
    public static void setAccessibleText(Node node, String text) {
        node.setAccessibleText(text);
        node.setAccessibleHelp(text);
    }
    
    /**
     * Sets accessible role for a node
     */
    public static void setAccessibleRole(Node node, AccessibleRole role) {
        node.setAccessibleRole(role);
    }
    
    /**
     * Creates an accessible label with proper font sizing
     */
    public static Label createAccessibleLabel(String text, double fontSize, String accessibleText) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.NORMAL, fontSize));
        label.setAccessibleText(accessibleText != null ? accessibleText : text);
        label.setAccessibleRole(AccessibleRole.TEXT);
        return label;
    }
    
    /**
     * Creates an accessible button with keyboard navigation support
     */
    public static Button createAccessibleButton(String text, String accessibleText) {
        Button button = new Button(text);
        button.setAccessibleText(accessibleText != null ? accessibleText : text);
        button.setAccessibleRole(AccessibleRole.BUTTON);
        button.setAccessibleHelp("Press Enter or Space to activate");
        
        // Ensure keyboard navigation
        button.setFocusTraversable(true);
        
        return button;
    }
    
    /**
     * Creates an accessible text field with proper labeling
     */
    public static TextField createAccessibleTextField(String prompt, String accessibleText) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setAccessibleText(accessibleText != null ? accessibleText : prompt);
        field.setAccessibleRole(AccessibleRole.TEXT_FIELD);
        field.setAccessibleHelp("Type your text here");
        
        // Ensure keyboard navigation
        field.setFocusTraversable(true);
        
        return field;
    }
    
    /**
     * Creates an accessible password field with proper labeling
     */
    public static PasswordField createAccessiblePasswordField(String prompt, String accessibleText) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setAccessibleText(accessibleText != null ? accessibleText : prompt);
        field.setAccessibleRole(AccessibleRole.PASSWORD_FIELD);
        field.setAccessibleHelp("Type your password. Characters will be hidden");
        
        // Ensure keyboard navigation
        field.setFocusTraversable(true);
        
        return field;
    }
    
    /**
     * Creates an accessible radio button with proper grouping
     */
    public static RadioButton createAccessibleRadioButton(String text, String accessibleText) {
        RadioButton radio = new RadioButton(text);
        radio.setAccessibleText(accessibleText != null ? accessibleText : text);
        radio.setAccessibleRole(AccessibleRole.RADIO_BUTTON);
        radio.setAccessibleHelp("Press Space to toggle selection");
        
        // Ensure keyboard navigation
        radio.setFocusTraversable(true);
        
        return radio;
    }
    
    /**
     * Creates an accessible combo box with proper labeling
     */
    public static <T> ComboBox<T> createAccessibleComboBox(String accessibleText) {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.setAccessibleText(accessibleText);
        comboBox.setAccessibleRole(AccessibleRole.COMBO_BOX);
        comboBox.setAccessibleHelp("Press Alt+Down Arrow to open dropdown");
        
        // Ensure keyboard navigation
        comboBox.setFocusTraversable(true);
        
        return comboBox;
    }
    
    /**
     * Applies high contrast theme to a control
     */
    public static void applyHighContrast(Control control) {
        control.setStyle(
            "-fx-background-color: " + HIGH_CONTRAST_BACKGROUND + "; " +
            "-fx-text-fill: " + HIGH_CONTRAST_FOREGROUND + "; " +
            "-fx-border-color: " + HIGH_CONTRAST_FOREGROUND + "; " +
            "-fx-border-width: 2;"
        );
    }
    
    /**
     * Applies focus styling for better visibility
     */
    public static void applyFocusStyling(Control control) {
        control.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                control.setStyle(
                    control.getStyle() + " " +
                    "-fx-border-color: #FFFF00; " +
                    "-fx-border-width: 3; " +
                    "-fx-effect: dropshadow(gaussian, #FFFF00, 10, 0, 0, 0);"
                );
            } else {
                // Remove focus styling but keep base styling
                String baseStyle = control.getStyle().replaceAll("-fx-border-color: #FFFF00;.*", "");
                control.setStyle(baseStyle);
            }
        });
    }
    
    /**
     * Sets up keyboard navigation for a button
     */
    public static void setupKeyboardNavigation(Button button, Runnable action) {
        button.setOnAction(e -> action.run());
        
        // Handle Enter key press
        button.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                action.run();
            }
        });
        
        // Handle Space key press
        button.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("SPACE")) {
                action.run();
            }
        });
    }
    
    /**
     * Creates an accessible alert with proper screen reader support
     */
    public static Alert createAccessibleAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Make alert accessible
        alert.getDialogPane().setAccessibleText(title + ": " + message);
        alert.getDialogPane().setAccessibleRole(AccessibleRole.DIALOG);
        
        return alert;
    }
    
    /**
     * Adds screen reader announcements for dynamic content
     */
    public static void announceToScreenReader(String message) {
        // Create a temporary label for screen reader announcement
        Label announcement = new Label(message);
        announcement.setAccessibleText(message);
        announcement.setVisible(false);
        
        // Force screen reader to read the announcement
        announcement.requestFocus();
        
        // Remove after announcement
        javafx.application.Platform.runLater(() -> {
            announcement.setVisible(false);
        });
    }
    
    /**
     * Applies font scaling for better readability
     */
    public static void applyFontScaling(Node node, double scale) {
        if (node instanceof Labeled) {
            Labeled labeled = (Labeled) node;
            Font currentFont = labeled.getFont();
            labeled.setFont(Font.font(currentFont.getFamily(), currentFont.getSize() * scale));
        }
    }
    
    /**
     * Sets up ARIA labels for complex components
     */
    public static void setupAriaLabels(Node node, String label, String description) {
        node.setAccessibleText(label);
        node.setAccessibleHelp(description);
    }
    
    /**
     * Creates an accessible progress bar with proper announcements
     */
    public static ProgressBar createAccessibleProgressBar(String accessibleText) {
        ProgressBar progressBar = new ProgressBar();
        progressBar.setAccessibleText(accessibleText);
        progressBar.setAccessibleRole(AccessibleRole.PROGRESS_INDICATOR);
        progressBar.setAccessibleHelp("Shows current progress");
        
        // Announce progress changes
        progressBar.progressProperty().addListener((obs, oldVal, newVal) -> {
            String announcement = String.format("Progress: %.0f percent complete", newVal.doubleValue() * 100);
            announceToScreenReader(announcement);
        });
        
        return progressBar;
    }
    
    /**
     * Validates accessibility of a component
     */
    public static boolean validateAccessibility(Node node) {
        boolean isAccessible = true;
        
        // Check if accessible text is set
        if (node.getAccessibleText() == null || node.getAccessibleText().isEmpty()) {
            System.err.println("Warning: Node missing accessible text: " + node.getClass().getSimpleName());
            isAccessible = false;
        }
        
        // Check if accessible role is set
        if (node.getAccessibleRole() == null) {
            System.err.println("Warning: Node missing accessible role: " + node.getClass().getSimpleName());
            isAccessible = false;
        }
        
        // Check if node is focus traversable (for interactive elements)
        if (node instanceof Control && !node.isFocusTraversable()) {
            System.err.println("Warning: Interactive element not focus traversable: " + node.getClass().getSimpleName());
            isAccessible = false;
        }
        
        return isAccessible;
    }
    
    /**
     * Applies comprehensive accessibility features to a form
     */
    public static void makeFormAccessible(VBox form) {
        for (Node child : form.getChildren()) {
            if (child instanceof Labeled) {
                Labeled labeled = (Labeled) child;
                if (labeled.getAccessibleText() == null) {
                    labeled.setAccessibleText(labeled.getText());
                }
                labeled.setAccessibleRole(AccessibleRole.TEXT);
                applyFocusStyling(labeled);
            } else if (child instanceof Control) {
                Control control = (Control) child;
                if (control.getAccessibleText() == null) {
                    control.setAccessibleText(control.getClass().getSimpleName());
                }
                // Set appropriate role based on control type
                if (control instanceof Button) {
                    control.setAccessibleRole(AccessibleRole.BUTTON);
                } else if (control instanceof TextField) {
                    control.setAccessibleRole(AccessibleRole.TEXT_FIELD);
                } else if (control instanceof PasswordField) {
                    control.setAccessibleRole(AccessibleRole.PASSWORD_FIELD);
                } else if (control instanceof ComboBox) {
                    control.setAccessibleRole(AccessibleRole.COMBO_BOX);
                } else if (control instanceof RadioButton) {
                    control.setAccessibleRole(AccessibleRole.RADIO_BUTTON);
                }
                applyFocusStyling(control);
            }
        }
    }
}
