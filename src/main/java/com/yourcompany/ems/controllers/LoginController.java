package com.yourcompany.ems.controllers;

import com.yourcompany.ems.models.User;
import com.yourcompany.ems.services.AuthService;
import com.yourcompany.ems.utils.AlertUtils;
import com.yourcompany.ems.utils.SceneManager;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private AnchorPane rootPane;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Text welcomeText;
    @FXML private Text usernameLabel;
    @FXML private Text passwordLabel;
    @FXML private AnchorPane loadingPane;

    private final AuthService authService = new AuthService();
    private Timeline shakeAnimation;
    private boolean isLoading = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupInitialAnimations();
        setupFieldAnimations();
        setupButtonAnimations();
        applyDropShadows();
    }

    private void setupInitialAnimations() {
        // Welcome text fade-in animation
        welcomeText.setOpacity(0);
        FadeTransition welcomeFade = new FadeTransition(Duration.millis(1500), welcomeText);
        welcomeFade.setFromValue(0);
        welcomeFade.setToValue(1);
        welcomeFade.play();

        // Slide-in animation for input fields
        usernameField.setTranslateX(-200);
        passwordField.setTranslateX(-200);
        loginButton.setTranslateY(50);
        loginButton.setOpacity(0);

        // Username field slide-in
        TranslateTransition usernameSlide = new TranslateTransition(Duration.millis(800), usernameField);
        usernameSlide.setFromX(-200);
        usernameSlide.setToX(0);
        usernameSlide.setDelay(Duration.millis(300));

        // Password field slide-in
        TranslateTransition passwordSlide = new TranslateTransition(Duration.millis(800), passwordField);
        passwordSlide.setFromX(-200);
        passwordSlide.setToX(0);
        passwordSlide.setDelay(Duration.millis(500));

        // Button fade-in and slide-up
        FadeTransition buttonFade = new FadeTransition(Duration.millis(600), loginButton);
        buttonFade.setFromValue(0);
        buttonFade.setToValue(1);
        buttonFade.setDelay(Duration.millis(800));

        TranslateTransition buttonSlide = new TranslateTransition(Duration.millis(600), loginButton);
        buttonSlide.setFromY(50);
        buttonSlide.setToY(0);
        buttonSlide.setDelay(Duration.millis(800));

        // Play all animations
        ParallelTransition initialAnimation = new ParallelTransition(
                welcomeFade, usernameSlide, passwordSlide, buttonFade, buttonSlide
        );
        initialAnimation.play();
    }

    private void setupFieldAnimations() {
        // Focus animations for username field
        usernameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            animateFieldFocus(usernameField, usernameLabel, newVal);
        });

        // Focus animations for password field
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            animateFieldFocus(passwordField, passwordLabel, newVal);
        });

        // Typing animation
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && oldVal.isEmpty()) {
                createTypingPulse(usernameField);
            }
        });

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty() && oldVal.isEmpty()) {
                createTypingPulse(passwordField);
            }
        });
    }

    private void animateFieldFocus(TextField field, Text label, boolean focused) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), field);
        FadeTransition labelFade = new FadeTransition(Duration.millis(200), label);

        if (focused) {
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            labelFade.setToValue(1.0);
            field.setStyle(field.getStyle() + "-fx-border-color: #4A90E2; -fx-border-width: 2px;");
        } else {
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            labelFade.setToValue(0.8);
            field.setStyle(field.getStyle().replace("-fx-border-color: #4A90E2; -fx-border-width: 2px;", ""));
        }

        scaleTransition.play();
        labelFade.play();
    }

    private void createTypingPulse(TextField field) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(100), field);
        pulse.setToX(1.02);
        pulse.setToY(1.02);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    private void setupButtonAnimations() {
        // Hover animations for login button
        loginButton.setOnMouseEntered(e -> {
            if (!isLoading) {
                ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), loginButton);
                scaleUp.setToX(1.1);
                scaleUp.setToY(1.1);
                scaleUp.play();

                // Add glow effect
                DropShadow glow = new DropShadow();
                glow.setColor(Color.DODGERBLUE);
                glow.setRadius(20);
                loginButton.setEffect(glow);
            }
        });

        loginButton.setOnMouseExited(e -> {
            if (!isLoading) {
                ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), loginButton);
                scaleDown.setToX(1.0);
                scaleDown.setToY(1.0);
                scaleDown.play();

                // Remove glow effect
                loginButton.setEffect(null);
            }
        });

        // Click animation
        loginButton.setOnMousePressed(e -> {
            if (!isLoading) {
                ScaleTransition press = new ScaleTransition(Duration.millis(100), loginButton);
                press.setToX(0.95);
                press.setToY(0.95);
                press.play();
            }
        });

        loginButton.setOnMouseReleased(e -> {
            if (!isLoading) {
                ScaleTransition release = new ScaleTransition(Duration.millis(100), loginButton);
                release.setToX(1.1);
                release.setToY(1.1);
                release.play();
            }
        });
    }

    private void applyDropShadows() {
        DropShadow fieldShadow = new DropShadow();
        fieldShadow.setColor(Color.color(0, 0, 0, 0.3));
        fieldShadow.setRadius(5);
        fieldShadow.setOffsetY(2);

        usernameField.setEffect(fieldShadow);
        passwordField.setEffect(fieldShadow);

        DropShadow buttonShadow = new DropShadow();
        buttonShadow.setColor(Color.color(0, 0, 0, 0.4));
        buttonShadow.setRadius(8);
        buttonShadow.setOffsetY(3);
        loginButton.setEffect(buttonShadow);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showValidationError("Please enter both username and password");
            shakeFields();
            return;
        }

        // Start loading animation
        startLoadingAnimation();

        // Simulate async login (replace with your actual async call)
        Timeline loginDelay = new Timeline(new KeyFrame(Duration.millis(1500), e -> {
            User loggedInUser = authService.login(username, password);
            if (loggedInUser != null) {
                showSuccessAnimation(() -> {
                    Stage stage = (Stage) usernameField.getScene().getWindow();
                    SceneManager.changeScene(stage, "/com/yourcompany/ems/views/newdashboard-main3.fxml", "Dashboard");
                });
            } else {
                stopLoadingAnimation();
                showValidationError("Invalid username or password");
                shakeFields();
            }
        }));
        loginDelay.play();
    }

    private void startLoadingAnimation() {
        if (loadingPane != null) {
            loadingPane.setVisible(true);
        }

        isLoading = true;
        loginButton.setText("Logging in...");
        loginButton.setDisable(true);

        // Create spinning animation
        RotateTransition spin = new RotateTransition(Duration.millis(1000), loginButton);
        spin.setByAngle(360);
        spin.setCycleCount(Timeline.INDEFINITE);
        spin.play();

        // Store the animation to stop it later
        loginButton.setUserData(spin);

        // Blur background slightly
        GaussianBlur blur = new GaussianBlur(2);
        rootPane.setEffect(blur);
    }

    private void stopLoadingAnimation() {
        if (loadingPane != null) {
            loadingPane.setVisible(false);
        }

        isLoading = false;
        loginButton.setText("LOGIN");
        loginButton.setDisable(false);

        // Stop spinning animation
        RotateTransition spin = (RotateTransition) loginButton.getUserData();
        if (spin != null) {
            spin.stop();
        }

        // Reset button rotation
        loginButton.setRotate(0);

        // Remove blur
        rootPane.setEffect(null);
    }

    private void showSuccessAnimation(Runnable onComplete) {
        stopLoadingAnimation();

        // Success color change
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 25; -fx-font-weight: bold; -fx-font-size: 16; -fx-cursor: hand;");
        loginButton.setText("SUCCESS!");

        // Success scale animation
        ScaleTransition successScale = new ScaleTransition(Duration.millis(300), loginButton);
        successScale.setToX(1.2);
        successScale.setToY(1.2);
        successScale.setCycleCount(2);
        successScale.setAutoReverse(true);

        // Fade out entire form
        FadeTransition fadeOut = new FadeTransition(Duration.millis(800), rootPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.millis(500));

        successScale.setOnFinished(e -> fadeOut.play());
        fadeOut.setOnFinished(e -> onComplete.run());

        successScale.play();
    }

    private void shakeFields() {
        // Create shake animation for both fields
        TranslateTransition shakeUsername = createShakeAnimation(usernameField);
        TranslateTransition shakePassword = createShakeAnimation(passwordField);

        ParallelTransition shakeAnimation = new ParallelTransition(shakeUsername, shakePassword);
        shakeAnimation.play();

        // Highlight fields with error color
        String errorStyle = "-fx-border-color: #F44336; -fx-border-width: 2px;";
        usernameField.setStyle(usernameField.getStyle() + errorStyle);
        passwordField.setStyle(passwordField.getStyle() + errorStyle);

        // Remove error styling after animation
        Timeline resetStyle = new Timeline(new KeyFrame(Duration.millis(2000), e -> {
            usernameField.setStyle(usernameField.getStyle().replace(errorStyle, ""));
            passwordField.setStyle(passwordField.getStyle().replace(errorStyle, ""));
        }));
        resetStyle.play();
    }

    private TranslateTransition createShakeAnimation(TextField field) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), field);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        return shake;
    }

    private void showValidationError(String message) {
        AlertUtils.showErrorAlert("Error", message);
    }

    // Method to handle Enter key press - FIXED VERSION
    @FXML
    private void handleEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && !isLoading) {
            handleLogin();
        }
    }
}
