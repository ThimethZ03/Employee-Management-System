package com.yourcompany.ems.controllers;

import com.yourcompany.ems.database.DatabaseConnection;
import com.yourcompany.ems.utils.EmailReceiver;
import com.yourcompany.ems.utils.EmailSender;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;
import com.yourcompany.ems.models.Message;
import javafx.concurrent.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

// Animation imports
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.effect.GaussianBlur;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessagesController {

    @FXML private TableView<Message> messagesTable;
    @FXML private TableColumn<Message, String> senderColumn;
    @FXML private TableColumn<Message, String> subjectColumn;
    @FXML private TableColumn<Message, Timestamp> dateColumn;

    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea bodyArea;

    @FXML private Label unreadCountLabel;

    // New FXML elements for enhanced UI
    @FXML private VBox mainContainer;
    @FXML private VBox sendMessageForm;
    @FXML private VBox formFields;
    @FXML private StackPane notificationStack;
    @FXML private Button notificationButton;
    @FXML private Button viewButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    @FXML private Button sendButton;
    @FXML private Button clearButton;
    @FXML private Button toggleFormButton;
    @FXML private StackPane loadingOverlay;
    @FXML private Label loadingLabel;
    @FXML private Label statusLabel;
    @FXML private Label lastUpdatedLabel;
    @FXML private HBox statusBar;

    private Connection conn;
    private Timeline pulseTimeline;
    private boolean isFormCollapsed = false;

    @FXML
    public void initialize() {
        try {
            conn = DatabaseConnection.getConnection();
            senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
            subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("sentTime"));

            setupAnimations();
            setupInteractions();
            loadMessages();
            updateUnreadCount();

            // Entrance animation
            playEntranceAnimation();

            Timer timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    Platform.runLater(() -> {
                        fetchNewEmails();
                        loadMessages();
                        updateUnreadCount();
                    });
                }
            }, 0, 120000);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database connection error: " + e.getMessage());
        }
    }

    private void setupAnimations() {
        // Notification pulse animation
        pulseTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(notificationStack.scaleXProperty(), 1.0)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(notificationStack.scaleXProperty(), 1.2)),
                new KeyFrame(Duration.seconds(1), new KeyValue(notificationStack.scaleXProperty(), 1.0))
        );
        pulseTimeline.setCycleCount(Timeline.INDEFINITE);

        // Button hover animations
        setupButtonHoverEffects(viewButton);
        setupButtonHoverEffects(deleteButton);
        setupButtonHoverEffects(refreshButton);
        setupButtonHoverEffects(sendButton);
        setupButtonHoverEffects(clearButton);

        // Form field focus animations
        setupFieldFocusEffects(toField);
        setupFieldFocusEffects(subjectField);
        setupFieldFocusEffects(bodyArea);
    }

    private void setupButtonHoverEffects(Button button) {
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(100), button);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(100), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        button.setOnMouseEntered(e -> {
            scaleOut.stop();
            scaleIn.play();
        });

        button.setOnMouseExited(e -> {
            scaleIn.stop();
            scaleOut.play();
        });
    }

    private void setupFieldFocusEffects(Control field) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), field);
            if (newVal) {
                scale.setToX(1.02);
                scale.setToY(1.02);
                field.setStyle(field.getStyle() + "-fx-border-color: #3498db; -fx-border-width: 2;");
            } else {
                scale.setToX(1.0);
                scale.setToY(1.0);
                field.setStyle(field.getStyle().replace("-fx-border-color: #3498db; -fx-border-width: 2;", ""));
            }
            scale.play();
        });
    }

    private void setupInteractions() {
        // Table row click animation
        messagesTable.setRowFactory(tv -> {
            TableRow<Message> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    animateRowSelection(row);
                    Platform.runLater(() -> handleView());
                }
            });
            return row;
        });

        // Notification bell click
        notificationButton.setOnAction(e -> animateNotificationClick());

        // Form toggle button
        toggleFormButton.setOnAction(e -> toggleMessageForm());

        // Status updates
        updateStatus("Application loaded successfully");
    }

    private void playEntranceAnimation() {
        mainContainer.setOpacity(0);
        mainContainer.setTranslateY(50);

        FadeTransition fade = new FadeTransition(Duration.millis(800), mainContainer);
        fade.setToValue(1.0);

        TranslateTransition slide = new TranslateTransition(Duration.millis(800), mainContainer);
        slide.setToY(0);

        ParallelTransition entrance = new ParallelTransition(fade, slide);
        entrance.play();
    }

    private void animateRowSelection(TableRow<Message> row) {
        Timeline flash = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(row.opacityProperty(), 1.0)),
                new KeyFrame(Duration.millis(100), new KeyValue(row.opacityProperty(), 0.7)),
                new KeyFrame(Duration.millis(200), new KeyValue(row.opacityProperty(), 1.0))
        );
        flash.setCycleCount(2);
        flash.play();
    }

    private void animateNotificationClick() {
        RotateTransition rotate = new RotateTransition(Duration.millis(500), notificationButton);
        rotate.setByAngle(360);
        rotate.play();

        updateStatus("Checking for new messages...");
        handleRefresh();
    }

    private void toggleMessageForm() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), formFields);
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), formFields);
        FadeTransition fade = new FadeTransition(Duration.millis(300), formFields);

        if (isFormCollapsed) {
            slide.setToY(0);
            scale.setToY(1.0);
            fade.setToValue(1.0);
            toggleFormButton.setText("📝");
            formFields.setVisible(true);
        } else {
            slide.setToY(-formFields.getHeight());
            scale.setToY(0.0);
            fade.setToValue(0.0);
            toggleFormButton.setText("📝");
        }

        ParallelTransition animation = new ParallelTransition(slide, scale, fade);
        animation.setOnFinished(e -> {
            if (isFormCollapsed) {
                formFields.setVisible(true);
            } else {
                formFields.setVisible(false);
            }
        });
        animation.play();

        isFormCollapsed = !isFormCollapsed;
    }

    @FXML
    private void handleSendMessage() {
        String to = toField.getText();
        String subject = subjectField.getText();
        String body = bodyArea.getText();

        if (to.isEmpty() || subject.isEmpty() || body.isEmpty()) {
            animateFormError();
            showAlert(Alert.AlertType.WARNING, "Please fill in all fields.");
            return;
        }

        showLoadingOverlay("Sending message...");
        animateButtonPress(sendButton);

        Task<Void> sendTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1000); // Simulate sending delay
                EmailSender.sendEmail(conn, to, subject, body);
                return null;
            }
        };

        sendTask.setOnSucceeded(e -> {
            hideLoadingOverlay();
            animateFormSuccess();
            clearForm();
            loadMessages();
            updateUnreadCount();
            updateStatus("Message sent successfully!");
        });

        sendTask.setOnFailed(e -> {
            hideLoadingOverlay();
            updateStatus("Failed to send message");
            showAlert(Alert.AlertType.ERROR, "Failed to send message");
        });

        new Thread(sendTask).start();
    }

    private void animateFormError() {
        Timeline shake = new Timeline();
        for (int i = 0; i < 4; i++) {
            KeyFrame kf1 = new KeyFrame(Duration.millis(i * 50), new KeyValue(sendMessageForm.translateXProperty(), -10));
            KeyFrame kf2 = new KeyFrame(Duration.millis(i * 50 + 25), new KeyValue(sendMessageForm.translateXProperty(), 10));
            shake.getKeyFrames().addAll(kf1, kf2);
        }
        shake.getKeyFrames().add(new KeyFrame(Duration.millis(200), new KeyValue(sendMessageForm.translateXProperty(), 0)));
        shake.play();
    }

    private void animateFormSuccess() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), sendMessageForm);
        scale.setToX(1.05);
        scale.setToY(1.05);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    private void animateButtonPress(Button button) {
        ScaleTransition press = new ScaleTransition(Duration.millis(100), button);
        press.setToX(0.95);
        press.setToY(0.95);
        press.setAutoReverse(true);
        press.setCycleCount(2);
        press.play();
    }

    @FXML
    private void handleView() {
        Message selected = messagesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No message selected.");
            return;
        }

        animateButtonPress(viewButton);
        showMessageDialog(selected);
        markAsRead(selected.getId());
        loadMessages();
        updateUnreadCount();
    }

    private void showMessageDialog(Message message) {
        Stage stage = new Stage();
        stage.setTitle("Message Details: " + message.getSubject());

        // Create animated dialog content
        VBox dialogContent = new VBox(15);
        dialogContent.setPadding(new Insets(20));
        dialogContent.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label headerLabel = new Label("Message Details");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPrefSize(600, 300);
        textArea.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");
        textArea.setText(
                "From: " + message.getSender() + "\n" +
                        "To: " + message.getRecipient() + "\n" +
                        "Date: " + message.getSentTime() + "\n\n" +
                        message.getBody()
        );

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8 20;");
        closeBtn.setOnAction(e -> {
            animateButtonPress(closeBtn);
            Timeline delay = new Timeline(new KeyFrame(Duration.millis(200), ev -> stage.close()));
            delay.play();
        });

        dialogContent.getChildren().addAll(headerLabel, textArea, closeBtn);

        Scene scene = new Scene(dialogContent);
        stage.setScene(scene);

        // Entrance animation for dialog
        dialogContent.setOpacity(0);
        dialogContent.setScaleX(0.8);
        dialogContent.setScaleY(0.8);

        stage.show();

        FadeTransition fade = new FadeTransition(Duration.millis(300), dialogContent);
        fade.setToValue(1.0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), dialogContent);
        scale.setToX(1.0);
        scale.setToY(1.0);

        ParallelTransition entrance = new ParallelTransition(fade, scale);
        entrance.play();
    }

    @FXML
    private void handleDelete() {
        Message selected = messagesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No message selected.");
            return;
        }

        animateButtonPress(deleteButton);
        showLoadingOverlay("Deleting message...");

        Task<Void> deleteTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(500); // Simulate deletion delay
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM messages WHERE id = ?")) {
                    stmt.setInt(1, selected.getId());
                    stmt.executeUpdate();
                }
                return null;
            }
        };

        deleteTask.setOnSucceeded(e -> {
            hideLoadingOverlay();
            loadMessages();
            updateUnreadCount();
            updateStatus("Message deleted successfully");
        });

        deleteTask.setOnFailed(e -> {
            hideLoadingOverlay();
            updateStatus("Failed to delete message");
            showAlert(Alert.AlertType.ERROR, "Failed to delete message.");
        });

        new Thread(deleteTask).start();
    }

    @FXML
    private void handleRefresh() {
        animateButtonPress(refreshButton);
        showLoadingOverlay("Refreshing messages...");

        // Add rotation animation to refresh button
        RotateTransition rotate = new RotateTransition(Duration.millis(1000), refreshButton);
        rotate.setByAngle(360);
        rotate.play();

        fetchNewEmails();
        loadMessages();
        updateUnreadCount();

        Timeline hideOverlay = new Timeline(new KeyFrame(Duration.millis(1500), e -> {
            hideLoadingOverlay();
            updateStatus("Messages refreshed");
            updateLastUpdated();
        }));
        hideOverlay.play();
    }

    @FXML
    private void clearForm() {
        animateButtonPress(clearButton);
        toField.clear();
        subjectField.clear();
        bodyArea.clear();
        updateStatus("Form cleared");
    }

    private void showLoadingOverlay(String message) {
        loadingLabel.setText(message);
        loadingOverlay.setVisible(true);

        FadeTransition fade = new FadeTransition(Duration.millis(300), loadingOverlay);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void hideLoadingOverlay() {
        FadeTransition fade = new FadeTransition(Duration.millis(300), loadingOverlay);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> loadingOverlay.setVisible(false));
        fade.play();
    }

    private void loadMessages() {
        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                java.util.List<Message> tempList = new java.util.ArrayList<>();

                String query = "SELECT * FROM messages ORDER BY sent_time DESC";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        Message msg = new Message(
                                rs.getInt("id"),
                                rs.getString("sender"),
                                rs.getString("recipient"),
                                rs.getString("subject"),
                                rs.getString("body"),
                                rs.getTimestamp("sent_time"),
                                rs.getBoolean("is_read"),
                                rs.getString("direction")
                        );
                        tempList.add(msg);
                    }
                }

                Platform.runLater(() -> {
                    ObservableList<Message> observableMessages = FXCollections.observableArrayList(tempList);
                    messagesTable.setItems(observableMessages);
                    animateTableUpdate();
                });

                return null;
            }
        };
        new Thread(loadTask).start();
    }

    private void animateTableUpdate() {
        FadeTransition fade = new FadeTransition(Duration.millis(300), messagesTable);
        fade.setFromValue(0.7);
        fade.setToValue(1.0);
        fade.play();
    }

    private void fetchNewEmails() {
        Task<Void> fetchTask = new Task<>() {
            @Override
            protected Void call() {
                EmailReceiver.fetchUnreadEmails();
                return null;
            }
        };
        fetchTask.setOnSucceeded(e -> {
            loadMessages();
            updateUnreadCount();
        });
        new Thread(fetchTask).start();
    }

    private void updateUnreadCount() {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM messages WHERE is_read = FALSE AND direction = 'RECEIVED'")) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    Platform.runLater(() -> {
                        unreadCountLabel.setText(String.valueOf(count));
                        unreadCountLabel.setVisible(count > 0);

                        // Start pulse animation if there are unread messages
                        if (count > 0 && pulseTimeline.getStatus() != Animation.Status.RUNNING) {
                            pulseTimeline.play();
                        } else if (count == 0) {
                            pulseTimeline.stop();
                            notificationStack.setScaleX(1.0);
                            notificationStack.setScaleY(1.0);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> updateStatus("Error updating unread count"));
        }
    }

    private void markAsRead(int id) {
        try (PreparedStatement stmt = conn.prepareStatement("UPDATE messages SET is_read = TRUE WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus("Error marking message as read");
        }
    }

    private void updateStatus(String message) {
        Platform.runLater(() -> {
            statusLabel.setText(message);
            // Fade animation for status updates
            FadeTransition fade = new FadeTransition(Duration.millis(200), statusLabel);
            fade.setFromValue(0.5);
            fade.setToValue(1.0);
            fade.play();
        });
    }

    private void updateLastUpdated() {
        Platform.runLater(() -> {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            lastUpdatedLabel.setText("Last updated: " + timestamp);
        });
    }

    private void showAlert(Alert.AlertType type, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type, message, ButtonType.OK);
            alert.setTitle("Email Management System");
            alert.setHeaderText(null);

            // Style the alert dialog
            alert.getDialogPane().setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 8;");

            alert.showAndWait();
        });
    }
}
