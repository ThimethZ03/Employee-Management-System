package com.yourcompany.ems.controllers;

import com.yourcompany.ems.database.DatabaseConnection;
import com.yourcompany.ems.database.UserDAO;
import com.yourcompany.ems.models.User;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class AddUserFormController {

    @FXML private TextField idField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label messageLabel;

    // Validation labels
    @FXML private Label idValidationLabel;
    @FXML private Label usernameValidationLabel;
    @FXML private Label passwordValidationLabel;
    @FXML private Label passwordStrengthLabel;
    @FXML private Label roleValidationLabel;

    private UserDAO userDAO;
    private AdminPanelController adminPanelController;
    private Timeline validationTimeline;

    // Email validation pattern
    private final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @FXML
    private void initialize() {
        setupDatabase();
        setupRoleComboBox();
        setupValidation();
        setupAnimations();
    }

    private void setupDatabase() {
        try {
            userDAO = new UserDAO(DatabaseConnection.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Database connection failed. Please try again later.", "error");
            disableForm();
        }
    }

    private void setupRoleComboBox() {
        roleComboBox.getItems().addAll(
                "Admin", "Manager", "Employee", "HR", "IT Support", "Finance"
        );
        roleComboBox.setPromptText("Select user role");
    }

    private void setupValidation() {
        // Real-time validation
        idField.textProperty().addListener((obs, oldVal, newVal) -> validateId(newVal));
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> validateUsername(newVal));
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> validatePassword(newVal));
        roleComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateRole(newVal));
    }

    private void setupAnimations() {
        // Add pulse animation to save button
        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(saveBtn.scaleXProperty(), 1.0)),
                new KeyFrame(Duration.millis(1000), new KeyValue(saveBtn.scaleXProperty(), 1.02)),
                new KeyFrame(Duration.millis(2000), new KeyValue(saveBtn.scaleXProperty(), 1.0))
        );
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.play();

        // Add hover effects
        addButtonHoverEffect(saveBtn);
        addButtonHoverEffect(cancelBtn);
    }

    private void addButtonHoverEffect(Button button) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), button);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        button.setOnMouseEntered(e -> scaleUp.play());
        button.setOnMouseExited(e -> scaleDown.play());
    }

    private void validateId(String value) {
        if (value == null || value.trim().isEmpty()) {
            showValidationMessage(idValidationLabel, "ID is required", false);
            return;
        }

        try {
            int id = Integer.parseInt(value.trim());
            if (id <= 0) {
                showValidationMessage(idValidationLabel, "ID must be positive", false);
            } else {
                showValidationMessage(idValidationLabel, "Valid ID", true);
            }
        } catch (NumberFormatException e) {
            showValidationMessage(idValidationLabel, "ID must be a number", false);
        }
    }

    private void validateUsername(String value) {
        if (value == null || value.trim().isEmpty()) {
            showValidationMessage(usernameValidationLabel, "Username/Email is required", false);
            return;
        }

        String trimmed = value.trim();
        if (trimmed.length() < 3) {
            showValidationMessage(usernameValidationLabel, "Username must be at least 3 characters", false);
        } else if (trimmed.contains("@")) {
            // Email validation
            if (EMAIL_PATTERN.matcher(trimmed).matches()) {
                showValidationMessage(usernameValidationLabel, "Valid email address", true);
            } else {
                showValidationMessage(usernameValidationLabel, "Invalid email format", false);
            }
        } else {
            // Username validation
            if (trimmed.matches("^[a-zA-Z0-9_]+$")) {
                showValidationMessage(usernameValidationLabel, "Valid username", true);
            } else {
                showValidationMessage(usernameValidationLabel, "Username can only contain letters, numbers, and underscores", false);
            }
        }
    }

    private void validatePassword(String value) {
        if (value == null || value.trim().isEmpty()) {
            showValidationMessage(passwordValidationLabel, "Password is required", false);
            passwordStrengthLabel.setText("");
            return;
        }

        String password = value.trim();

        // Basic password validation
        if (password.length() < 8) {
            showValidationMessage(passwordValidationLabel, "Password must be at least 8 characters", false);
            passwordStrengthLabel.setText("");
            return;
        }

        // Check password strength
        int strength = calculatePasswordStrength(password);
        updatePasswordStrengthLabel(strength);

        if (strength < 3) {
            showValidationMessage(passwordValidationLabel, "Password is too weak", false);
        } else {
            showValidationMessage(passwordValidationLabel, "Password meets requirements", true);
        }
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;

        if (password.length() >= 8) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[0-9].*")) strength++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) strength++;

        return strength;
    }

    private void updatePasswordStrengthLabel(int strength) {
        switch (strength) {
            case 0:
            case 1:
                passwordStrengthLabel.setText("Very Weak");
                passwordStrengthLabel.setTextFill(Color.web("#e74c3c"));
                break;
            case 2:
                passwordStrengthLabel.setText("Weak");
                passwordStrengthLabel.setTextFill(Color.web("#f39c12"));
                break;
            case 3:
                passwordStrengthLabel.setText("Fair");
                passwordStrengthLabel.setTextFill(Color.web("#f1c40f"));
                break;
            case 4:
                passwordStrengthLabel.setText("Good");
                passwordStrengthLabel.setTextFill(Color.web("#27ae60"));
                break;
            case 5:
                passwordStrengthLabel.setText("Strong");
                passwordStrengthLabel.setTextFill(Color.web("#2ecc71"));
                break;
        }
    }

    private void validateRole(String value) {
        if (value == null || value.trim().isEmpty()) {
            showValidationMessage(roleValidationLabel, "Role selection is required", false);
        } else {
            showValidationMessage(roleValidationLabel, "Role selected", true);
        }
    }

    private void showValidationMessage(Label label, String message, boolean isValid) {
        if (label != null) {
            label.setText(message);
            label.setTextFill(isValid ? Color.web("#27ae60") : Color.web("#e74c3c"));

            // Add fade animation
            FadeTransition fade = new FadeTransition(Duration.millis(300), label);
            fade.setFromValue(0.5);
            fade.setToValue(1.0);
            fade.play();
        }
    }

    @FXML
    private void handleAddUser() {
        if (!validateForm()) {
            return;
        }

        // Show loading indicator
        showLoadingIndicator(true);
        disableForm();

        // Create user object
        User user = new User();
        user.setId(Integer.parseInt(idField.getText().trim()));
        user.setUsername(usernameField.getText().trim());
        user.setPassword(passwordField.getText().trim());
        user.setRole(roleComboBox.getValue());

        // Save user in background thread
        Task<Boolean> saveTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    // Simulate some processing time
                    Thread.sleep(1000);
                    return userDAO.addUser(user);
                } catch (Exception e) {
                    throw e;
                }
            }
        };

        saveTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                showLoadingIndicator(false);
                enableForm();

                if (saveTask.getValue()) {
                    showMessage("User added successfully!", "success");
                    clearForm();

                    // Auto-hide success message after 3 seconds
                    Timeline hideMessage = new Timeline(
                            new KeyFrame(Duration.seconds(3), ev -> hideMessage())
                    );
                    hideMessage.play();
                } else {
                    showMessage("Failed to add user. Please try again.", "error");
                }
            });
        });

        saveTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                showLoadingIndicator(false);
                enableForm();

                Throwable exception = saveTask.getException();
                if (exception.getMessage().contains("duplicate")) {
                    showMessage("User ID already exists. Please use a different ID.", "error");
                } else {
                    showMessage("Error: " + exception.getMessage(), "error");
                }
            });
        });

        Thread saveThread = new Thread(saveTask);
        saveThread.setDaemon(true);
        saveThread.start();
    }

    @FXML
    private void handleCancel() {
        if (adminPanelController != null) {
            adminPanelController.showMainPanel();
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validate ID
        if (idField.getText() == null || idField.getText().trim().isEmpty()) {
            showValidationMessage(idValidationLabel, "ID is required", false);
            isValid = false;
        } else {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                if (id <= 0) {
                    showValidationMessage(idValidationLabel, "ID must be positive", false);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                showValidationMessage(idValidationLabel, "ID must be a number", false);
                isValid = false;
            }
        }

        // Validate username
        if (usernameField.getText() == null || usernameField.getText().trim().isEmpty()) {
            showValidationMessage(usernameValidationLabel, "Username/Email is required", false);
            isValid = false;
        } else if (usernameField.getText().trim().length() < 3) {
            showValidationMessage(usernameValidationLabel, "Username must be at least 3 characters", false);
            isValid = false;
        }

        // Validate password
        if (passwordField.getText() == null || passwordField.getText().trim().isEmpty()) {
            showValidationMessage(passwordValidationLabel, "Password is required", false);
            isValid = false;
        } else if (passwordField.getText().trim().length() < 8) {
            showValidationMessage(passwordValidationLabel, "Password must be at least 8 characters", false);
            isValid = false;
        } else if (calculatePasswordStrength(passwordField.getText().trim()) < 3) {
            showValidationMessage(passwordValidationLabel, "Password is too weak", false);
            isValid = false;
        }

        // Validate role
        if (roleComboBox.getValue() == null) {
            showValidationMessage(roleValidationLabel, "Role selection is required", false);
            isValid = false;
        }

        return isValid;
    }

    private void clearForm() {
        idField.clear();
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue(null);

        // Clear validation messages
        idValidationLabel.setText("");
        usernameValidationLabel.setText("");
        passwordValidationLabel.setText("");
        passwordStrengthLabel.setText("");
        roleValidationLabel.setText("");
    }

    private void showLoadingIndicator(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(show);
        }
    }

    private void disableForm() {
        idField.setDisable(true);
        usernameField.setDisable(true);
        passwordField.setDisable(true);
        roleComboBox.setDisable(true);
        saveBtn.setDisable(true);
    }

    private void enableForm() {
        idField.setDisable(false);
        usernameField.setDisable(false);
        passwordField.setDisable(false);
        roleComboBox.setDisable(false);
        saveBtn.setDisable(false);
    }

    private void showMessage(String message, String type) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setVisible(true);

            // Style based on message type
            switch (type) {
                case "success":
                    messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-background-color: #d5f4e6; -fx-padding: 10; -fx-background-radius: 5;");
                    break;
                case "error":
                    messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-background-color: #fadbd8; -fx-padding: 10; -fx-background-radius: 5;");
                    break;
                case "info":
                    messageLabel.setStyle("-fx-text-fill: #3498db; -fx-background-color: #d6eaf8; -fx-padding: 10; -fx-background-radius: 5;");
                    break;
            }

            // Fade in animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), messageLabel);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        }
    }

    private void hideMessage() {
        if (messageLabel != null && messageLabel.isVisible()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), messageLabel);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> messageLabel.setVisible(false));
            fadeOut.play();
        }
    }

    // Setter method for AdminPanelController reference
    public void setAdminPanelController(AdminPanelController adminPanelController) {
        this.adminPanelController = adminPanelController;
    }
}
