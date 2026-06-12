package com.teknisio.controller;

import com.teknisio.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField phoneField;

    @FXML
    private Button confirmButton;

    @FXML
    private Label loginLink;

    @FXML
    private void handleRegister(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String phone = phoneField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        // Email validation
        if (!email.contains("@") || !email.contains(".")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid email address.");
            return;
        }

        // Password validation (simple check)
        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 6 characters.");
            return;
        }

        // Phone number validation (numeric check)
        if (!phone.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Phone number must contain only numbers.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Success", "Registration Successful!\nYou can now log in.");

        // Automatically switch back to login screen
        try {
            Main.setRoot("/com/teknisio/fxml/login.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load login page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin(MouseEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/login.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load login page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Premium customization of the alert dialog styling
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("alert-dialog");
        
        alert.showAndWait();
    }
}
