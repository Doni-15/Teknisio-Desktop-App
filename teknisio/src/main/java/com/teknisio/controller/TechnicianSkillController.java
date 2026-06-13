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

public class TechnicianSkillController implements Initializable {

    @FXML
    private Label txtSkillSubtitle;
    @FXML
    private VBox layoutCurrentSkills;
    @FXML
    private Label txtCurrentSkillEmpty;
    @FXML
    private VBox layoutAvailableSkills;
    @FXML
    private Label txtAvailableSkillEmpty;
    @FXML
    private Button btnRefreshSkills;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Load skills from backend
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
        // TODO: Refresh skills
    }
}