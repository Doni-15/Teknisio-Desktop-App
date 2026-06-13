package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.dto.AuthResponse;
import com.teknisio.dto.RegisterRequest;
import com.teknisio.service.ApiClient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private Button confirmButton;
    @FXML private Label loginLink;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;

    // Role tabs
    @FXML private Label txtTabCustomer;
    @FXML private Label txtTabTechnician;
    @FXML private VBox layoutDescription;
    @FXML private TextArea edtDescription;

    private boolean isTechnicianTab = false;

    @FXML
    private void handleRegister(ActionEvent event) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();
        String password = passwordField.getText();
        String description = isTechnicianTab ? (edtDescription != null ? edtDescription.getText().trim() : "") : "";

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Harap isi semua field yang wajib.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showAlert(Alert.AlertType.ERROR, "Validasi", "Format email tidak valid.");
            return;
        }

        // Call backend API
        try {
            String path;
            Object payload;

            if (isTechnicianTab) {
                payload = new RegisterRequest.TechnicianPayload(name, email, phone, password, address, description);
                path = "/api/auth/register/technician";
            } else {
                payload = new RegisterRequest.CustomerPayload(name, email, phone, password, address);
                path = "/api/auth/register/customer";
            }

            ApiClient.ApiResponse<AuthResponse> apiResponse = ApiClient.post(path, payload, AuthResponse.class);

            if (apiResponse.isSuccess()) {
                String roleLabel = isTechnicianTab ? "Teknisi" : "Pelanggan";
                showAlert(Alert.AlertType.INFORMATION, "Sukses",
                    "Registrasi " + roleLabel + " berhasil!\nSilakan login.");
                Main.setRoot("/com/teknisio/fxml/login.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", apiResponse.getMessage());
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error Koneksi",
                "Gagal terhubung ke server.\nPastikan backend berjalan di localhost:8080");
            System.err.println("Register error: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Request timeout. Coba lagi.");
            Thread.currentThread().interrupt();
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

    @FXML
    private void switchToCustomer(MouseEvent event) {
        setActiveTab(false);
    }

    @FXML
    private void switchToTechnician(MouseEvent event) {
        setActiveTab(true);
    }

    private void setActiveTab(boolean technician) {
        isTechnicianTab = technician;
        if (technician) {
            // Technician tab active
            txtTabCustomer.setStyle("-fx-background-color: #D3DFF5; -fx-background-radius: 8px; -fx-text-fill: #2F4A8A; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8 0; -fx-cursor: hand;");
            txtTabTechnician.setStyle("-fx-background-color: #4F70D9; -fx-background-radius: 8px; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8 0; -fx-cursor: hand;");
            if (layoutDescription != null) layoutDescription.setVisible(true);
            if (layoutDescription != null) layoutDescription.setManaged(true);
            if (confirmButton != null) confirmButton.setText("Daftar Teknisi");
        } else {
            // Customer tab active
            txtTabCustomer.setStyle("-fx-background-color: #4F70D9; -fx-background-radius: 8px; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8 0; -fx-cursor: hand;");
            txtTabTechnician.setStyle("-fx-background-color: #D3DFF5; -fx-background-radius: 8px; -fx-text-fill: #2F4A8A; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8 0; -fx-cursor: hand;");
            if (layoutDescription != null) layoutDescription.setVisible(false);
            if (layoutDescription != null) layoutDescription.setManaged(false);
            if (confirmButton != null) confirmButton.setText("Daftar");
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