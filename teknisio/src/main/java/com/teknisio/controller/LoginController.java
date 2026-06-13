package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.dto.AuthResponse;
import com.teknisio.dto.LoginRequest;
import com.teknisio.service.ApiClient;
import com.teknisio.service.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button confirmButton;
    @FXML private Label registerLink;

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Harap isi semua field.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showAlert(Alert.AlertType.ERROR, "Validasi", "Format email tidak valid.");
            return;
        }

        // Call backend API
        try {
            LoginRequest request = new LoginRequest(email, password);
            ApiClient.ApiResponse<AuthResponse> apiResponse = ApiClient.post(
                "/api/auth/login", request, AuthResponse.class
            );

            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                AuthResponse auth = apiResponse.getData();

                // Store JWT token
                ApiClient.setToken(auth.getToken());

                // Store session
                SessionManager.login(
                    auth.getUserId(),
                    auth.getEmail(),
                    auth.getName(),
                    auth.getPhone(),
                    auth.getAddress(),
                    auth.getRole()
                );

                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Login berhasil!\nSelamat datang, " + auth.getName());

                // Route based on role
                if (SessionManager.isTechnician()) {
                    Main.setRoot("/com/teknisio/fxml/TechnicianHome.fxml");
                } else {
                    Main.setRoot("/com/teknisio/fxml/home_user.fxml");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", apiResponse.getMessage());
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal terhubung ke server.\nPastikan backend berjalan di localhost:8080");
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Request timeout. Coba lagi.");
            Thread.currentThread().interrupt();
        }
    }

    @FXML
    private void goToRegister(MouseEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/register.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load register page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            try {
                alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
                alert.getDialogPane().getStyleClass().add("alert-dialog");
            } catch (Exception ignored) {}
            alert.showAndWait();
        });
    }
}