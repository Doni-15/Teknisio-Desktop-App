package com.teknisio.controller;

import com.teknisio.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OrderHistoryController implements Initializable {

    @FXML
    private Label txtHistorySubtitle;

    @FXML
    private HBox layoutStatusFilters;

    @FXML
    private VBox layoutOrders;

    @FXML
    private Label txtOrderEmpty;

    @FXML
    private Button btnRefreshOrders;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Load order history from backend
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
        // TODO: Refresh orders
    }

    @FXML
    private void handleHomeTab(MouseEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/home_user.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to Home: " + e.getMessage());
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