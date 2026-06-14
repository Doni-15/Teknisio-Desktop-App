package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.dto.ServiceRequestDto;
import com.teknisio.service.SessionManager;
import com.teknisio.service.TechnicianRequestService;
import com.teknisio.util.ImageUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TechnicianHomeController implements Initializable {

    @FXML private StackPane technicianAvatarWrapper;
    @FXML private Label txtTechnicianAvatar;
    @FXML private Label txtTechnicianGreeting;
    @FXML private Label txtTechnicianSubtitle;
    @FXML private Label txtTechnicianRequestCount;
    @FXML private VBox layoutTechnicianRequests;
    @FXML private Label txtTechnicianEmpty;

    // Shared state: which request was selected for detail page
    private static ServiceRequestDto selectedRequest = null;
    public static ServiceRequestDto getSelectedRequest() { return selectedRequest; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Header info from session
        String name = SessionManager.getName();
        if (txtTechnicianGreeting != null) {
            txtTechnicianGreeting.setText("Hai, " + (name != null ? name.split(" ")[0] : "Teknisi") + "!");
        }
        if (txtTechnicianSubtitle != null) {
            String addr = SessionManager.getAddress();
            txtTechnicianSubtitle.setText(addr != null && !addr.isBlank() ? addr : "Alamat belum diatur");
        }
        if (txtTechnicianAvatar != null && name != null && !name.isBlank()) {
            txtTechnicianAvatar.setText(String.valueOf(name.toUpperCase().charAt(0)));
        }

        // Load profile photo if available
        String photo = SessionManager.getProfilePhoto();
        if (photo != null && !photo.isBlank() && technicianAvatarWrapper != null) {
            ImageView iv = new ImageView();
            iv.setFitWidth(54);
            iv.setFitHeight(54);
            iv.setPreserveRatio(false);
            Circle clip = new Circle(27, 27, 27);
            iv.setClip(clip);
            ImageUtil.applyBase64ToImageView(iv, photo);
            technicianAvatarWrapper.getChildren().clear();
            technicianAvatarWrapper.getChildren().add(iv);
        }

        loadRequests();
    }

    private void loadRequests() {
        if (txtTechnicianRequestCount != null) txtTechnicianRequestCount.setText("Memuat request...");
        if (layoutTechnicianRequests != null) layoutTechnicianRequests.getChildren().clear();
        if (txtTechnicianEmpty != null) { txtTechnicianEmpty.setVisible(false); txtTechnicianEmpty.setManaged(false); }

        Thread t = new Thread(() -> {
            List<ServiceRequestDto> requests = TechnicianRequestService.getMyRequests(null);
            Platform.runLater(() -> {
                if (requests.isEmpty()) {
                    if (txtTechnicianRequestCount != null) txtTechnicianRequestCount.setText("Tidak ada request.");
                    if (txtTechnicianEmpty != null) {
                        txtTechnicianEmpty.setVisible(true);
                        txtTechnicianEmpty.setManaged(true);
                    }
                } else {
                    if (txtTechnicianRequestCount != null) {
                        txtTechnicianRequestCount.setText(requests.size() + " request masuk");
                    }
                    for (ServiceRequestDto req : requests) {
                        if (layoutTechnicianRequests != null) {
                            layoutTechnicianRequests.getChildren().add(buildRequestCard(req));
                        }
                    }
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private VBox buildRequestCard(ServiceRequestDto req) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16px; -fx-cursor: hand;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        // Header: status badge + code
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setSpacing(10);

        Label codeLabel = new Label(req.getServiceRequestCode() != null ? req.getServiceRequestCode() : "REQ-???");
        codeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1F2329;");
        HBox.setHgrow(codeLabel, Priority.ALWAYS);

        Label statusBadge = new Label(req.getStatusLabel());
        statusBadge.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white;"
            + "-fx-background-color: " + req.getStatusColor() + ";"
            + "-fx-background-radius: 20px; -fx-padding: 4px 12px;");
        headerRow.getChildren().addAll(codeLabel, statusBadge);

        // Customer info row
        HBox customerRow = new HBox();
        customerRow.setAlignment(Pos.CENTER_LEFT);
        customerRow.setSpacing(10);

        // Customer avatar (base64)
        ImageView avatar = new ImageView();
        avatar.setFitWidth(36);
        avatar.setFitHeight(36);
        avatar.setPreserveRatio(false);
        Circle clip = new Circle(18, 18, 18);
        avatar.setClip(clip);
        String photo = req.getCustomerProfilePhoto();
        if (photo != null && !photo.isBlank()) {
            ImageUtil.applyBase64ToImageView(avatar, photo);
        } else {
            try {
                avatar.setImage(new Image(
                    getClass().getResource("/com/teknisio/assets/profile/profile.png").toExternalForm()));
            } catch (Exception ignored) {}
        }

        VBox customerInfo = new VBox();
        customerInfo.setSpacing(2);
        Label customerName = new Label(req.getCustomerName() != null ? req.getCustomerName() : "Pelanggan");
        customerName.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2D4B73;");
        Label customerPhone = new Label(req.getCustomerPhoneNumber() != null ? req.getCustomerPhoneNumber() : "—");
        customerPhone.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7680;");
        customerInfo.getChildren().addAll(customerName, customerPhone);

        customerRow.getChildren().addAll(avatar, customerInfo);

        // Category & issue
        String categories = "-";
        if (req.getSelectedDeviceCategories() != null && !req.getSelectedDeviceCategories().isEmpty()) {
            categories = req.getSelectedDeviceCategories().stream()
                .map(c -> c.getName())
                .reduce((a, b) -> a + ", " + b)
                .orElse("-");
        }
        Label catLabel = new Label("Kategori: " + categories);
        catLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #2D4B73; -fx-font-weight: bold;");

        String issue = req.getIssueDescription();
        Label issueLabel = new Label("Masalah: " + (issue != null ? issue : "—"));
        issueLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #5F6B73;");
        issueLabel.setWrapText(true);

        // Time
        String time = req.getRequestTime();
        Label timeLabel = new Label(time != null ? "Waktu: " + time.substring(0, Math.min(16, time.length())).replace("T", " ") : "");
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #95A5A6;");

        card.getChildren().addAll(headerRow, customerRow, catLabel, issueLabel, timeLabel);

        // Add action buttons only for PENDING status
        if ("PENDING".equalsIgnoreCase(req.getStatus())) {
            HBox btnRow = new HBox();
            btnRow.setSpacing(10);
            btnRow.setAlignment(Pos.CENTER_RIGHT);

            Button rejectBtn = new Button("Tolak");
            rejectBtn.setStyle("-fx-background-color: #FF5A5A; -fx-text-fill: white;"
                + "-fx-background-radius: 10px; -fx-font-weight: bold; -fx-padding: 8 16; -fx-cursor: hand;");
            rejectBtn.setOnAction(e -> handleRejectFromCard(req));

            Button acceptBtn = new Button("Terima");
            acceptBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;"
                + "-fx-background-radius: 10px; -fx-font-weight: bold; -fx-padding: 8 16; -fx-cursor: hand;");
            acceptBtn.setOnAction(e -> handleAcceptFromCard(req));

            btnRow.getChildren().addAll(rejectBtn, acceptBtn);
            card.getChildren().add(btnRow);
        }

        // Click card to open detail
        card.setOnMouseClicked(event -> openDetail(req));

        return card;
    }

    private void handleAcceptFromCard(ServiceRequestDto req) {
        Thread t = new Thread(() -> {
            try {
                TechnicianRequestService.acceptRequest(req.getServiceRequestId());
                Platform.runLater(this::loadRequests);
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> showAlert("Gagal menerima request: " + e.getMessage()));
                Thread.currentThread().interrupt();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void handleRejectFromCard(ServiceRequestDto req) {
        Thread t = new Thread(() -> {
            try {
                TechnicianRequestService.rejectRequest(req.getServiceRequestId(), null);
                Platform.runLater(this::loadRequests);
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> showAlert("Gagal menolak request: " + e.getMessage()));
                Thread.currentThread().interrupt();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void openDetail(ServiceRequestDto req) {
        selectedRequest = req;
        TechnicianRequestDetailController.setCurrentRequest(req);
        try { Main.setRoot("/com/teknisio/fxml/TechnicianRequestDetail.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    private void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // -- Navigation ---
    @FXML private void handleHistoryTab(MouseEvent event) {
        try { Main.setRoot("/com/teknisio/fxml/TechnicianHistory.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleSkillTab(MouseEvent event) {
        try { Main.setRoot("/com/teknisio/fxml/TechnicianSkill.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleAccountTab(MouseEvent event) {
        try { Main.setRoot("/com/teknisio/fxml/UserProfile.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleNotification(javafx.event.ActionEvent event) {
        showAlert("Fitur notifikasi segera hadir.");
    }
}