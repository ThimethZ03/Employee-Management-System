package com.yourcompany.ems.controllers;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController implements Initializable {

    @FXML private VBox sidebar;
    @FXML private ImageView logoImage;
    @FXML private Button Btn_Dashboard;
    @FXML private Button Btn_Messages;
    @FXML private Button Btn_Jobs;
    @FXML private Button Btn_Candidates;
    @FXML private Button Btn_Report;
    @FXML private Button Btn_Payroll;
    @FXML private Button Btn_EM;
    @FXML private Button logoutButton;

    // Icons for animations
    @FXML private ImageView dashboardIcon;
    @FXML private ImageView messagesIcon;
    @FXML private ImageView jobsIcon;
    @FXML private ImageView candidatesIcon;
    @FXML private ImageView reportIcon;
    @FXML private ImageView payrollIcon;
    @FXML private ImageView employeeIcon;
    @FXML private ImageView logoutIcon;

    private MainDashboardController mainDashboardController;
    private Button activeButton = null;
    private Timeline pulseAnimation;

    public void setMainDashboardController(MainDashboardController controller) {
        this.mainDashboardController = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupAnimations();
        setupActiveButtonTracking();
        startLogoAnimation();
    }

    private void setupAnimations() {
        // Add hover animations for all buttons
        setupButtonHoverAnimation(Btn_Dashboard, dashboardIcon);
        setupButtonHoverAnimation(Btn_Messages, messagesIcon);
        setupButtonHoverAnimation(Btn_Jobs, jobsIcon);
        setupButtonHoverAnimation(Btn_Candidates, candidatesIcon);
        setupButtonHoverAnimation(Btn_Report, reportIcon);
        setupButtonHoverAnimation(Btn_Payroll, payrollIcon);
        setupButtonHoverAnimation(Btn_EM, employeeIcon);

        // Special animation for logout button
        setupLogoutHoverAnimation();

        // Logo hover animation
        setupLogoHoverAnimation();
    }

    private void setupButtonHoverAnimation(Button button, ImageView icon) {
        // Scale animation on hover
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), button);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        // Icon rotation animation
        RotateTransition iconRotate = new RotateTransition(Duration.millis(300), icon);
        iconRotate.setByAngle(360);

        // Color transition on hover
        button.setOnMouseEntered(e -> {
            scaleIn.play();
            button.setStyle(button.getStyle() +
                    "-fx-background-color: linear-gradient(to right, #009955, #00CC66); " +
                    "-fx-text-fill: white; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,255,0,0.6), 8, 0, 0, 0);");

            // Add subtle icon animation
            if (icon != null) {
                ScaleTransition iconScale = new ScaleTransition(Duration.millis(200), icon);
                iconScale.setToX(1.1);
                iconScale.setToY(1.1);
                iconScale.play();
            }
        });

        button.setOnMouseExited(e -> {
            if (activeButton != button) {
                scaleOut.play();
                resetButtonStyle(button);

                // Reset icon scale
                if (icon != null) {
                    ScaleTransition iconScale = new ScaleTransition(Duration.millis(200), icon);
                    iconScale.setToX(1.0);
                    iconScale.setToY(1.0);
                    iconScale.play();
                }
            }
        });

        // Click animation
        button.setOnMousePressed(e -> {
            ScaleTransition clickScale = new ScaleTransition(Duration.millis(100), button);
            clickScale.setToX(0.95);
            clickScale.setToY(0.95);
            clickScale.play();

            // Icon bounce effect
            if (icon != null) {
                iconRotate.play();
            }
        });

        button.setOnMouseReleased(e -> {
            ScaleTransition releaseScale = new ScaleTransition(Duration.millis(100), button);
            releaseScale.setToX(1.05);
            releaseScale.setToY(1.05);
            releaseScale.play();
        });
    }

    private void setupLogoutHoverAnimation() {
        // Special pulsing animation for logout button
        ScaleTransition pulse = new ScaleTransition(Duration.millis(300), logoutButton);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(2);

        logoutButton.setOnMouseEntered(e -> {
            pulse.play();
            logoutButton.setStyle(logoutButton.getStyle() +
                    "-fx-background-color: linear-gradient(to bottom, #FF6666, #FF0000); " +
                    "-fx-effect: dropshadow(gaussian, rgba(255,0,0,0.8), 10, 0, 0, 0);");
        });

        logoutButton.setOnMouseExited(e -> {
            logoutButton.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #FF4444, #CC0000); " +
                            "-fx-background-radius: 25; " +
                            "-fx-border-radius: 25; " +
                            "-fx-font-weight: bold; " +
                            "-fx-font-size: 13px; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 5, 0, 2, 2); " +
                            "-fx-transition: all 0.3s ease; " +
                            "-fx-text-fill: white;");
        });
    }

    private void setupLogoHoverAnimation() {
        RotateTransition logoRotate = new RotateTransition(Duration.millis(1000), logoImage);
        logoRotate.setByAngle(360);

        ScaleTransition logoScale = new ScaleTransition(Duration.millis(300), logoImage);
        logoScale.setToX(1.1);
        logoScale.setToY(1.1);

        logoImage.setOnMouseEntered(e -> {
            logoScale.setToX(1.1);
            logoScale.setToY(1.1);
            logoScale.play();
        });

        logoImage.setOnMouseExited(e -> {
            logoScale.setToX(1.0);
            logoScale.setToY(1.0);
            logoScale.play();
        });

        logoImage.setOnMouseClicked(e -> logoRotate.play());
    }

    private void startLogoAnimation() {
        // Entrance animation for the logo
        FadeTransition logoFade = new FadeTransition(Duration.millis(1500), logoImage);
        logoFade.setFromValue(0);
        logoFade.setToValue(1);

        TranslateTransition logoSlide = new TranslateTransition(Duration.millis(1000), logoImage);
        logoSlide.setFromY(-100);
        logoSlide.setToY(0);

        ParallelTransition logoEntrance = new ParallelTransition(logoFade, logoSlide);
        logoEntrance.play();
    }

    private void setupActiveButtonTracking() {
        // Set Dashboard as default active
        setActiveButton(Btn_Dashboard);
    }

    private void setActiveButton(Button button) {
        // Reset previous active button
        if (activeButton != null) {
            resetButtonStyle(activeButton);
        }

        // Set new active button
        activeButton = button;
        button.setStyle(button.getStyle() +
                "-fx-background-color: linear-gradient(to right, #004D2A, #007D49); " +
                "-fx-text-fill: #FFD700; " +
                "-fx-effect: dropshadow(gaussian, rgba(255,215,0,0.8), 8, 0, 0, 0); " +
                "-fx-border-color: #FFD700; " +
                "-fx-border-width: 2px;");
    }

    private void resetButtonStyle(Button button) {
        button.setStyle(
                "-fx-background-color: #007D49; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-radius: 8; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 12px; " +
                        "-fx-cursor: hand; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 3, 0, 1, 1); " +
                        "-fx-transition: all 0.3s ease; " +
                        "-fx-text-fill: white;");
    }

    private void animatePageTransition() {
        // Slide animation for the entire sidebar when changing pages
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), sidebar);
        slideOut.setToX(-20);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), sidebar);
        slideIn.setToX(0);

        SequentialTransition pageTransition = new SequentialTransition(slideOut, slideIn);
        pageTransition.play();
    }

    @FXML
    private void handleDashboard() {
        setActiveButton(Btn_Dashboard);
        animatePageTransition();
        mainDashboardController.setContent("/com/yourcompany/ems/views/newdashboard.fxml");
    }

    @FXML
    private void handleMessages() {
        setActiveButton(Btn_Messages);
        animatePageTransition();
        mainDashboardController.setContent("/com/yourcompany/ems/views/messages.fxml");
    }

    @FXML
    private void handleJobs() {
        setActiveButton(Btn_Jobs);
        animatePageTransition();
        mainDashboardController.setContent("/com/yourcompany/ems/views/jobs.fxml");
    }

    @FXML
    private void handlePayroll() {
        setActiveButton(Btn_Payroll);
        animatePageTransition();
        mainDashboardController.setContent("/com/yourcompany/ems/views/payroll-management.fxml");
    }

    @FXML
    private void handleAddEmployee() {
        setActiveButton(Btn_EM);
        animatePageTransition();
        mainDashboardController.setContent("/com/yourcompany/ems/views/employee-form.fxml");
    }

    @FXML
    private void openEmployeeManagementPage() {
        if (mainDashboardController != null) {
            setActiveButton(Btn_EM);
            animatePageTransition();
            mainDashboardController.setContent("/com/yourcompany/ems/views/employee-management.fxml");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Logout animation
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), sidebar);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        ScaleTransition shrink = new ScaleTransition(Duration.millis(500), sidebar);
        shrink.setToX(0.8);
        shrink.setToY(0.8);

        ParallelTransition logoutAnimation = new ParallelTransition(fadeOut, shrink);

        logoutAnimation.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/yourcompany/ems/views/login.fxml"));
                Parent loginRoot = loader.load();

                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                Scene loginScene = new Scene(loginRoot);
                stage.setScene(loginScene);
                stage.setTitle("Login");
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        logoutAnimation.play();
    }
}




