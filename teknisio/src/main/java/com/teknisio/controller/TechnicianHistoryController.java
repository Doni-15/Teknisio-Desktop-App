package com.teknisio.controller;

import com.teknisio.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TechnicianHistoryController implements Initializable {

    @FXML
    private Label txtTechnicianHistorySubtitle;
    @FXML
    private HBox layoutTechnicianHistoryFilters;
    @FXML
    private VBox layoutTechnicianHistoryRequests;
    @FXML
    private Label txtTechnicianHistoryEmpty;
    @FXML
    private Button btnRefreshTechnicianHistory;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Load technician history from backend
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

    @FXML
    private void handleRefresh() {
        // TODO: Refresh history
    }
}