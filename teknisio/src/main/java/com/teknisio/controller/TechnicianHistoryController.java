package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.dto.ServiceRequestDto;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TechnicianHistoryController implements Initializable {

    @FXML private Label txtTechnicianHistorySubtitle;
    @FXML private HBox layoutTechnicianHistoryFilters;
    @FXML private VBox layoutTechnicianHistoryRequests;
    @FXML private Label txtTechnicianHistoryEmpty;
    @FXML private Button btnRefreshTechnicianHistory;

    private String selectedStatusFilter = null; // null means ALL

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFilterChips();
        loadHistory();
    }

    private void setupFilterChips() {
        if (layoutTechnicianHistoryFilters == null) return;
        layoutTechnicianHistoryFilters.getChildren().clear();

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
                for (javafx.scene.Node n : layoutTechnicianHistoryFilters.getChildren()) {
                    if (n instanceof Button) {
                        updateChipStyle((Button) n, n == chip);
                    }
                }
                loadHistory();
            });

            if ((selectedStatusFilter == null && f[0] == null) || 
                (selectedStatusFilter != null && selectedStatusFilter.equals(f[0]))) {
                updateChipStyle(chip, true);
            }

            layoutTechnicianHistoryFilters.getChildren().add(chip);
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

    private void loadHistory() {
        if (txtTechnicianHistoryEmpty != null) {
            txtTechnicianHistoryEmpty.setText("Memuat riwayat...");
            txtTechnicianHistoryEmpty.setVisible(true);
        }
        if (layoutTechnicianHistoryRequests != null) {
            layoutTechnicianHistoryRequests.getChildren().clear();
        }

        Thread t = new Thread(() -> {
            List<ServiceRequestDto> list = TechnicianRequestService.getMyRequests(selectedStatusFilter);
            Platform.runLater(() -> {
                if (list == null || list.isEmpty()) {
                    if (txtTechnicianHistoryEmpty != null) {
                        txtTechnicianHistoryEmpty.setText("Belum ada request dengan status ini.");
                        txtTechnicianHistoryEmpty.setVisible(true);
                    }
                } else {
                    if (txtTechnicianHistoryEmpty != null) {
                        txtTechnicianHistoryEmpty.setVisible(false);
                    }
                    for (ServiceRequestDto req : list) {
                        if (layoutTechnicianHistoryRequests != null) {
                            layoutTechnicianHistoryRequests.getChildren().add(buildRequestCard(req));
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

        // Customer details
        HBox customerRow = new HBox();
        customerRow.setAlignment(Pos.CENTER_LEFT);
        customerRow.setSpacing(10);

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

        VBox custInfo = new VBox();
        custInfo.setSpacing(2);
        Label custName = new Label(req.getCustomerName() != null ? req.getCustomerName() : "Pelanggan");
        custName.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2D4B73;");

        Label custPhone = new Label("HP: " + (req.getCustomerPhoneNumber() != null ? req.getCustomerPhoneNumber() : "—"));
        custPhone.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7680;");
        custInfo.getChildren().addAll(custName, custPhone);

        customerRow.getChildren().addAll(avatar, custInfo);

        // Address & issue details
        String desc = req.getIssueDescription();
        if (desc != null && desc.length() > 50) desc = desc.substring(0, 47) + "...";
        Label descLabel = new Label("Kerusakan: " + (desc != null ? desc : "—"));
        descLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #5F6B73;");
        descLabel.setWrapText(true);

        Label addrLabel = new Label("Alamat: " + (req.getAddress() != null ? req.getAddress() : "—"));
        addrLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #95A5A6;");
        addrLabel.setWrapText(true);

        card.getChildren().addAll(headerRow, customerRow, descLabel, addrLabel);

        // Click handler to open detail page
        card.setOnMouseClicked(e -> {
            TechnicianRequestDetailController.setCurrentRequest(req);
            try {
                Main.setRoot("/com/teknisio/fxml/TechnicianRequestDetail.fxml");
            } catch (IOException ex) {
                System.err.println("Failed to open detail: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        return card;
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
        loadHistory();
    }
}