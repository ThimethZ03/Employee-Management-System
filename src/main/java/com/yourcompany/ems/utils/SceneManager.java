package com.yourcompany.ems.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    /**
     * Switches the entire scene (e.g., used for login screen, forms, etc.)
     */
    public static void changeScene(Stage stage, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads FXML content into a target AnchorPane (used for loading internal views)
     */
    public static void loadInPane(AnchorPane container, String fxmlPath) {
        try {
            Parent pane = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            container.getChildren().setAll(pane);
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads an FXML and returns the controller instance for advanced usage (e.g., dependency injection).
     * Must call this from the caller's controller.
     */
    public static <T> T loadWithController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            loader.load();
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

