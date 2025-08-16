package com.yourcompany.ems.controllers;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.io.IOException;

public class AdminPanelController {

    @FXML private AnchorPane contentContainer;
    @FXML private VBox mainContent;
    @FXML private Button backBtn;
    @FXML private Button addUserBtn;
    @FXML private Button viewUsersBtn;
    @FXML private ProgressIndicator loadingIndicator;

    private Node currentView;
    private boolean isAnimating = false;

    @FXML
    private void initialize() {
        // Add hover effects to buttons
        addHoverEffects();

        // Store reference to main content
        currentView = mainContent;
    }

    private void addHoverEffects() {
        // Add hover effects to all buttons
        addHoverEffect(addUserBtn);
        addHoverEffect(viewUsersBtn);
        addHoverEffect(backBtn);
    }

    private void addHoverEffect(Button button) {
        if (button == null) return;

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), button);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        button.setOnMouseEntered(e -> {
            if (!isAnimating) {
                scaleUp.play();
            }
        });

        button.setOnMouseExited(e -> {
            if (!isAnimating) {
                scaleDown.play();
            }
        });
    }

    @FXML
    private void openAddUserForm(ActionEvent event) {
        if (isAnimating) return;
        loadViewWithAnimation("/com/yourcompany/ems/views/add-user-form.fxml", this::setupAddUserForm);
    }

    @FXML
    private void openViewUsersPanel(ActionEvent event) {
        if (isAnimating) return;
        // Placeholder for view users functionality
        showMessage("View Users functionality coming soon!", "info");
    }

    @FXML
    private void showMainPanel(ActionEvent event) {
        showMainPanel();
    }

    public void showMainPanel() {
        if (isAnimating) return;

        if (currentView != mainContent) {
            animateViewTransition(currentView, mainContent, () -> {
                currentView = mainContent;
                updateNavigationButtons(false);
            });
        }
    }

    private void loadViewWithAnimation(String fxmlPath, Runnable setupCallback) {
        if (isAnimating) return;

        // Show loading indicator
        showLoadingIndicator(true);

        // Simulate loading delay for better UX
        Timeline loadingDelay = new Timeline(new KeyFrame(Duration.millis(300), e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Node newView = loader.load();

                // Setup controller reference if needed
                if (setupCallback != null) {
                    setupCallback.run();
                }

                // Get the controller and pass this reference to it
                Object controller = loader.getController();
                if (controller instanceof AddUserFormController) {
                    ((AddUserFormController) controller).setAdminPanelController(this);
                }

                showLoadingIndicator(false);
                animateViewTransition(currentView, newView, () -> {
                    currentView = newView;
                    updateNavigationButtons(true);
                });

            } catch (IOException ex) {
                ex.printStackTrace();
                showLoadingIndicator(false);
                showMessage("Failed to load view: " + ex.getMessage(), "error");
            }
        }));
        loadingDelay.play();
    }

    private void setupAddUserForm() {
        // Additional setup for add user form if needed
    }

    private void animateViewTransition(Node oldView, Node newView, Runnable onComplete) {
        if (isAnimating) return;
        isAnimating = true;

        // Prepare new view
        contentContainer.getChildren().add(newView);
        AnchorPane.setTopAnchor(newView, 0.0);
        AnchorPane.setBottomAnchor(newView, 0.0);
        AnchorPane.setLeftAnchor(newView, 0.0);
        AnchorPane.setRightAnchor(newView, 0.0);

        // Set initial position for slide animation
        newView.setTranslateX(contentContainer.getWidth());
        newView.setOpacity(0.0);

        // Create animations
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), oldView);
        slideOut.setToX(-contentContainer.getWidth());

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), newView);
        slideIn.setToX(0);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), oldView);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), newView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // Combine animations
        ParallelTransition slideAnimation = new ParallelTransition(slideOut, slideIn);
        ParallelTransition fadeAnimation = new ParallelTransition(fadeOut, fadeIn);

        SequentialTransition transition = new SequentialTransition();
        transition.getChildren().addAll(
                new ParallelTransition(slideAnimation, fadeAnimation)
        );

        transition.setOnFinished(e -> {
            // Clean up old view
            contentContainer.getChildren().remove(oldView);
            oldView.setTranslateX(0);
            oldView.setOpacity(1.0);

            newView.setTranslateX(0);
            newView.setOpacity(1.0);

            isAnimating = false;
            if (onComplete != null) {
                onComplete.run();
            }
        });

        transition.play();
    }

    private void updateNavigationButtons(boolean showBack) {
        // Animate button visibility changes
        Timeline buttonAnimation = new Timeline();

        if (showBack) {
            backBtn.setVisible(true);
            backBtn.setOpacity(0.0);

            KeyFrame fadeInBack = new KeyFrame(Duration.millis(300),
                    new KeyValue(backBtn.opacityProperty(), 1.0));
            KeyFrame fadeOutAdd = new KeyFrame(Duration.millis(300),
                    new KeyValue(addUserBtn.opacityProperty(), 0.0));
            KeyFrame fadeOutView = new KeyFrame(Duration.millis(300),
                    new KeyValue(viewUsersBtn.opacityProperty(), 0.0));

            buttonAnimation.getKeyFrames().addAll(fadeInBack, fadeOutAdd, fadeOutView);
            buttonAnimation.setOnFinished(e -> {
                addUserBtn.setVisible(false);
                viewUsersBtn.setVisible(false);
                addUserBtn.setOpacity(1.0);
                viewUsersBtn.setOpacity(1.0);
            });
        } else {
            addUserBtn.setVisible(true);
            viewUsersBtn.setVisible(true);
            addUserBtn.setOpacity(0.0);
            viewUsersBtn.setOpacity(0.0);

            KeyFrame fadeOutBack = new KeyFrame(Duration.millis(300),
                    new KeyValue(backBtn.opacityProperty(), 0.0));
            KeyFrame fadeInAdd = new KeyFrame(Duration.millis(300),
                    new KeyValue(addUserBtn.opacityProperty(), 1.0));
            KeyFrame fadeInView = new KeyFrame(Duration.millis(300),
                    new KeyValue(viewUsersBtn.opacityProperty(), 1.0));

            buttonAnimation.getKeyFrames().addAll(fadeOutBack, fadeInAdd, fadeInView);
            buttonAnimation.setOnFinished(e -> {
                backBtn.setVisible(false);
                backBtn.setOpacity(1.0);
            });
        }

        buttonAnimation.play();
    }

    private void showLoadingIndicator(boolean show) {
        if (loadingIndicator != null) {
            if (show) {
                loadingIndicator.setVisible(true);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(200), loadingIndicator);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } else {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(200), loadingIndicator);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> loadingIndicator.setVisible(false));
                fadeOut.play();
            }
        }
    }

    private void showMessage(String message, String type) {
        // Create a temporary message label for feedback
        // This would typically be implemented with a proper notification system
        System.out.println("[" + type.toUpperCase() + "] " + message);
    }
}