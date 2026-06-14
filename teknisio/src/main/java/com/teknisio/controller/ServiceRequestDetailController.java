package com.teknisio.controller;

import com.google.gson.reflect.TypeToken;
import com.teknisio.Main;
import com.teknisio.dto.ServiceRequestDto;
import com.teknisio.dto.TechnicianDto;
import com.teknisio.service.ApiClient;
import com.teknisio.service.ServiceRequestService;
import com.teknisio.service.TechnicianService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class ServiceRequestDetailController implements Initializable {

    @FXML private Label txtOrderCode;
    @FXML private StackPane statusBadgeContainer;
    @FXML private Label txtOrderStatus;
    @FXML private Label txtOrderTime;

    @FXML private VBox layoutOrderTechnicianSummary;
    @FXML private Label txtOrderTechnicianName;
    @FXML private Label txtOrderTechnicianMeta;
    @FXML private Label txtOrderTechnicianCategories;

    @FXML private Label txtOrderCategories;
    @FXML private Label txtOrderIssue;
    @FXML private Label txtOrderAddress;
    @FXML private Label txtCancelReason;

    @FXML private VBox layoutCompletionSummary;
    @FXML private Label txtFinalCostValue;
    @FXML private Label txtCompletionSummaryNoteLabel;
    @FXML private Label txtCompletionSummaryNote;

    @FXML private Label txtStatusHistoryLabel;
    @FXML private VBox layoutStatusHistory;
    @FXML private Label txtDetailMessage;

    @FXML private Button btnCancelOrder;
    @FXML private Button btnWriteReview;
    @FXML private Button btnTrackTechnician;
    @FXML private Button btnOpenChat;

    private static ServiceRequestDto selectedOrder = null;
    private ServiceRequestDto currentOrder = null;
    private com.teknisio.dto.TechnicianDto currentTechnician = null;

    public static void setSelectedOrder(ServiceRequestDto order) {
        selectedOrder = order;
    }

    public static class StatusHistoryDto {
        private String statusHistoryId;
        private String previousStatus;
        private String newStatus;
        private String note;
        private String changedByUserId;
        private String changedAt;

        public String getStatusSebelum() { return previousStatus; }
        public String getStatusSesudah() { return newStatus; }
        public String getCatatan() { return note; }
        public String getCreatedAt() { return changedAt; }
    }

    private static final Type HISTORY_LIST_TYPE = new TypeToken<List<StatusHistoryDto>>() {}.getType();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (selectedOrder != null) {
            refreshOrderDetail(selectedOrder.getServiceRequestId());
        }
    }

    private void refreshOrderDetail(String id) {
        Thread t = new Thread(() -> {
            ServiceRequestDto detail = ServiceRequestService.getServiceRequestDetail(id);
            if (detail != null) {
                currentOrder = detail;
                
                // Fetch technician info
                TechnicianDto tech = null;
                if (detail.getTechnicianProfileId() != null) {
                    tech = TechnicianService.getTechnicianDetail(detail.getTechnicianProfileId());
                }
                
                // Fetch status history
                List<StatusHistoryDto> history = null;
                try {
                    ApiClient.ApiResponse<List<StatusHistoryDto>> res = ApiClient.get(
                        "/api/customers/service-requests/" + id + "/status-history",
                        HISTORY_LIST_TYPE
                    );
                    if (res.isSuccess()) {
                        history = res.getData();
                    }
                } catch (Exception e) {
                    System.err.println("Failed to fetch history: " + e.getMessage());
                }

                final TechnicianDto finalTech = tech;
                final List<StatusHistoryDto> finalHistory = history;
                
                Platform.runLater(() -> {
                    bindData(detail, finalTech, finalHistory);
                });
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void bindData(ServiceRequestDto req, com.teknisio.dto.TechnicianDto tech, List<StatusHistoryDto> history) {
        this.currentTechnician = tech;
        txtOrderCode.setText(req.getServiceRequestCode() != null ? req.getServiceRequestCode() : "REQ-???");
        txtOrderStatus.setText(req.getStatusLabel());
        txtOrderStatus.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 5 12;");
        statusBadgeContainer.setStyle("-fx-background-color: " + req.getStatusColor() + "; -fx-background-radius: 20px;");

        String time = req.getRequestTime();
        txtOrderTime.setText("Waktu Request: " + (time != null ? time.substring(0, Math.min(16, time.length())).replace("T", " ") : "—"));

        // Categories
        String categories = "—";
        if (req.getSelectedDeviceCategories() != null && !req.getSelectedDeviceCategories().isEmpty()) {
            categories = req.getSelectedDeviceCategories().stream()
                .map(c -> c.getName())
                .reduce((a, b) -> a + ", " + b)
                .orElse("—");
        }
        txtOrderCategories.setText(categories);
        txtOrderIssue.setText(req.getIssueDescription() != null ? req.getIssueDescription() : "—");
        
        String addressText = req.getAddress();
        if (req.getAddressDetail() != null && !req.getAddressDetail().isBlank()) {
            addressText += "\nDetail: " + req.getAddressDetail();
        }
        txtOrderAddress.setText(addressText != null ? addressText : "—");

        // Technician card
        if (tech != null) {
            layoutOrderTechnicianSummary.setVisible(true);
            txtOrderTechnicianName.setText(tech.getName());
            txtOrderTechnicianMeta.setText("No. HP: " + (tech.getPhoneNumber() != null ? tech.getPhoneNumber() : "—"));
            
            String techCats = "Spesialisasi: —";
            if (tech.getSupportedDeviceCategories() != null && !tech.getSupportedDeviceCategories().isEmpty()) {
                techCats = "Spesialisasi: " + tech.getSupportedDeviceCategories().stream()
                    .map(c -> c.getName())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("—");
            }
            txtOrderTechnicianCategories.setText(techCats);
        } else {
            layoutOrderTechnicianSummary.setVisible(false);
        }

        // Cancel reason
        if ("CANCELLED".equals(req.getStatus()) && req.getCancelReason() != null && !req.getCancelReason().isBlank()) {
            txtCancelReason.setText("Alasan Batal: " + req.getCancelReason());
            txtCancelReason.setVisible(true);
        } else if ("REJECTED".equals(req.getStatus()) && req.getRejectReason() != null && !req.getRejectReason().isBlank()) {
            txtCancelReason.setText("Alasan Ditolak: " + req.getRejectReason());
            txtCancelReason.setVisible(true);
        } else {
            txtCancelReason.setVisible(false);
            txtCancelReason.setText("");
        }

        // Completion
        if ("COMPLETED".equals(req.getStatus())) {
            layoutCompletionSummary.setVisible(true);
            
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            String cost = format.format(req.getFinalCost() != null ? req.getFinalCost() : (req.getEstimatedCost() != null ? req.getEstimatedCost() : 0));
            txtFinalCostValue.setText(cost);

            if (req.getTechnicianNote() != null && !req.getTechnicianNote().isBlank()) {
                txtCompletionSummaryNoteLabel.setVisible(true);
                txtCompletionSummaryNote.setText(req.getTechnicianNote());
                txtCompletionSummaryNote.setVisible(true);
            } else {
                txtCompletionSummaryNoteLabel.setVisible(false);
                txtCompletionSummaryNote.setVisible(false);
            }
        } else {
            layoutCompletionSummary.setVisible(false);
        }

        // History
        if (history != null && !history.isEmpty()) {
            txtStatusHistoryLabel.setVisible(true);
            layoutStatusHistory.setVisible(true);
            layoutStatusHistory.getChildren().clear();

            for (StatusHistoryDto h : history) {
                VBox row = new VBox(2);
                String before = h.getStatusSebelum() != null ? h.getStatusSebelum() : "START";
                String after = h.getStatusSesudah() != null ? h.getStatusSesudah() : "UNKNOWN";
                
                Label title = new Label("Perubahan: " + before + " → " + after);
                title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2D4B73;");

                String timeStr = h.getCreatedAt();
                Label dateLabel = new Label(timeStr != null ? timeStr.substring(0, Math.min(16, timeStr.length())).replace("T", " ") : "");
                dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #95A5A6;");

                row.getChildren().addAll(title, dateLabel);

                if (h.getCatatan() != null && !h.getCatatan().isBlank()) {
                    Label note = new Label("Catatan: " + h.getCatatan());
                    note.setStyle("-fx-font-size: 11px; -fx-text-fill: #5F6B73;");
                    row.getChildren().add(note);
                }

                layoutStatusHistory.getChildren().add(row);
                // add simple separator if not last
                if (history.indexOf(h) < history.size() - 1) {
                    Separator sep = new Separator();
                    sep.setStyle("-fx-opacity: 0.4;");
                    layoutStatusHistory.getChildren().add(sep);
                }
            }
        } else {
            txtStatusHistoryLabel.setVisible(false);
            layoutStatusHistory.setVisible(false);
        }

        // Button visibility logic
        String status = req.getStatus();
        btnCancelOrder.setVisible("PENDING".equals(status) || "ACCEPTED".equals(status));
        btnWriteReview.setVisible("COMPLETED".equals(status));
        btnTrackTechnician.setVisible("IN_PROGRESS".equals(status) || "ACCEPTED".equals(status));

        // Chat button: only visible when technician has accepted (ACCEPTED or IN_PROGRESS).
        // If still PENDING/WAITING, the technician hasn't accepted yet so chat is not available.
        boolean chatAllowed = tech != null
                && ("ACCEPTED".equals(status) || "IN_PROGRESS".equals(status));
        btnOpenChat.setVisible(chatAllowed);
        btnOpenChat.setManaged(chatAllowed);
    }

    @FXML
    private void handleBack() {
        try {
            Main.setRoot("/com/teknisio/fxml/OrderHistory.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelOrder() {
        if (currentOrder == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Batalkan Pesanan");
        dialog.setHeaderText("Masukkan alasan pembatalan:");
        dialog.setContentText("Alasan:");

        try {
            dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
            dialog.getDialogPane().getStyleClass().add("alert-dialog");
        } catch (Exception ignored) {}

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            Thread t = new Thread(() -> {
                try {
                    ServiceRequestService.cancelServiceRequest(currentOrder.getServiceRequestId(), result.get().trim());
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Pesanan berhasil dibatalkan!");
                        alert.showAndWait();
                        refreshOrderDetail(currentOrder.getServiceRequestId());
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal membatalkan pesanan: " + e.getMessage());
                        alert.showAndWait();
                    });
                }
            });
            t.setDaemon(true);
            t.start();
        }
    }

    @FXML
    private void handleWriteReview() {
        if (currentOrder == null) return;

        // Custom premium review dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ulas Teknisi");
        dialog.setHeaderText("Beri penilaian pelayanan untuk teknisi:");

        try {
            dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
            dialog.getDialogPane().getStyleClass().add("alert-dialog");
        } catch (Exception ignored) {}

        ButtonType submitButtonType = new ButtonType("Kirim", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Integer> ratingBox = new ComboBox<>();
        ratingBox.getItems().addAll(5, 4, 3, 2, 1);
        ratingBox.setValue(5);

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Tulis komentar anda...");
        commentArea.setPrefRowCount(3);
        commentArea.setPrefColumnCount(20);

        grid.add(new Label("Rating:"), 0, 0);
        grid.add(ratingBox, 1, 0);
        grid.add(new Label("Komentar:"), 0, 1);
        grid.add(commentArea, 1, 1);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == submitButtonType) {
            int rating = ratingBox.getValue();
            String comment = commentArea.getText().trim();

            Thread t = new Thread(() -> {
                try {
                    boolean ok = ServiceRequestService.createReview(currentOrder.getServiceRequestId(), rating, comment);
                    Platform.runLater(() -> {
                        if (ok) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Terima kasih atas ulasan anda!");
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal mengirim ulasan.");
                            alert.showAndWait();
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal mengirim ulasan: " + e.getMessage());
                        alert.showAndWait();
                    });
                }
            });
            t.setDaemon(true);
            t.start();
        }
    }

    @FXML
    private void handleTrackTechnician() {
        if (currentOrder == null) return;
        try {
            // Check if technician's real-time coordinates are set
            if (currentOrder.getLatitude() == null || currentOrder.getLongitude() == null ||
                (Math.abs(currentOrder.getLatitude()) < 0.0001 && Math.abs(currentOrder.getLongitude()) < 0.0001)) {
                
                showNoLocationAlert();
                return;
            }

            // Try to check if coordinates are still just the customer's coordinates (not updated yet)
            Double custLat = com.teknisio.service.SessionManager.getLatitude();
            Double custLon = com.teknisio.service.SessionManager.getLongitude();
            if (custLat == null || custLon == null) {
                com.teknisio.util.GeoLocationUtil.LocationResult loc = com.teknisio.util.GeoLocationUtil.geocodeAddress(currentOrder.getAddress());
                if (loc != null) {
                    custLat = loc.lat;
                    custLon = loc.lon;
                }
            }

            if (custLat != null && custLon != null) {
                if (Math.abs(currentOrder.getLatitude() - custLat) < 0.001 && Math.abs(currentOrder.getLongitude() - custLon) < 0.001) {
                    showNoLocationAlert();
                    return;
                }
            }
            
            String url = "https://www.google.com/maps/dir/?api=1&destination=" + currentOrder.getLatitude() + "," + currentOrder.getLongitude();
            Main.getInstance().getHostServices().showDocument(url);
        } catch (Exception e) {
            System.err.println("Failed to open Google Maps: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showNoLocationAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Lokasi Tidak Tersedia");
        alert.setHeaderText(null);
        alert.setContentText("Lokasi aktif teknisi belum tersedia saat ini. Mohon tunggu hingga teknisi memulai perjalanan.");
        try {
            alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("alert-dialog");
        } catch (Exception ignored) {}
        alert.showAndWait();
    }

    @FXML
    private void handleOpenChat() {
        if (currentOrder == null) return;
        try {
            String techName = (currentTechnician != null && currentTechnician.getName() != null) ? currentTechnician.getName() : "Teknisi";
            String techPhoto = currentTechnician != null ? currentTechnician.getProfilePhoto() : null;
            String techStatus = "Online";
            String serviceRequestId = currentOrder.getServiceRequestId();

            // Add to active conversations in ChatController if not already present
            boolean exists = ChatController.SESSION_ACTIVE_CONTACTS.stream()
                .anyMatch(c -> c.getName().equals(techName));
            if (!exists) {
                ChatController.ChatContact active = new ChatController.ChatContact(
                    techName, "", "", techPhoto,
                    techStatus, 0, true, null, serviceRequestId);
                ChatController.SESSION_ACTIVE_CONTACTS.add(0, active);
            } else {
                // Update serviceRequestId on the existing contact if missing
                ChatController.SESSION_ACTIVE_CONTACTS.stream()
                    .filter(c -> c.getName().equals(techName))
                    .findFirst()
                    .ifPresent(c -> c.setServiceRequestId(serviceRequestId));
            }

            // Load ChatDetail FXML and set data
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/teknisio/fxml/ChatDetail.fxml"));
            javafx.scene.Parent root = loader.load();
            ChatDetailController detailController = loader.getController();
            detailController.setContactData(techName, techPhoto, techStatus);
            detailController.setServiceRequestId(serviceRequestId);

            btnOpenChat.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Failed to open chat: " + e.getMessage());
            e.printStackTrace();
        }
    }
}