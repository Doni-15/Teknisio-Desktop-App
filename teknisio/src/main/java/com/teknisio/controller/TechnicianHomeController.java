package com.teknisio.controller;

import com.teknisio.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TechnicianHomeController implements Initializable {

    @FXML
    private StackPane technicianAvatarWrapper;
    @FXML
    private Label txtTechnicianAvatar;
    @FXML
    private Label txtTechnicianGreeting;
    @FXML
    private Label txtTechnicianSubtitle;
    @FXML
    private Label txtTechnicianRequestCount;
    @FXML
    private VBox cardTechnicianOrders;
    @FXML
    private VBox layoutTechnicianRequests;
    @FXML
    private Label txtTechnicianEmpty;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Load technician data and requests from backend
    }

    @FXML
    private void handleNotification() {
        try {
            Main.setRoot("/com/teknisio/fxml/Notification.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to Notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHistoryTab(MouseEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/TechnicianHistory.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to TechnicianHistory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSkillTab(MouseEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/TechnicianSkill.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to TechnicianSkill: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAccountTab(MouseEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/UserProfile.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to Account: " + e.getMessage());
            e.printStackTrace();
        }
    }
}