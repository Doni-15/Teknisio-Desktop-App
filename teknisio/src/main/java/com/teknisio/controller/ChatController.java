package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.dto.ServiceRequestDto;
import com.teknisio.dto.TechnicianDto;
import com.teknisio.service.ServiceRequestService;
import com.teknisio.service.TechnicianService;
import com.teknisio.util.ImageUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ChatController implements Initializable {

    @FXML private VBox activeChatContainer;
    @FXML private VBox technicianChatContainer;
    @FXML private TextField searchField;

    // Active conversations are stored per-session (not from backend)
    public static final List<ChatContact> SESSION_ACTIVE_CONTACTS = new ArrayList<>();

    private List<ChatContact> technicianContacts = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        renderActiveContacts(SESSION_ACTIVE_CONTACTS);

        // Load technicians from backend API
        Thread t = new Thread(() -> {
            List<TechnicianDto> techs = TechnicianService.searchTechnicians(null);
            Platform.runLater(() -> {
                technicianContacts.clear();
                for (TechnicianDto tech : techs) {
                    String spec = tech.getSupportedDeviceCategories() != null
                            && !tech.getSupportedDeviceCategories().isEmpty()
                            ? tech.getSupportedDeviceCategories().get(0).getName() + " Specialist"
                            : "General Specialist";

                    String status = tech.isAvailable() ? "Online" : "Offline";

                    technicianContacts.add(new ChatContact(
                        tech.getName(),
                        spec + " — " + (tech.isAvailable() ? "tersedia" : "tidak tersedia"),
                        "",
                        tech.getProfilePhoto(),
                        status,
                        0,
                        false,
                        tech
                    ));
                }
                renderTechnicianContacts(technicianContacts);
            });
        });
        t.setDaemon(true);
        t.start();

        // Live search filter
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterContacts(newVal));
        }
    }

    private void filterContacts(String query) {
        String lq = query.toLowerCase().trim();
        List<ChatContact> filteredActive;
        List<ChatContact> filteredTech;

        if (lq.isEmpty()) {
            filteredActive = SESSION_ACTIVE_CONTACTS;
            filteredTech = technicianContacts;
        } else {
            filteredActive = SESSION_ACTIVE_CONTACTS.stream()
                .filter(c -> c.getName().toLowerCase().contains(lq)
                    || c.getLastMessage().toLowerCase().contains(lq))
                .collect(Collectors.toList());

            filteredTech = technicianContacts.stream()
                .filter(c -> c.getName().toLowerCase().contains(lq)
                    || c.getLastMessage().toLowerCase().contains(lq))
                .collect(Collectors.toList());
        }

        renderActiveContacts(filteredActive);
        renderTechnicianContacts(filteredTech);
    }

    private void renderActiveContacts(List<ChatContact> contacts) {
        if (activeChatContainer == null) return;
        activeChatContainer.getChildren().clear();

        if (contacts.isEmpty()) {
            Label lbl = new Label("Belum ada percakapan aktif.");
            lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #6F7E91; -fx-padding: 16px;");
            lbl.setMaxWidth(Double.MAX_VALUE);
            activeChatContainer.getChildren().add(lbl);
            return;
        }

        for (int i = 0; i < contacts.size(); i++) {
            HBox row = createContactRow(contacts.get(i), i == contacts.size() - 1, true);
            activeChatContainer.getChildren().add(row);
        }
    }

    private void renderTechnicianContacts(List<ChatContact> contacts) {
        if (technicianChatContainer == null) return;
        technicianChatContainer.getChildren().clear();

        if (contacts.isEmpty()) {
            Label lbl = new Label("Tidak ada teknisi yang ditemukan.");
            lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #6F7E91; -fx-padding: 16px;");
            lbl.setMaxWidth(Double.MAX_VALUE);
            technicianChatContainer.getChildren().add(lbl);
            return;
        }

        for (int i = 0; i < contacts.size(); i++) {
            HBox row = createContactRow(contacts.get(i), i == contacts.size() - 1, false);
            technicianChatContainer.getChildren().add(row);
        }
    }

    private HBox createContactRow(ChatContact contact, boolean isLast, boolean isActiveChat) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(12);
        row.setPadding(new Insets(14, 18, 14, 18));

        String border = isLast ? "-fx-border-color: transparent; -fx-border-width: 0; -fx-cursor: hand;"
                : "-fx-border-color: transparent transparent #F1F5F9 transparent; -fx-border-width: 0 0 1px 0; -fx-cursor: hand;";
        row.setStyle(border);

        row.setOnMouseEntered(e -> row.setStyle(row.getStyle() + "-fx-background-color: #F8FAFC;"));
        row.setOnMouseExited(e -> row.setStyle(border));

        // Avatar
        ImageView avatar = new ImageView();
        avatar.setFitHeight(48);
        avatar.setFitWidth(48);
        avatar.setPickOnBounds(true);
        avatar.setPreserveRatio(false);

        String photo = contact.getAvatarBase64();
        if (photo != null && !photo.isBlank()) {
            ImageUtil.applyBase64ToImageView(avatar, photo);
        } else {
            try {
                avatar.setImage(new Image(getClass().getResource("/com/teknisio/assets/profile/profile.png").toExternalForm()));
            } catch (Exception ignored) {}
        }

        Circle clip = new Circle(24, 24, 24);
        avatar.setClip(clip);

        StackPane avatarWrapper = new StackPane(avatar);
        avatarWrapper.setMinSize(48, 48);
        avatarWrapper.setMaxSize(48, 48);

        // Online dot
        StackPane onlineDot = new StackPane();
        onlineDot.setMinSize(12, 12);
        onlineDot.setMaxSize(12, 12);
        onlineDot.setStyle("-fx-background-radius: 50%; -fx-background-color: "
            + ("Online".equals(contact.getStatus()) ? "#27AE60" : "#95A5A6") + ";");

        StackPane avatarStack = new StackPane(avatarWrapper, onlineDot);
        StackPane.setAlignment(onlineDot, Pos.BOTTOM_RIGHT);

        // Middle
        VBox details = new VBox();
        details.setAlignment(Pos.CENTER_LEFT);
        details.setSpacing(4);
        HBox.setHgrow(details, Priority.ALWAYS);

        Label nameLabel = new Label(contact.getName());
        nameLabel.getStyleClass().add("chat-name");

        Label msgLabel = new Label(contact.getLastMessage());
        msgLabel.getStyleClass().add("chat-message");

        details.getChildren().addAll(nameLabel, msgLabel);

        // Right side
        VBox rightBox = new VBox();
        rightBox.setAlignment(Pos.TOP_RIGHT);
        rightBox.setSpacing(6);
        rightBox.setPrefWidth(60);

        if (isActiveChat) {
            Label timeLabel = new Label(contact.getTime());
            timeLabel.getStyleClass().add("chat-time");
            rightBox.getChildren().add(timeLabel);

            if (contact.getUnreadCount() > 0) {
                StackPane badge = new StackPane();
                badge.getStyleClass().add("chat-unread-badge");
                Label badgeText = new Label(String.valueOf(contact.getUnreadCount()));
                badgeText.getStyleClass().add("chat-unread-text");
                badge.getChildren().add(badgeText);
                rightBox.getChildren().add(badge);
            }
        } else {
            Label startChatLabel = new Label("Chat");
            startChatLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #2D4B73;"
                + "-fx-background-color: #E2ECF7; -fx-background-radius: 12px; -fx-padding: 4px 10px;");
            rightBox.getChildren().add(startChatLabel);
        }

        row.getChildren().addAll(avatarStack, details, rightBox);

        row.setOnMouseClicked(event -> {
            openChatDetail(contact);
            event.consume();
        });

        return row;
    }

    private void openChatDetail(ChatContact contact) {
        // If this is already an active chat, navigate directly.
        if (contact.isActiveChat()) {
            navigateToChatDetail(contact);
            return;
        }

        // Otherwise, validate that the technician has an ACCEPTED or IN_PROGRESS service request.
        // Run the API call on a background thread to avoid blocking the UI.
        Thread t = new Thread(() -> {
            TechnicianDto techDto = contact.getTechnicianDto();
            String technicianProfileId = techDto != null ? techDto.getTechnicianProfileId() : null;

            boolean hasActiveRequest = false;
            if (technicianProfileId != null) {
                // Fetch customer's service requests that are in ACCEPTED status
                List<ServiceRequestDto> acceptedRequests = ServiceRequestService.getMyServiceRequests("ACCEPTED");
                List<ServiceRequestDto> inProgressRequests = ServiceRequestService.getMyServiceRequests("ON_PROGRESS");

                hasActiveRequest = acceptedRequests.stream()
                        .anyMatch(r -> technicianProfileId.equals(r.getTechnicianProfileId()));

                if (!hasActiveRequest) {
                    hasActiveRequest = inProgressRequests.stream()
                            .anyMatch(r -> technicianProfileId.equals(r.getTechnicianProfileId()));
                }
            }

            final boolean canChat = hasActiveRequest;
            Platform.runLater(() -> {
                if (canChat) {
                    navigateToChatDetail(contact);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Chat Tidak Tersedia");
                    alert.setHeaderText(null);
                    alert.setContentText(
                        "Fitur chat belum tersedia. Anda hanya dapat mengirim pesan " +
                        "setelah teknisi menerima permintaan perbaikan Anda.");
                    try {
                        alert.getDialogPane().getStylesheets().add(
                            getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
                        alert.getDialogPane().getStyleClass().add("alert-dialog");
                    } catch (Exception ignored) {}
                    alert.showAndWait();
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private void navigateToChatDetail(ChatContact contact) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teknisio/fxml/ChatDetail.fxml"));
            Parent root = loader.load();
            ChatDetailController detailController = loader.getController();
            detailController.setContactData(contact.getName(), contact.getAvatarBase64(), contact.getStatus());

            // Add to active conversations if not already present
            boolean exists = SESSION_ACTIVE_CONTACTS.stream()
                .anyMatch(c -> c.getName().equals(contact.getName()));
            if (!exists) {
                ChatContact active = new ChatContact(
                    contact.getName(), "", "", contact.getAvatarBase64(),
                    contact.getStatus(), 0, true, contact.getTechnicianDto());
                SESSION_ACTIVE_CONTACTS.add(0, active);
            }

            javafx.scene.Scene scene = searchField != null ? searchField.getScene() : null;
            if (scene != null) scene.setRoot(root);
        } catch (IOException e) {
            System.err.println("Failed to open chat detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try { Main.setRoot("/com/teknisio/fxml/home_user.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Represents a chat contact (technician or active conversation).
     */
    public static class ChatContact {
        private String name;
        private String lastMessage;
        private String time;
        private String avatarBase64; // base64 photo
        private String status;
        private int unreadCount;
        private boolean isActiveChat;
        private TechnicianDto technicianDto;

        public ChatContact(String name, String lastMessage, String time, String avatarBase64,
                           String status, int unreadCount, boolean isActiveChat, TechnicianDto dto) {
            this.name = name;
            this.lastMessage = lastMessage;
            this.time = time;
            this.avatarBase64 = avatarBase64;
            this.status = status;
            this.unreadCount = unreadCount;
            this.isActiveChat = isActiveChat;
            this.technicianDto = dto;
        }

        public String getName() { return name; }
        public String getLastMessage() { return lastMessage; }
        public void setLastMessage(String msg) { this.lastMessage = msg; }
        public String getTime() { return time; }
        public String getAvatarBase64() { return avatarBase64; }
        public String getStatus() { return status; }
        public int getUnreadCount() { return unreadCount; }
        public boolean isActiveChat() { return isActiveChat; }
        public TechnicianDto getTechnicianDto() { return technicianDto; }
    }
}