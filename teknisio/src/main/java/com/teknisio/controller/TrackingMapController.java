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

public class TrackingMapController implements Initializable {

    @FXML
    private Label txtTrackingTitle;
    @FXML
    private Label txtLiveBadge;
    @FXML
    private StackPane mapContainer;
    @FXML
    private Label mapPlaceholder;
    @FXML
    private Label txtTrackingStatus;
    @FXML
    private VBox layoutInfoCard;
    @FXML
    private Label txtTechnicianName;
    @FXML
    private Label txtLastUpdate;
    @FXML
    private Label txtDistance;
    @FXML
    private Label txtInfoHint;
    @FXML
    private Button btnCenterMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Initialize map view and start location tracking
    }

    @FXML
    private void handleBack() {
        try {
            Main.setRoot("/com/teknisio/fxml/ServiceRequestDetail.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCenterMap() {
        // TODO: Center map on technician's location
    }
}