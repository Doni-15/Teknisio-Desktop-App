package com.teknisio.controller;

import com.teknisio.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Can be expanded to bind or fetch data programmatically
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            if (com.teknisio.service.SessionManager.isTechnician()) {
                Main.setRoot("/com/teknisio/fxml/TechnicianHome.fxml");
            } else {
                Main.setRoot("/com/teknisio/fxml/home_user.fxml");
            }
        } catch (IOException e) {
            System.err.println("Failed to navigate to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
