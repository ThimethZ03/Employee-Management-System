package com.yourcompany.ems.controllers;

import com.yourcompany.ems.models.Message;
import com.yourcompany.ems.database.DatabaseConnection;
import com.yourcompany.ems.database.EmployeeDAO;
import com.yourcompany.ems.models.Employee;
import com.yourcompany.ems.models.User;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.json.JSONObject;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class NewDashboardController implements Initializable {

    // FXML Components
    @FXML private Label welcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Label lastUpdateLabel;
    @FXML private ImageView avatarImageView;
    @FXML private TableView<Message> recentMessagesTable;
    @FXML private TableColumn<Message, String> senderColumn;
    @FXML private TableColumn<Message, String> subjectColumn;
    @FXML private TableColumn<Message, Timestamp> sentTimeColumn;

    // Clock and Date
    @FXML private Label clockLabel;
    @FXML private Label dateLabel;

    // Weather Components
    @FXML private Label temperatureLabel;
    @FXML private Label weatherDescriptionLabel;
    @FXML private Label locationLabel;
    @FXML private Label windLabel;
    @FXML private Label humidityLabel;
    @FXML private Label pressureLabel;
    @FXML private ImageView weatherIcon;
    @FXML private AnchorPane refreshWeatherBtn;

    // Statistics
    @FXML private Label totalEmployeesLabel;
    @FXML private Label totalMessagesLabel;
    @FXML private Label activeUsersLabel;

    // Section Containers
    @FXML private AnchorPane weatherSection;
    @FXML private AnchorPane messagesSection;
    @FXML private AnchorPane clockSection;
    @FXML private AnchorPane profileSection;
    @FXML private AnchorPane statsSection;
    @FXML private AnchorPane quickActionsSection;

    // Floating particles for animation
    @FXML private Label particle1;
    @FXML private Label particle2;
    @FXML private Label particle3;
    @FXML private Label particle4;

    // Animation and timing
    private Timeline clockTimeline;
    private Timeline particleAnimation;
    private Timeline pulseAnimation;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");

    // Weather API configuration
    private static final String WEATHER_API_KEY = "78c014a37a1b8a433daa327e966bb011";
    private static final String WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String IP_API_URL = "http://ip-api.com/json";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // FORCE CLOCK VISIBILITY FIRST - before any animations
            if (clockSection != null) {
                clockSection.setVisible(true);
                clockSection.setOpacity(1.0);
                clockSection.setScaleX(1.0);
                clockSection.setScaleY(1.0);
                clockSection.setTranslateY(0);
            }
            if (clockLabel != null) {
                clockLabel.setVisible(true);
                clockLabel.setOpacity(1.0);
                clockLabel.setText("00:00:00");
            }
            if (dateLabel != null) {
                dateLabel.setVisible(true);
                dateLabel.setOpacity(1.0);
                dateLabel.setText("Loading date...");
            }

            // Start clock immediately
            startClock();

            // Initialize other components
            initializeTableColumns();

            // Load other data
            loadUserProfile();
            loadStatistics();
            loadRecentMessages();
            loadWeather();
            setupEventHandlers();

            // Do animations AFTER clock is established
            Platform.runLater(() -> {
                initializeAnimations();
                updateLastUpdate();
            });

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Failed to initialize dashboard: " + e.getMessage());

            // Emergency fallback for clock
            Platform.runLater(() -> {
                if (clockSection != null) {
                    clockSection.setVisible(true);
                    clockSection.setOpacity(1.0);
                }
                if (clockLabel != null) {
                    clockLabel.setText("12:00:00");
                    clockLabel.setVisible(true);
                    clockLabel.setOpacity(1.0);
                }
            });
        }
    }

    private void initializeTableColumns() {
        if (senderColumn != null) {
            senderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        }
        if (subjectColumn != null) {
            subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        }
        if (sentTimeColumn != null) {
            sentTimeColumn.setCellValueFactory(new PropertyValueFactory<>("sentTime"));
        }
    }

    private void initializeAnimations() {
        // Add entrance animations with staggered delays
        List<Node> sections = List.of(clockSection, profileSection, weatherSection,
                statsSection, quickActionsSection, messagesSection);

        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i) != null) {
                addEntranceAnimation(sections.get(i), i * 150);
            }
        }

        // Setup hover effects
        setupHoverEffects();

        // Setup floating particle animations
        setupParticleAnimations();

        // Setup weather icon pulse
        setupWeatherIconPulse();
    }

    private void addEntranceAnimation(Node node, int delayMs) {
        if (node == null) return;

        // Special handling for clock section - skip animation to ensure visibility
        if (node == clockSection) {
            node.setOpacity(1.0);
            node.setVisible(true);
            node.setScaleX(1.0);
            node.setScaleY(1.0);
            node.setTranslateY(0);
            return; // Skip animation for clock
        }

        // Initial state for other nodes
        node.setOpacity(0);
        node.setScaleX(0.8);
        node.setScaleY(0.8);
        node.setTranslateY(20);
        node.setVisible(true);

        // Create entrance timeline
        Timeline entrance = new Timeline();

        KeyValue[] values = {
                new KeyValue(node.opacityProperty(), 1.0, Interpolator.EASE_OUT),
                new KeyValue(node.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                new KeyValue(node.scaleYProperty(), 1.0, Interpolator.EASE_OUT),
                new KeyValue(node.translateYProperty(), 0, Interpolator.EASE_OUT)
        };

        entrance.getKeyFrames().add(new KeyFrame(Duration.millis(600), values));

        entrance.setOnFinished(e -> {
            node.setOpacity(1.0);
            node.setVisible(true);
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(delayMs), e -> {
            try {
                entrance.play();
            } catch (Exception ex) {
                node.setOpacity(1.0);
                node.setVisible(true);
                ex.printStackTrace();
            }
        }));
        timeline.play();
    }

    private void setupHoverEffects() {
        List<Node> interactiveNodes = List.of(weatherSection, messagesSection,
                clockSection, profileSection,
                statsSection, quickActionsSection);

        interactiveNodes.forEach(node -> {
            if (node != null) {
                addHoverEffect(node);
            }
        });

        // Special hover effect for avatar
        if (avatarImageView != null) {
            setupAvatarHover();
        }

        // Weather refresh button
        if (refreshWeatherBtn != null) {
            setupButtonHover(refreshWeatherBtn);
        }
    }

    private void addHoverEffect(Node node) {
        DropShadow originalShadow = new DropShadow(10, Color.rgb(0, 0, 0, 0.2));
        DropShadow hoverShadow = new DropShadow(15, Color.rgb(52, 152, 219, 0.4));

        node.setEffect(originalShadow);

        node.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
            scale.setToX(1.03);
            scale.setToY(1.03);
            scale.setInterpolator(Interpolator.EASE_OUT);
            scale.play();

            node.setEffect(hoverShadow);
        });

        node.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.setInterpolator(Interpolator.EASE_OUT);
            scale.play();

            node.setEffect(originalShadow);
        });
    }

    private void setupAvatarHover() {
        avatarImageView.setOnMouseEntered(e -> {
            RotateTransition rotate = new RotateTransition(Duration.millis(300), avatarImageView);
            rotate.setToAngle(5);

            ScaleTransition scale = new ScaleTransition(Duration.millis(300), avatarImageView);
            scale.setToX(1.1);
            scale.setToY(1.1);

            new ParallelTransition(rotate, scale).play();
        });

        avatarImageView.setOnMouseExited(e -> {
            RotateTransition rotate = new RotateTransition(Duration.millis(300), avatarImageView);
            rotate.setToAngle(0);

            ScaleTransition scale = new ScaleTransition(Duration.millis(300), avatarImageView);
            scale.setToX(1.0);
            scale.setToY(1.0);

            new ParallelTransition(rotate, scale).play();
        });
    }

    private void setupButtonHover(Node button) {
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }

    private void setupParticleAnimations() {
        List<Label> particles = List.of(particle1, particle2, particle3, particle4);

        for (Label particle : particles) {
            if (particle != null) {
                animateParticle(particle);
            }
        }
    }

    private void animateParticle(Label particle) {
        // Floating animation
        TranslateTransition float1 = new TranslateTransition(Duration.seconds(3 + Math.random() * 2), particle);
        float1.setByY(-20 - Math.random() * 20);
        float1.setAutoReverse(true);
        float1.setCycleCount(Timeline.INDEFINITE);
        float1.setInterpolator(Interpolator.EASE_BOTH);

        // Rotation animation
        RotateTransition rotate = new RotateTransition(Duration.seconds(4 + Math.random() * 3), particle);
        rotate.setByAngle(360);
        rotate.setCycleCount(Timeline.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);

        // Opacity pulse
        FadeTransition fade = new FadeTransition(Duration.seconds(2 + Math.random()), particle);
        fade.setFromValue(0.3);
        fade.setToValue(0.8);
        fade.setAutoReverse(true);
        fade.setCycleCount(Timeline.INDEFINITE);

        new ParallelTransition(float1, rotate, fade).play();
    }

    private void setupWeatherIconPulse() {
        if (weatherIcon != null) {
            pulseAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(weatherIcon.scaleXProperty(), 1.0),
                            new KeyValue(weatherIcon.scaleYProperty(), 1.0)),
                    new KeyFrame(Duration.seconds(2),
                            new KeyValue(weatherIcon.scaleXProperty(), 1.1, Interpolator.EASE_BOTH),
                            new KeyValue(weatherIcon.scaleYProperty(), 1.1, Interpolator.EASE_BOTH)),
                    new KeyFrame(Duration.seconds(4),
                            new KeyValue(weatherIcon.scaleXProperty(), 1.0, Interpolator.EASE_BOTH),
                            new KeyValue(weatherIcon.scaleYProperty(), 1.0, Interpolator.EASE_BOTH))
            );
            pulseAnimation.setCycleCount(Timeline.INDEFINITE);
        }
    }

    private void startClock() {
        try {
            // Immediate update
            updateClockNow();

            if (clockTimeline != null) {
                clockTimeline.stop();
            }

            clockTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                updateClockNow();
            }));

            clockTimeline.setCycleCount(Timeline.INDEFINITE);
            clockTimeline.play();

        } catch (Exception e) {
            System.err.println("Failed to start clock: " + e.getMessage());
            e.printStackTrace();

            // Set a static time if dynamic update fails
            if (clockLabel != null) {
                clockLabel.setText("12:00:00");
                clockLabel.setVisible(true);
            }
        }
    }

    private void updateClockNow() {
        try {
            LocalTime now = LocalTime.now();
            LocalDateTime dateTime = LocalDateTime.now();

            String timeText = now.format(timeFormatter);
            String dateText = dateTime.format(dateFormatter);

            Platform.runLater(() -> {
                if (clockLabel != null) {
                    clockLabel.setText(timeText);
                    clockLabel.setVisible(true);
                    clockLabel.setOpacity(1.0);
                }

                if (dateLabel != null) {
                    dateLabel.setText(dateText);
                    dateLabel.setVisible(true);
                    dateLabel.setOpacity(1.0);
                }
            });
        } catch (Exception e) {
            System.err.println("Error updating clock: " + e.getMessage());
            Platform.runLater(() -> {
                if (clockLabel != null) {
                    clockLabel.setText("--:--:--");
                    clockLabel.setVisible(true);
                }
            });
        }
    }

    // Alternative method with simple fade animation (use this if you want some animation)
    private void updateClockWithAnimation() {
        try {
            LocalTime now = LocalTime.now();
            LocalDateTime dateTime = LocalDateTime.now();

            if (clockLabel != null) {
                String timeText = now.format(timeFormatter);
                if (!timeText.equals(clockLabel.getText())) {
                    // Simple fade without complex animations
                    clockLabel.setOpacity(0.7);
                    clockLabel.setText(timeText);

                    FadeTransition fade = new FadeTransition(Duration.millis(300), clockLabel);
                    fade.setToValue(1.0);
                    fade.play();
                }
            }

            if (dateLabel != null) {
                String dateText = dateTime.format(dateFormatter);
                dateLabel.setText(dateText);
            }
        } catch (Exception e) {
            System.err.println("Error updating clock: " + e.getMessage());
        }
    }

    private void loadUserProfile() {
        User currentUser = User.getCurrentUser();
        if (currentUser == null) {
            setWelcomeMessage("Welcome to Dashboard");
            return;
        }

        CompletableFuture.supplyAsync(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                EmployeeDAO employeeDAO = new EmployeeDAO(conn);
                return employeeDAO.getEmployeeByEmail(currentUser.getUsername());
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }).thenAccept(employee -> Platform.runLater(() -> {
            if (employee != null) {
                setWelcomeMessage("Welcome back,\n" + employee.getName() + "!");
                loadUserAvatar(employee.getAvatarPath());
                updateStatus("Online");
            } else {
                setWelcomeMessage("Welcome back,\n" + currentUser.getUsername());
                updateStatus("Online");
            }
        }));
    }

    private void setWelcomeMessage(String message) {
        if (welcomeLabel != null) {
            animateTextChange(welcomeLabel, message);
        }
    }

    private void loadUserAvatar(String avatarPath) {
        if (avatarImageView != null && avatarPath != null && !avatarPath.isEmpty()) {
            try {
                File avatarFile = new File(avatarPath);
                if (avatarFile.exists()) {
                    Image avatar = new Image(avatarFile.toURI().toString());
                    animateImageChange(avatarImageView, avatar);
                }
            } catch (Exception e) {
                System.out.println("Could not load avatar: " + e.getMessage());
            }
        }
    }

    private void updateStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText("● " + status);
        }
    }

    private void updateLastUpdate() {
        if (lastUpdateLabel != null) {
            lastUpdateLabel.setText("Last updated: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    private void loadStatistics() {
        CompletableFuture.supplyAsync(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                return new Statistics(
                        getEmployeeCount(conn),
                        getMessageCount(conn),
                        getActiveUserCount(conn)
                );
            } catch (SQLException e) {
                e.printStackTrace();
                return new Statistics(0, 0, 0);
            }
        }).thenAccept(stats -> Platform.runLater(() -> updateStatisticsDisplay(stats)));
    }

    private int getEmployeeCount(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM employees";
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getMessageCount(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM messages";
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int getActiveUserCount(Connection conn) throws SQLException {
        String query = "SELECT COUNT(DISTINCT sender) FROM messages WHERE DATE(sent_time) = CURDATE()";
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void updateStatisticsDisplay(Statistics stats) {
        if (totalEmployeesLabel != null) {
            animateNumberChange(totalEmployeesLabel, stats.totalEmployees);
        }
        if (totalMessagesLabel != null) {
            animateNumberChange(totalMessagesLabel, stats.totalMessages);
        }
        if (activeUsersLabel != null) {
            animateNumberChange(activeUsersLabel, stats.activeUsers);
        }
    }

    private void loadRecentMessages() {
        CompletableFuture.supplyAsync(() -> {
            List<Message> messages = new ArrayList<>();
            String query = "SELECT * FROM messages ORDER BY sent_time DESC LIMIT 5";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Message message = new Message(
                            rs.getInt("id"),
                            rs.getString("sender"),
                            null, // recipient
                            rs.getString("subject"),
                            null, // content
                            rs.getTimestamp("sent_time"),
                            false, // isRead
                            null   // attachment
                    );
                    messages.add(message);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return messages;
        }).thenAccept(messages -> Platform.runLater(() -> updateMessagesTable(messages)));
    }

    private void updateMessagesTable(List<Message> messages) {
        if (recentMessagesTable != null) {
            ObservableList<Message> observableMessages = FXCollections.observableArrayList(messages);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), recentMessagesTable);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                recentMessagesTable.setItems(observableMessages);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), recentMessagesTable);
                fadeIn.setToValue(1);
                fadeIn.play();
            });
            fadeOut.play();
        }
    }

    private void loadWeather() {
        if (temperatureLabel != null) {
            temperatureLabel.setText("Loading...");
        }
        if (weatherDescriptionLabel != null) {
            weatherDescriptionLabel.setText("Fetching weather...");
        }

        CompletableFuture.supplyAsync(this::fetchWeatherData)
                .thenAccept(weather -> Platform.runLater(() -> {
                    if (weather != null) {
                        updateWeatherDisplay(weather);
                        if (pulseAnimation != null) {
                            pulseAnimation.play();
                        }
                    } else {
                        showWeatherError();
                    }
                }));
    }

    private WeatherData fetchWeatherData() {
        try {
            // Get location from IP
            String city = getCurrentCity();
            if (city == null) city = "New York"; // fallback

            // Fetch weather data
            String weatherUrl = WEATHER_BASE_URL + "?q=" + city + "&appid=" + WEATHER_API_KEY + "&units=metric";
            URL url = new URL(weatherUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) {
                return null;
            }

            StringBuilder response = new StringBuilder();
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
            }

            return parseWeatherData(response.toString(), city);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getCurrentCity() {
        try {
            URL url = new URL(IP_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            StringBuilder response = new StringBuilder();
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
            }

            JSONObject data = new JSONObject(response.toString());
            return data.optString("city", "Unknown");

        } catch (Exception e) {
            return null;
        }
    }

    private WeatherData parseWeatherData(String jsonResponse, String city) {
        try {
            JSONObject data = new JSONObject(jsonResponse);
            JSONObject main = data.getJSONObject("main");
            JSONObject weather = data.getJSONArray("weather").getJSONObject(0);
            JSONObject wind = data.getJSONObject("wind");

            return new WeatherData(
                    main.getDouble("temp"),
                    weather.getString("description"),
                    weather.getString("icon"),
                    wind.optDouble("speed", 0),
                    main.optInt("pressure", 0),
                    main.optInt("humidity", 0),
                    city
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateWeatherDisplay(WeatherData weather) {
        animateTextChange(temperatureLabel, String.format("%.1f°C", weather.temperature));
        animateTextChange(weatherDescriptionLabel, capitalizeFirst(weather.description));
        animateTextChange(locationLabel, "📍 " + weather.city);
        animateTextChange(windLabel, String.format("💨 Wind: %.1f m/s", weather.windSpeed));
        animateTextChange(pressureLabel, "🌡️ Pressure: " + weather.pressure + " hPa");
        animateTextChange(humidityLabel, "💧 Humidity: " + weather.humidity + "%");

        // Load weather icon
        if (weatherIcon != null && weather.icon != null) {
            try {
                String iconUrl = "https://openweathermap.org/img/wn/" + weather.icon + "@2x.png";
                Image icon = new Image(iconUrl, true);
                animateImageChange(weatherIcon, icon);
            } catch (Exception e) {
                System.out.println("Could not load weather icon: " + e.getMessage());
            }
        }
    }

    private void showWeatherError() {
        if (temperatureLabel != null) {
            temperatureLabel.setText("Weather unavailable");
        }
        if (weatherDescriptionLabel != null) {
            weatherDescriptionLabel.setText("Could not fetch weather data");
        }
    }

    private void setupEventHandlers() {
        if (refreshWeatherBtn != null) {
            refreshWeatherBtn.setOnMouseClicked(this::handleRefreshWeather);
        }
    }

    @FXML
    private void handleRefreshWeather(MouseEvent event) {
        loadWeather();
        updateLastUpdate();
    }

    // Animation utility methods
    private void animateTextChange(Label label, String newText) {
        if (label == null || newText == null) return;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), label);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            label.setText(newText);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), label);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void animateImageChange(ImageView imageView, Image newImage) {
        if (imageView == null || newImage == null) return;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), imageView);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            imageView.setImage(newImage);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), imageView);
            fadeIn.setToValue(1);

            ScaleTransition scale = new ScaleTransition(Duration.millis(300), imageView);
            scale.setFromX(0.8);
            scale.setFromY(0.8);
            scale.setToX(1.0);
            scale.setToY(1.0);

            new ParallelTransition(fadeIn, scale).play();
        });
        fadeOut.play();
    }

    private void animateNumberChange(Label label, int newValue) {
        if (label == null) return;

        Timeline numberAnimation = new Timeline();
        int currentValue = 0;
        try {
            currentValue = Integer.parseInt(label.getText());
        } catch (NumberFormatException ignored) {}

        final int startValue = currentValue;
        final int endValue = newValue;
        final int duration = 1000; // 1 second

        KeyFrame keyFrame = new KeyFrame(Duration.millis(duration), e -> {
            label.setText(String.valueOf(endValue));
        });

        numberAnimation.getKeyFrames().add(keyFrame);

        // Animate the counting
        Timeline countAnimation = new Timeline();
        for (int i = 0; i <= 50; i++) {
            final int step = i;
            KeyFrame frame = new KeyFrame(Duration.millis(i * 20), e -> {
                int currentNumber = startValue + (int)((endValue - startValue) * (step / 50.0));
                label.setText(String.valueOf(currentNumber));
            });
            countAnimation.getKeyFrames().add(frame);
        }

        countAnimation.play();
    }

    private void showErrorMessage(String message) {
        System.err.println("Dashboard Error: " + message);
        // You could show this in a status bar or notification area if available
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // Cleanup method
    public void cleanup() {
        if (clockTimeline != null) {
            clockTimeline.stop();
        }
        if (particleAnimation != null) {
            particleAnimation.stop();
        }
        if (pulseAnimation != null) {
            pulseAnimation.stop();
        }
    }

    // Data classes
    private static class WeatherData {
        final double temperature;
        final String description;
        final String icon;
        final double windSpeed;
        final int pressure;
        final int humidity;
        final String city;

        WeatherData(double temperature, String description, String icon,
                    double windSpeed, int pressure, int humidity, String city) {
            this.temperature = temperature;
            this.description = description;
            this.icon = icon;
            this.windSpeed = windSpeed;
            this.pressure = pressure;
            this.humidity = humidity;
            this.city = city;
        }
    }

    private static class Statistics {
        final int totalEmployees;
        final int totalMessages;
        final int activeUsers;

        Statistics(int totalEmployees, int totalMessages, int activeUsers) {
            this.totalEmployees = totalEmployees;
            this.totalMessages = totalMessages;
            this.activeUsers = activeUsers;
        }
    }
}




