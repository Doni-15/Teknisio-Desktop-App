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

public class ServiceRequestDetailController implements Initializable {

    @FXML
    private Label txtOrderCode;
    @FXML
    private StackPane statusBadgeContainer;
    @FXML
    private Label txtOrderStatus;
    @FXML
    private Label txtOrderTime;

    @FXML
    private VBox layoutOrderTechnicianSummary;
    @FXML
    private Label txtOrderTechnicianName;
    @FXML
    private Label txtOrderTechnicianMeta;
    @FXML
    private Label txtOrderTechnicianCategories;

    @FXML
    private Label txtOrderCategories;
    @FXML
    private Label txtOrderIssue;
    @FXML
    private Label txtOrderAddress;
    @FXML
    private Label txtCancelReason;

    @FXML
    private VBox layoutCompletionSummary;
    @FXML
    private Label txtFinalCostValue;
    @FXML
    private Label txtCompletionSummaryNoteLabel;
    @FXML
    private Label txtCompletionSummaryNote;

    @FXML
    private Label txtStatusHistoryLabel;
    @FXML
    private VBox layoutStatusHistory;
    @FXML
    private Label txtDetailMessage;

    @FXML
    private Button btnCancelOrder;
    @FXML
    private Button btnWriteReview;
    @FXML
    private Button btnTrackTechnician;
    @FXML
    private Button btnOpenChat;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Load service request detail from backend
    }

    @FXML
    private void handleBack() {
        try {
            Main.setRoot("/com/teknisio/fxml/OrderHistory.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelOrder() {
        // TODO: Cancel order logic
    }

    @FXML
    private void handleWriteReview() {
        // TODO: Write review logic
    }

    @FXML
    private void handleTrackTechnician() {
        try {
            Main.setRoot("/com/teknisio/fxml/TrackingMap.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to TrackingMap: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenChat() {
        try {
            Main.setRoot("/com/teknisio/fxml/Chat.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to Chat: " + e.getMessage());
            e.printStackTrace();
        }
    }
}