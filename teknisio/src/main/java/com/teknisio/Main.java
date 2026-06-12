package com.teknisio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class simulating the Teknisio Mobile GUI.
 */
public class Main extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Load the initial Walkthrough/Splash screen
        Parent root = loadFXML("/com/teknisio/fxml/walkthrough.fxml");
        scene = new Scene(root, 360, 740);
        
        stage.setScene(scene);
        stage.setTitle("Teknisio Mobile");
        stage.setResizable(false);
        
        // Add application icon if logo exists
        try {
            Image icon = new Image(getClass().getResource("/com/teknisio/images/logo.png").toExternalForm());
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
        
        stage.show();
    }

    /**
     * Set a new FXML root for the current scene, simulating a screen transition.
     * @param fxml Path to the FXML file
     * @throws IOException if FXML cannot be loaded
     */
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
