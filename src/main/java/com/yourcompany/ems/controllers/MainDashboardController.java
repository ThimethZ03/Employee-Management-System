package com.yourcompany.ems.controllers;

import com.yourcompany.ems.utils.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainDashboardController {

    @FXML
    private VBox sidebar;

    @FXML
    private AnchorPane contentArea;

    @FXML
    public void initialize() {
        loadSidebar();
        loadDashboardView();
    }

    private void loadSidebar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/yourcompany/ems/views/sidebar.fxml"));
            VBox sidebarContent = loader.load();

            SidebarController sidebarController = loader.getController();
            sidebarController.setMainDashboardController(this);

            sidebar.getChildren().setAll(sidebarContent.getChildren());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/yourcompany/ems/views/newdashboard.fxml"));
            AnchorPane dashboardPane = loader.load();

            contentArea.getChildren().setAll(dashboardPane);

            // Optional: access NewDashboardController if you want to call methods
            NewDashboardController dashboardController = loader.getController();
            // etc.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fixed method to load any FXML with any root type
    public void setContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent pane = loader.load();  // Use Parent, not AnchorPane
            contentArea.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}







