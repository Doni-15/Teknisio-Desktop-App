package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.dto.ServiceRequestDto;
import com.teknisio.service.ServiceRequestService;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrderHistoryController implements Initializable {

    @FXML private Label txtHistorySubtitle;
    @FXML private HBox layoutStatusFilters;
    @FXML private VBox layoutOrders;
    @FXML private Label txtOrderEmpty;
    @FXML private Button btnRefreshOrders;

    private String selectedStatusFilter = null; // null means ALL
    private static ServiceRequestDto selectedOrder = null;
    public static ServiceRequestDto getSelectedOrder() { return selectedOrder; }
    public static void setSelectedOrder(ServiceRequestDto order) { selectedOrder = order; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFilterChips();
        loadOrders();
    }

    private void setupFilterChips() {
        if (layoutStatusFilters == null) return;
        layoutStatusFilters.getChildren().clear();

        String[][] filters = {
            {null, "Semua"},
            {"WAITING", "Menunggu"},
            {"ACCEPTED", "Diterima"},
            {"ON_PROGRESS", "Dikerjakan"},
            {"COMPLETED", "Selesai"},
            {"CANCELLED", "Batal"},
            {"REJECTED", "Ditolak"}
        };

        for (String[] f : filters) {
            Button chip = new Button(f[1]);
            chip.setUserData(f[0]);
            chip.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
            updateChipStyle(chip, false);

            chip.setOnAction(e -> {
                selectedStatusFilter = (String) chip.getUserData();
                for (javafx.scene.Node n : layoutStatusFilters.getChildren()) {
                    if (n instanceof Button) {
                        updateChipStyle((Button) n, n == chip);
                    }
                }
                loadOrders();
            });

            if ((selectedStatusFilter == null && f[0] == null) || 
                (selectedStatusFilter != null && selectedStatusFilter.equals(f[0]))) {
                updateChipStyle(chip, true);
            }

            layoutStatusFilters.getChildren().add(chip);
        }
    }

    private void updateChipStyle(Button chip, boolean isActive) {
        if (isActive) {
            chip.setStyle("-fx-background-color: #2D4B73; -fx-text-fill: white; "
                + "-fx-background-radius: 20px; -fx-padding: 6px 14px; -fx-font-weight: bold; -fx-cursor: hand;");
        } else {
            chip.setStyle("-fx-background-color: white; -fx-text-fill: #5F6B73; "
                + "-fx-background-radius: 20px; -fx-padding: 6px 14px; -fx-border-color: #E2ECF7; "
                + "-fx-border-radius: 20px; -fx-border-width: 1px; -fx-cursor: hand;");
        }
    }

    private void loadOrders() {
        if (txtOrderEmpty != null) {
            txtOrderEmpty.setText("Memuat order...");
            txtOrderEmpty.setVisible(true);
        }
        if (layoutOrders != null) {
            layoutOrders.getChildren().clear();
        }

        Thread t = new Thread(() -> {
            List<ServiceRequestDto> list = ServiceRequestService.getMyServiceRequests(selectedStatusFilter);
            Platform.runLater(() -> {
                if (list == null || list.isEmpty()) {
                    if (txtOrderEmpty != null) {
                        txtOrderEmpty.setText("Belum ada order dengan status ini.");
                        txtOrderEmpty.setVisible(true);
                    }
                } else {
                    if (txtOrderEmpty != null) {
                        txtOrderEmpty.setVisible(false);
                    }
                    for (ServiceRequestDto req : list) {
                        if (layoutOrders != null) {
                            layoutOrders.getChildren().add(buildOrderCard(req));
                        }
                    }
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private VBox buildOrderCard(ServiceRequestDto req) {
        VBox card = new VBox();
        card.setSpacing(10);
        card.setPadding(new Insets(14));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16px; -fx-cursor: hand;"
            + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        // Header: code + status badge
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

        // Technician details
        HBox techRow = new HBox();
        techRow.setAlignment(Pos.CENTER_LEFT);
        techRow.setSpacing(10);

        ImageView avatar = new ImageView();
        avatar.setFitWidth(36);
        avatar.setFitHeight(36);
        avatar.setPreserveRatio(false);
        Circle clip = new Circle(18, 18, 18);
        avatar.setClip(clip);
        try {
            avatar.setImage(new Image(
                getClass().getResource("/com/teknisio/assets/profile/profile.png").toExternalForm()));
        } catch (Exception ignored) {}

        VBox techInfo = new VBox();
        techInfo.setSpacing(2);
        Label techName = new Label("Memuat...");
        techName.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2D4B73;");

        String categories = "-";
        if (req.getSelectedDeviceCategories() != null && !req.getSelectedDeviceCategories().isEmpty()) {
            categories = req.getSelectedDeviceCategories().stream()
                .map(c -> c.getName())
                .reduce((a, b) -> a + ", " + b)
                .orElse("-");
        }
        Label techSpec = new Label("Spesialis: " + categories);
        techSpec.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7680;");
        techInfo.getChildren().addAll(techName, techSpec);

        techRow.getChildren().addAll(avatar, techInfo);

        String techId = req.getTechnicianProfileId();
        if (techId != null) {
            Thread t = new Thread(() -> {
                com.teknisio.dto.TechnicianDto tech = com.teknisio.service.TechnicianService.getTechnicianDetail(techId);
                if (tech != null) {
                    Platform.runLater(() -> {
                        techName.setText(tech.getName());
                        String photo = tech.getProfilePhoto();
                        if (photo != null && !photo.isBlank()) {
                            ImageUtil.applyBase64ToImageView(avatar, photo);
                        }
                    });
                } else {
                    Platform.runLater(() -> techName.setText("Teknisi"));
                }
            });
            t.setDaemon(true);
            t.start();
        } else {
            techName.setText("Mencari Teknisi");
        }

        // Time
        String time = req.getRequestTime();
        Label timeLabel = new Label("Jadwal: " + (time != null ? time.substring(0, Math.min(16, time.length())).replace("T", " ") : "—"));
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #5F6B73;");

        // Short description
        String desc = req.getIssueDescription();
        if (desc != null && desc.length() > 50) desc = desc.substring(0, 47) + "...";
        Label descLabel = new Label("Kerusakan: " + (desc != null ? desc : "—"));
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95A5A6;");
        descLabel.setWrapText(true);

        card.getChildren().addAll(headerRow, techRow, timeLabel, descLabel);

        // Click handler to open detail page
        card.setOnMouseClicked(e -> openDetail(req));

        return card;
    }

    private void openDetail(ServiceRequestDto req) {
        selectedOrder = req;
        ServiceRequestDetailController.setSelectedOrder(req);
        try {
            Main.setRoot("/com/teknisio/fxml/ServiceRequestDetail.fxml");
        } catch (IOException e) {
            System.err.println("Failed to open detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            if (com.teknisio.service.SessionManager.isTechnician()) {
                Main.setRoot("/com/teknisio/fxml/TechnicianHome.fxml");
            } else {
                Main.setRoot("/com/teknisio/fxml/home_user.fxml");
            }
        } catch (IOException e) {
            System.err.println("Failed to navigate back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadOrders();
    }

    @FXML
    private void handleHomeTab(MouseEvent event) {
        try {
            if (com.teknisio.service.SessionManager.isTechnician()) {
                Main.setRoot("/com/teknisio/fxml/TechnicianHome.fxml");
            } else {
                Main.setRoot("/com/teknisio/fxml/home_user.fxml");
            }
        } catch (IOException e) {
            System.err.println("Failed to navigate to Home: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChatTab(MouseEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/Chat.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to Chat: " + e.getMessage());
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