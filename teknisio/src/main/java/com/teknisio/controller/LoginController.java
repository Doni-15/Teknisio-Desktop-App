package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.dto.AuthResponse;
import com.teknisio.dto.LoginRequest;
import com.teknisio.service.ApiClient;
import com.teknisio.service.SessionManager;
import com.teknisio.service.UserService;

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

        // Disable button to prevent double-click
        if (confirmButton != null) confirmButton.setDisable(true);

        // Run login on background thread to avoid freezing UI
        Thread loginThread = new Thread(() -> {
            try {
                LoginRequest request = new LoginRequest(email, password);
                ApiClient.ApiResponse<AuthResponse> apiResponse = ApiClient.post(
                    "/api/auth/login", request, AuthResponse.class
                );

                if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                    AuthResponse auth = apiResponse.getData();
                    AuthResponse.AuthUserResponse user = auth.getUser();

                    if (user == null) {
                        javafx.application.Platform.runLater(() -> {
                            if (confirmButton != null) confirmButton.setDisable(false);
                            showAlert(Alert.AlertType.ERROR, "Login Gagal", "Data user tidak ditemukan.");
                        });
                        return;
                    }

                    // Store JWT token
                    ApiClient.setToken(auth.getAccessToken());

                    // Store session basic data
                    SessionManager.login(
                        null, // userId as Long not used (backend uses UUID)
                        user.getEmail(),
                        user.getName(),
                        user.getPhoneNumber(),
                        user.getAddress(),
                        user.getRole() != null ? user.getRole() : "CUSTOMER"
                    );

                    // Store photo and technicianProfileId from login response
                    SessionManager.setProfilePhoto(user.getProfilePhoto());
                    if (user.getUserId() != null) {
                        SessionManager.setUserIdString(user.getUserId());
                    }
                    if (user.getTechnicianProfileId() != null) {
                        SessionManager.setTechnicianProfileId(user.getTechnicianProfileId());
                    }

                    javafx.application.Platform.runLater(() -> {
                        showAlert(Alert.AlertType.INFORMATION, "Sukses",
                            "Login berhasil!\nSelamat datang, " + SessionManager.getName());

                        try {
                            // Route based on role
                            if (SessionManager.isTechnician()) {
                                Main.setRoot("/com/teknisio/fxml/TechnicianHome.fxml");
                            } else {
                                Main.setRoot("/com/teknisio/fxml/home_user.fxml");
                            }
                        } catch (IOException ex) {
                            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman utama.");
                        }
                    });
                } else {
                    javafx.application.Platform.runLater(() -> {
                        if (confirmButton != null) confirmButton.setDisable(false);
                        showAlert(Alert.AlertType.ERROR, "Login Gagal", apiResponse.getMessage());
                    });
                }
            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    if (confirmButton != null) confirmButton.setDisable(false);
                    showAlert(Alert.AlertType.ERROR, "Error",
                        "Gagal terhubung ke server.\nPastikan backend berjalan di localhost:8080");
                });
                System.err.println("Login error: " + e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                javafx.application.Platform.runLater(() -> {
                    if (confirmButton != null) confirmButton.setDisable(false);
                    showAlert(Alert.AlertType.ERROR, "Error", "Request timeout. Coba lagi.");
                });
            }
        });
        loginThread.setDaemon(true);
        loginThread.start();
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