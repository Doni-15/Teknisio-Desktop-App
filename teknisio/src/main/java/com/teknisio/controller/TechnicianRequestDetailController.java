package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.dto.ServiceRequestDto;
import com.teknisio.service.TechnicianRequestService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class TechnicianRequestDetailController implements Initializable {

    // Set by TechnicianHomeController before navigating here
    private static ServiceRequestDto currentRequest = null;
    public static void setCurrentRequest(ServiceRequestDto req) { currentRequest = req; }

    @FXML private Label txtTechOrderCode;
    @FXML private StackPane techStatusBadgeContainer;
    @FXML private Label txtTechOrderStatus;
    @FXML private Label txtTechOrderTime;
    @FXML private Label txtTechCustomer;
    @FXML private Label txtTechCategories;
    @FXML private Label txtTechIssue;
    @FXML private Label txtTechAddress;
    @FXML private Label txtTechCost;
    @FXML private Label txtTechNote;
    @FXML private Label txtStatusHistoryLabel;
    @FXML private VBox layoutStatusHistory;
    @FXML private VBox layoutTechnicianActionPanel;
    @FXML private Label txtTechDetailMessage;
    @FXML private Button btnAcceptRequest;
    @FXML private Button btnStartRequest;
    @FXML private Button btnNavigateToCustomer;
    @FXML private Button btnTechnicianChat;
    @FXML private Button btnCompleteRequest;
    @FXML private Button btnRejectRequest;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (currentRequest != null) {
            populateDetail(currentRequest);
        } else {
            if (txtTechOrderCode != null) txtTechOrderCode.setText("REQ-???");
        }
    }

    private void populateDetail(ServiceRequestDto req) {
        // Basic info
        if (txtTechOrderCode != null)
            txtTechOrderCode.setText(req.getServiceRequestCode() != null ? req.getServiceRequestCode() : "REQ-???");

        if (txtTechOrderStatus != null) {
            txtTechOrderStatus.setText(req.getStatusLabel());
            if (techStatusBadgeContainer != null) {
                techStatusBadgeContainer.setStyle(
                    "-fx-background-color: " + req.getStatusColor() + ";"
                    + "-fx-background-radius: 12px; -fx-padding: 4px 12px;");
            }
        }

        if (txtTechOrderTime != null) {
            String t = req.getRequestTime();
            txtTechOrderTime.setText("Request pada: " + (t != null
                ? t.substring(0, Math.min(16, t.length())).replace("T", " ") : "—"));
        }

        if (txtTechCustomer != null) {
            String cust = (req.getCustomerName() != null ? req.getCustomerName() : "—");
            if (req.getCustomerPhoneNumber() != null) cust += "\n" + req.getCustomerPhoneNumber();
            txtTechCustomer.setText(cust);
        }

        // Categories
        if (txtTechCategories != null) {
            String cats = req.getSelectedDeviceCategories() != null
                ? req.getSelectedDeviceCategories().stream()
                    .map(c -> c.getName()).reduce((a, b) -> a + ", " + b).orElse("—")
                : "—";
            txtTechCategories.setText(cats);
        }

        if (txtTechIssue != null)
            txtTechIssue.setText(req.getIssueDescription() != null ? req.getIssueDescription() : "—");

        if (txtTechAddress != null) {
            String addr = req.getAddress() != null ? req.getAddress() : "—";
            if (req.getAddressDetail() != null) addr += "\n" + req.getAddressDetail();
            txtTechAddress.setText(addr);
        }

        // Cost & note (only show if not null)
        if (txtTechCost != null) {
            if (req.getFinalCost() != null) {
                txtTechCost.setText("Biaya akhir: Rp " + req.getFinalCost().toPlainString());
                txtTechCost.setVisible(true); txtTechCost.setManaged(true);
            } else if (req.getEstimatedCost() != null) {
                txtTechCost.setText("Estimasi biaya: Rp " + req.getEstimatedCost().toPlainString());
                txtTechCost.setVisible(true); txtTechCost.setManaged(true);
            }
        }

        if (txtTechNote != null && req.getTechnicianNote() != null) {
            txtTechNote.setText("Catatan: " + req.getTechnicianNote());
            txtTechNote.setVisible(true); txtTechNote.setManaged(true);
        }

        // Action panel
        configureActionPanel(req);
    }

    private void configureActionPanel(ServiceRequestDto req) {
        if (layoutTechnicianActionPanel == null) return;
        layoutTechnicianActionPanel.setVisible(true);
        layoutTechnicianActionPanel.setManaged(true);

        String status = req.getStatus() != null ? req.getStatus().toUpperCase() : "UNKNOWN";

        // Reset all buttons
        setButtonVisible(btnAcceptRequest, false);
        setButtonVisible(btnStartRequest, false);
        setButtonVisible(btnNavigateToCustomer, false);
        setButtonVisible(btnTechnicianChat, false);
        setButtonVisible(btnCompleteRequest, false);
        setButtonVisible(btnRejectRequest, false);

        switch (status) {
            case "PENDING":
                if (txtTechDetailMessage != null)
                    txtTechDetailMessage.setText("Request masuk — belum ada tindakan.");
                setButtonVisible(btnAcceptRequest, true);
                setButtonVisible(btnRejectRequest, true);
                break;

            case "ACCEPTED":
                if (txtTechDetailMessage != null)
                    txtTechDetailMessage.setText("Request diterima. Mulai pengerjaan ketika kamu sudah tiba.");
                setButtonVisible(btnStartRequest, true);
                setButtonVisible(btnNavigateToCustomer, true);
                setButtonVisible(btnTechnicianChat, true);
                break;

            case "IN_PROGRESS":
                if (txtTechDetailMessage != null)
                    txtTechDetailMessage.setText("Sedang mengerjakan. Selesaikan pekerjaan dan klik tombol di bawah.");
                setButtonVisible(btnCompleteRequest, true);
                setButtonVisible(btnTechnicianChat, true);
                break;

            case "COMPLETED":
                if (txtTechDetailMessage != null)
                    txtTechDetailMessage.setText("Pekerjaan selesai. Terima kasih!");
                break;

            case "CANCELLED":
                if (txtTechDetailMessage != null)
                    txtTechDetailMessage.setText("Request dibatalkan oleh pelanggan.\nAlasan: "
                        + (req.getCancelReason() != null ? req.getCancelReason() : "—"));
                break;

            case "REJECTED":
                if (txtTechDetailMessage != null)
                    txtTechDetailMessage.setText("Request ditolak.\nAlasan: "
                        + (req.getRejectReason() != null ? req.getRejectReason() : "—"));
                break;

            default:
                if (txtTechDetailMessage != null) txtTechDetailMessage.setText("");
                break;
        }
    }

    private void setButtonVisible(Button btn, boolean visible) {
        if (btn != null) { btn.setVisible(visible); btn.setManaged(visible); }
    }

    @FXML
    private void handleBack() {
        try { Main.setRoot("/com/teknisio/fxml/TechnicianHome.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleAcceptRequest() {
        if (currentRequest == null) return;
        Thread t = new Thread(() -> {
            try {
                ServiceRequestDto updated = TechnicianRequestService.acceptRequest(currentRequest.getServiceRequestId());
                currentRequest = updated;
                Platform.runLater(() -> populateDetail(updated));
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> showAlert("Gagal menerima request: " + e.getMessage()));
                Thread.currentThread().interrupt();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void handleStartRequest() {
        if (currentRequest == null) return;
        Thread t = new Thread(() -> {
            try {
                ServiceRequestDto updated = TechnicianRequestService.startRequest(currentRequest.getServiceRequestId());
                currentRequest = updated;
                Platform.runLater(() -> populateDetail(updated));
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> showAlert("Gagal memulai pengerjaan: " + e.getMessage()));
                Thread.currentThread().interrupt();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void handleCompleteRequest() {
        if (currentRequest == null) return;

        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle("Selesaikan Pekerjaan");
        dialog.setHeaderText("Masukkan biaya akhir (dalam Rupiah):");
        dialog.setContentText("Biaya (Rp):");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(cost -> {
            TextInputDialog noteDialog = new TextInputDialog();
            noteDialog.setTitle("Catatan Teknisi");
            noteDialog.setHeaderText("Catatan tambahan (opsional):");
            noteDialog.setContentText("Catatan:");
            Optional<String> noteResult = noteDialog.showAndWait();
            String note = noteResult.orElse("");

            Thread t = new Thread(() -> {
                try {
                    ServiceRequestDto updated = TechnicianRequestService.completeRequest(
                        currentRequest.getServiceRequestId(), note, cost);
                    currentRequest = updated;
                    Platform.runLater(() -> populateDetail(updated));
                } catch (IOException | InterruptedException e) {
                    Platform.runLater(() -> showAlert("Gagal menyelesaikan: " + e.getMessage()));
                    Thread.currentThread().interrupt();
                }
            });
            t.setDaemon(true);
            t.start();
        });
    }

    @FXML
    private void handleRejectRequest() {
        if (currentRequest == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tolak Request");
        dialog.setHeaderText("Alasan penolakan (opsional):");
        dialog.setContentText("Alasan:");
        Optional<String> result = dialog.showAndWait();

        Thread t = new Thread(() -> {
            try {
                ServiceRequestDto updated = TechnicianRequestService.rejectRequest(
                    currentRequest.getServiceRequestId(), result.orElse(null));
                currentRequest = updated;
                Platform.runLater(() -> {
                    populateDetail(updated);
                    showAlert("Request berhasil ditolak.");
                });
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> showAlert("Gagal menolak request: " + e.getMessage()));
                Thread.currentThread().interrupt();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void handleNavigateToCustomer() {
        try { Main.setRoot("/com/teknisio/fxml/TrackingMap.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleOpenChat() {
        if (currentRequest == null) return;
        try {
            String custName = currentRequest.getCustomerName() != null ? currentRequest.getCustomerName() : "Pelanggan";
            String custPhoto = currentRequest.getCustomerProfilePhoto();
            String custStatus = "Online";
            String serviceRequestId = currentRequest.getServiceRequestId();

            // Load ChatDetail FXML and set data
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/teknisio/fxml/ChatDetail.fxml"));
            javafx.scene.Parent root = loader.load();
            ChatDetailController detailController = loader.getController();
            detailController.setContactData(custName, custPhoto, custStatus);
            detailController.setServiceRequestId(serviceRequestId);

            btnTechnicianChat.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println("Failed to open chat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        try {
            alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("alert-dialog");
        } catch (Exception ignored) {}
        alert.showAndWait();
    }
}