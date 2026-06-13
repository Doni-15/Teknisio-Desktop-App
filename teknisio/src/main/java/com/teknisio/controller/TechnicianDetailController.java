package com.teknisio.controller;

import com.teknisio.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TechnicianDetailController implements Initializable {

    @FXML
    private Label txtTechnicianInitial;
    @FXML
    private Label txtTechnicianName;
    @FXML
    private Label txtTechnicianStatus;
    @FXML
    private Label txtRating;
    @FXML
    private Label txtJobs;
    @FXML
    private Label txtReviews;
    @FXML
    private Label txtDescription;
    @FXML
    private VBox layoutSupportedCategories;
    @FXML
    private Label txtDetailMessage;
    @FXML
    private Button btnOrderTechnician;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Load technician detail from backend
    }

    @FXML
    private void handleBack() {
        try {
            Main.setRoot("/com/teknisio/fxml/TechnicianList.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOrderTechnician() {
        try {
            Main.setRoot("/com/teknisio/fxml/OrderTechnician.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to OrderTechnician: " + e.getMessage());
            e.printStackTrace();
        }
    }
}