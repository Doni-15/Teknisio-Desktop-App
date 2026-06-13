package com.teknisio.controller;

import com.teknisio.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TechnicianRequestDetailController implements Initializable {

    @FXML private Label txtTechOrderCode;
    @FXML private StackPane techStatusBadgeContainer;
    @FXML private Label txtTechOrderStatus;
    @FXML private Label txtTechOrderTime;
    @FXML private Label txtTechCustomer;
    @FXML private Label txtTechCategories;
    @FXML private Label txtTechIssue;
    @FXML private Label txtTechAddress;
    @FXML private Label txtTechCost;
    @FXML private Label txtTechNote;
    @FXML private Label txtStatusHistoryLabel;
    @FXML private VBox layoutStatusHistory;
    @FXML private VBox layoutTechnicianActionPanel;
    @FXML private Label txtTechDetailMessage;
    @FXML private Button btnAcceptRequest;
    @FXML private Button btnStartRequest;
    @FXML private Button btnNavigateToCustomer;
    @FXML private Button btnTechnicianChat;
    @FXML private Button btnCompleteRequest;
    @FXML private Button btnRejectRequest;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Load request detail from backend
    }

    @FXML
    private void handleBack() {
        try {
            Main.setRoot("/com/teknisio/fxml/TechnicianHome.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML private void handleAcceptRequest() { /* TODO */ }
    @FXML private void handleStartRequest() { /* TODO */ }
    @FXML private void handleCompleteRequest() { /* TODO */ }
    @FXML private void handleRejectRequest() { /* TODO */ }
    
    @FXML
    private void handleNavigateToCustomer() {
        try {
            Main.setRoot("/com/teknisio/fxml/TrackingMap.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenChat() {
        try {
            Main.setRoot("/com/teknisio/fxml/Chat.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}