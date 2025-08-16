package com.yourcompany.ems.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.io.IOException;
import com.yourcompany.ems.services.AuthService;
import com.yourcompany.ems.models.User;

public class JobsController {

    @FXML private VBox adminCard, financeCard, employeeCard;
    @FXML private Button adminBtn, financeBtn, employeeBtn, backButton;
    @FXML private AnchorPane roleSelectionContainer;
    @FXML private StackPane contentContainer;

    private Parent currentPanelContent;

    public void openAdminPanel() {
        User currentUser = AuthService.getLoggedInUser();

        if (currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole())) {
            loadPanelInContainer("/com/yourcompany/ems/views/admin-panel.fxml", "Admin Panel");
        } else {
            showAccessDeniedAlert("Admin Panel", "admin");
        }
    }

    public void openFinancePanel() {
        User currentUser = AuthService.getLoggedInUser();

        if (currentUser != null && ("admin".equalsIgnoreCase(currentUser.getRole()) ||
                "finance".equalsIgnoreCase(currentUser.getRole()))) {
            loadPanelInContainer("/com/yourcompany/ems/views/finance-panel.fxml", "Finance Panel");
        } else {
            showAccessDeniedAlert("Finance Panel", "finance or admin");
        }
    }

    public void openEmployeePanel() {
        User currentUser = AuthService.getLoggedInUser();

        if (currentUser != null) {
            loadPanelInContainer("/com/yourcompany/ems/views/employee-panel.fxml", "Employee Panel");
        } else {
            showAlert("Authentication Required", "Please log in to access the Employee Panel.");
        }
    }

    private void loadPanelInContainer(String fxmlPath, String panelTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            currentPanelContent = loader.load();

            // Clear existing content and add new panel
            contentContainer.getChildren().clear();
            contentContainer.getChildren().add(currentPanelContent);

            // Show content container and hide role selection
            showContentContainer();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load " + panelTitle + ".");
        }
    }

    private void showContentContainer() {
        // Create fade out animation for role selection
        FadeTransition fadeOutRoles = new FadeTransition(Duration.millis(300), roleSelectionContainer);
        fadeOutRoles.setToValue(0);

        fadeOutRoles.setOnFinished(e -> {
            // Hide role selection and show content container
            roleSelectionContainer.setVisible(false);
            contentContainer.setVisible(true);
            backButton.setVisible(true);

            // Fade in the content container
            FadeTransition fadeInContent = new FadeTransition(Duration.millis(300), contentContainer);
            fadeInContent.setFromValue(0);
            fadeInContent.setToValue(1);

            // Fade in the back button
            FadeTransition fadeInBack = new FadeTransition(Duration.millis(300), backButton);
            fadeInBack.setFromValue(0);
            fadeInBack.setToValue(1);

            // Play both animations simultaneously
            ParallelTransition showContent = new ParallelTransition(fadeInContent, fadeInBack);
            showContent.play();
        });

        fadeOutRoles.play();
    }

    @FXML
    public void showRoleSelection() {
        // Create fade out animation for content container
        FadeTransition fadeOutContent = new FadeTransition(Duration.millis(300), contentContainer);
        fadeOutContent.setToValue(0);

        FadeTransition fadeOutBack = new FadeTransition(Duration.millis(300), backButton);
        fadeOutBack.setToValue(0);

        ParallelTransition hideContent = new ParallelTransition(fadeOutContent, fadeOutBack);

        hideContent.setOnFinished(e -> {
            // Hide content container and show role selection
            contentContainer.setVisible(false);
            backButton.setVisible(false);
            roleSelectionContainer.setVisible(true);

            // Clear the content container
            contentContainer.getChildren().clear();
            currentPanelContent = null;

            // Fade in the role selection
            FadeTransition fadeInRoles = new FadeTransition(Duration.millis(300), roleSelectionContainer);
            fadeInRoles.setFromValue(0);
            fadeInRoles.setToValue(1);
            fadeInRoles.play();
        });

        hideContent.play();
    }

    // Card hover animations
    public void onCardHover(javafx.scene.input.MouseEvent event) {
        VBox card = (VBox) event.getSource();

        // Scale up animation
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), card);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        // Shadow enhancement
        TranslateTransition moveUp = new TranslateTransition(Duration.millis(200), card);
        moveUp.setToY(-5);

        ParallelTransition hover = new ParallelTransition(scaleUp, moveUp);
        hover.play();
    }

    public void onCardExit(javafx.scene.input.MouseEvent event) {
        VBox card = (VBox) event.getSource();

        // Scale back animation
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), card);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        // Move back down
        TranslateTransition moveDown = new TranslateTransition(Duration.millis(200), card);
        moveDown.setToY(0);

        ParallelTransition exit = new ParallelTransition(scaleDown, moveDown);
        exit.play();
    }

    // Button hover animations
    public void onButtonHover(javafx.scene.input.MouseEvent event) {
        Button btn = (Button) event.getSource();

        ScaleTransition pulse = new ScaleTransition(Duration.millis(150), btn);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.play();
    }

    public void onButtonExit(javafx.scene.input.MouseEvent event) {
        Button btn = (Button) event.getSource();

        ScaleTransition reset = new ScaleTransition(Duration.millis(150), btn);
        reset.setToX(1.0);
        reset.setToY(1.0);
        reset.play();
    }

    private void showAccessDeniedAlert(String panelName, String requiredRole) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Access Denied");
        alert.setHeaderText("Insufficient Permissions");
        alert.setContentText("Only " + requiredRole + " users can access the " + panelName + ".");

        // Add custom styling
        alert.getDialogPane().setStyle("-fx-background-color: #f7fafc;");
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #f7fafc;");
        alert.showAndWait();
    }
}

