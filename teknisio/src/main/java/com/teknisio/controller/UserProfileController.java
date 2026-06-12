package com.teknisio.controller;

import com.teknisio.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {

    @FXML
    private StackPane profileImageWrapper;

    @FXML
    private ImageView profileImageView;

    @FXML
    private Label profileNameLabel;

    @FXML
    private Label rowNameVal;

    @FXML
    private Label rowEmailVal;

    @FXML
    private Label rowPhoneVal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Crop profile image to a circle (92x92 px, so radius is 46, center is 46, 46)
        Circle clip = new Circle(46, 46, 46);
        profileImageView.setClip(clip);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        navigateHome();
    }

    @FXML
    private void handleEditName(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog(rowNameVal.getText());
        dialog.setTitle("Edit Name");
        dialog.setHeaderText("Change your profile name:");
        dialog.setContentText("Name:");

        // Load custom styles
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("alert-dialog");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            if (!newName.trim().isEmpty()) {
                profileNameLabel.setText(newName.trim());
                rowNameVal.setText(newName.trim());
            }
        });
    }

    @FXML
    private void handleEditPhone(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog(rowPhoneVal.getText());
        dialog.setTitle("Edit Phone Number");
        dialog.setHeaderText("Change your phone number:");
        dialog.setContentText("Phone:");

        // Load custom styles
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("alert-dialog");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPhone -> {
            if (!newPhone.trim().isEmpty()) {
                rowPhoneVal.setText(newPhone.trim());
            }
        });
    }

    @FXML
    private void handleLanguageClick(MouseEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Settings Selection", "Language settings page will be opened soon.");
    }

    @FXML
    private void handleTermsClick(MouseEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Settings Selection", "Terms & Conditions document is loading...");
    }

    @FXML
    private void handlePrivacyClick(MouseEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Settings Selection", "Privacy Policy document is loading...");
    }

    @FXML
    private void handleCallCenterClick(MouseEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Settings Selection", "Connecting to Call Center...");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Show success alert and return to walkthrough/login
        showAlert(Alert.AlertType.INFORMATION, "Logout", "You have successfully logged out of Teknisio.");
        
        try {
            Main.setRoot("/com/teknisio/fxml/walkthrough.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to walkthrough: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateHome() {
        try {
            Main.setRoot("/com/teknisio/fxml/home_user.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("alert-dialog");
        
        alert.showAndWait();
    }
}
