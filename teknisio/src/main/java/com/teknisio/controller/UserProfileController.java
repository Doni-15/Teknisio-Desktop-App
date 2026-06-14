package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.service.SessionManager;
import com.teknisio.service.UserService;
import com.teknisio.util.ImageUtil;

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
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {

    @FXML private StackPane profileImageWrapper;
    @FXML private ImageView profileImageView;
    @FXML private Label profileNameLabel;
    @FXML private Label rowNameVal;
    @FXML private Label rowEmailVal;
    @FXML private Label rowPhoneVal;
    @FXML private Label rowAddressVal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Circular crop
        Circle clip = new Circle(46, 46, 46);
        profileImageView.setClip(clip);

        // Fill all fields from SessionManager (real data after login)
        loadProfileFromSession();
    }

    private void loadProfileFromSession() {
        String name  = SessionManager.getName();
        String email = SessionManager.getEmail();
        String phone = SessionManager.getPhone();
        String address = SessionManager.getAddress();
        String photo = SessionManager.getProfilePhoto();

        if (profileNameLabel != null)
            profileNameLabel.setText(name != null ? name : "—");
        if (rowNameVal != null)
            rowNameVal.setText(name != null ? name : "—");
        if (rowEmailVal != null)
            rowEmailVal.setText(email != null ? email : "—");
        if (rowPhoneVal != null)
            rowPhoneVal.setText(phone != null ? phone : "—");
        if (rowAddressVal != null)
            rowAddressVal.setText(address != null ? address : "—");

        // Load profile photo from base64
        if (photo != null && !photo.isBlank()) {
            ImageUtil.applyBase64ToImageView(profileImageView, photo);
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        navigateHome();
    }

    @FXML
    private void handleEditName(ActionEvent event) {
        String current = rowNameVal != null ? rowNameVal.getText() : "";
        TextInputDialog dialog = new TextInputDialog("—".equals(current) ? "" : current);
        dialog.setTitle("Edit Nama");
        dialog.setHeaderText("Ubah nama profil kamu:");
        dialog.setContentText("Nama:");
        applyStyle(dialog);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            if (!newName.trim().isEmpty()) {
                // Update via API in background thread
                Thread t = new Thread(() -> {
                    boolean ok = UserService.updateProfile(Map.of("name", newName.trim()));
                    javafx.application.Platform.runLater(() -> {
                        if (ok) {
                            loadProfileFromSession();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memperbarui nama.");
                        }
                    });
                });
                t.setDaemon(true);
                t.start();
            }
        });
    }

    @FXML
    private void handleEditPhone(ActionEvent event) {
        String current = rowPhoneVal != null ? rowPhoneVal.getText() : "";
        TextInputDialog dialog = new TextInputDialog("—".equals(current) ? "" : current);
        dialog.setTitle("Edit No. Telepon");
        dialog.setHeaderText("Ubah nomor telepon kamu:");
        dialog.setContentText("No. Telepon:");
        applyStyle(dialog);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPhone -> {
            if (!newPhone.trim().isEmpty()) {
                Thread t = new Thread(() -> {
                    boolean ok = UserService.updateProfile(Map.of("phoneNumber", newPhone.trim()));
                    javafx.application.Platform.runLater(() -> {
                        if (ok) {
                            loadProfileFromSession();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memperbarui no. telepon.");
                        }
                    });
                });
                t.setDaemon(true);
                t.start();
            }
        });
    }

    @FXML
    private void handleSelectLocation(MouseEvent event) {
        LocationController.setBackRoute("/com/teknisio/fxml/UserProfile.fxml");
        try {
            Main.setRoot("/com/teknisio/fxml/Location.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to Location: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLanguageClick(MouseEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Pengaturan", "Pengaturan bahasa akan segera tersedia.");
    }

    @FXML
    private void handleTermsClick(MouseEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Syarat & Ketentuan", "Dokumen sedang dimuat...");
    }

    @FXML
    private void handlePrivacyClick(MouseEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Kebijakan Privasi", "Dokumen sedang dimuat...");
    }

    @FXML
    private void handleCallCenterClick(MouseEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Call Center", "Menghubungkan ke Call Center Teknisio...\nTelp: 0800-TEKNISIO");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();
        showAlert(Alert.AlertType.INFORMATION, "Logout", "Kamu telah berhasil keluar dari Teknisio.");
        try {
            Main.setRoot("/com/teknisio/fxml/walkthrough.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to walkthrough: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateHome() {
        try {
            if (SessionManager.isTechnician()) {
                Main.setRoot("/com/teknisio/fxml/TechnicianHome.fxml");
            } else {
                Main.setRoot("/com/teknisio/fxml/home_user.fxml");
            }
        } catch (IOException e) {
            System.err.println("Failed to navigate: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyStyle(javafx.scene.control.Dialog<?> dialog) {
        try {
            dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
            dialog.getDialogPane().getStyleClass().add("alert-dialog");
        } catch (Exception ignored) {}
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        applyStyle(alert);
        alert.showAndWait();
    }
}
