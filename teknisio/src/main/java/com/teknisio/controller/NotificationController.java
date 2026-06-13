package com.teknisio.controller;

import com.teknisio.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NotificationController implements Initializable {

    @FXML
    private Label txtNotificationSubtitle;

    @FXML
    private VBox layoutNotifications;

    @FXML
    private Label txtNotificationEmpty;

    @FXML
    private Button btnRetry;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Load notifications from backend
    }

    @FXML
    private void handleBack() {
        try {
            Main.setRoot("/com/teknisio/fxml/home_user.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        // TODO: Refresh notifications
    }
}