package com.teknisio.controller;

import com.teknisio.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    private ImageView ahmedAvatar;

    @FXML
    private ImageView evanAvatar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Crop avatars to perfect circles
        Circle clipAhmed = new Circle(24, 24, 24);
        ahmedAvatar.setClip(clipAhmed);

        Circle clipEvan = new Circle(24, 24, 24);
        evanAvatar.setClip(clipEvan);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/home_user.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAhmedClick(MouseEvent event) {
        showChatAlert("Ahmed Rush");
    }

    @FXML
    private void handleEvanClick(MouseEvent event) {
        showChatAlert("Evan Bran");
    }

    private void showChatAlert(String name) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chat Room");
        alert.setHeaderText(null);
        alert.setContentText("Opening chat room with: " + name + "\nFeatures will be fully integrated with database messaging soon.");

        // Load custom styles
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("alert-dialog");

        alert.showAndWait();
    }
}
