package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.dto.ChatMessageDto;
import com.teknisio.service.ChatService;
import com.teknisio.service.SessionManager;
import com.teknisio.util.ImageUtil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatDetailController implements Initializable {

    @FXML private ImageView contactAvatar;
    @FXML private Label contactNameLabel;
    @FXML private Label contactStatusLabel;
    @FXML private Label dateLabel;
    @FXML private VBox messagesContainer;
    @FXML private ScrollPane messagesScrollPane;
    @FXML private TextField messageInput;
    @FXML private Button sendBtn;

    private String contactName;
    private String contactAvatarBase64;
    private String contactStatus;
    private String serviceRequestId;

    // ID of the last loaded message to detect new messages on poll
    private String lastLoadedMessageId = null;

    private ScheduledExecutorService pollingExecutor;
    private static final int POLL_INTERVAL_SECONDS = 3;

    private final DateTimeFormatter timeFormatter =
        DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Circular crop on avatar
        Circle clip = new Circle(20, 20, 20);
        contactAvatar.setClip(clip);

        contactName = "Teknisi";
        contactAvatarBase64 = null;
        contactStatus = "Online";

        applyContactData();

        messageInput.setOnAction(this::handleSend);
        Platform.runLater(this::scrollToBottom);
    }

    /**
     * Called by the opener (ChatController / ServiceRequestDetailController)
     * BEFORE the scene is shown so data is available at initialize time.
     */
    public void setContactData(String name, String avatarBase64, String status) {
        this.contactName = name;
        this.contactAvatarBase64 = avatarBase64;
        this.contactStatus = status;
    }

    /**
     * Must be called after setContactData to link this chat to a service request.
     * Triggers initial message load and starts polling.
     */
    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
        applyContactData();
        loadMessages();
        startPolling();
    }

    private void applyContactData() {
        if (contactNameLabel == null) return; // not yet initialized by FXMLLoader
        contactNameLabel.setText(contactName);
        contactStatusLabel.setText(contactStatus);

        if (contactStatus != null && contactStatus.contains("Online")) {
            contactStatusLabel.setStyle("-fx-text-fill: #27AE60;");
        } else {
            contactStatusLabel.setStyle("-fx-text-fill: #95A5A6;");
        }

        if (contactAvatarBase64 != null && !contactAvatarBase64.isBlank()) {
            ImageUtil.applyBase64ToImageView(contactAvatar, contactAvatarBase64);
        } else {
            try {
                contactAvatar.setImage(new javafx.scene.image.Image(
                    getClass().getResource("/com/teknisio/assets/profile/profile.png").toExternalForm()));
            } catch (Exception e) {
                System.err.println("Failed to load default chat avatar");
            }
        }
    }

    // ---- Message loading & polling ----

    private void loadMessages() {
        if (serviceRequestId == null) return;

        Thread t = new Thread(() -> {
            List<ChatMessageDto> messages = ChatService.getMessages(serviceRequestId);
            Platform.runLater(() -> {
                messagesContainer.getChildren().clear();
                lastLoadedMessageId = null;
                for (ChatMessageDto msg : messages) {
                    appendMessageBubble(msg);
                }
                scrollToBottom();
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private void startPolling() {
        pollingExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "chat-poll");
            t.setDaemon(true);
            return t;
        });

        pollingExecutor.scheduleAtFixedRate(() -> {
            if (serviceRequestId == null) return;
            List<ChatMessageDto> messages = ChatService.getMessages(serviceRequestId);
            if (messages.isEmpty()) return;

            String latestId = messages.get(messages.size() - 1).getIdPesan();
            if (latestId != null && latestId.equals(lastLoadedMessageId)) return; // no new messages

            // Find new messages (those after the lastLoadedMessageId)
            int startIdx = 0;
            if (lastLoadedMessageId != null) {
                for (int i = 0; i < messages.size(); i++) {
                    if (lastLoadedMessageId.equals(messages.get(i).getIdPesan())) {
                        startIdx = i + 1;
                        break;
                    }
                }
            }

            final List<ChatMessageDto> newMessages = messages.subList(startIdx, messages.size());
            if (newMessages.isEmpty()) return;

            Platform.runLater(() -> {
                for (ChatMessageDto msg : newMessages) {
                    appendMessageBubble(msg);
                }
                scrollToBottom();
            });

        }, POLL_INTERVAL_SECONDS, POLL_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private void stopPolling() {
        if (pollingExecutor != null && !pollingExecutor.isShutdown()) {
            pollingExecutor.shutdownNow();
        }
    }

    // ---- UI helpers ----

    /**
     * Append a single chat message bubble to the messages container.
     * Determines SENT vs RECEIVED based on the sender's userId vs current session userId.
     */
    private void appendMessageBubble(ChatMessageDto msg) {
        String currentUserId = SessionManager.getUserIdString();
        boolean isSent = currentUserId != null && currentUserId.equals(msg.getSenderId());

        HBox row = new HBox();
        row.setPadding(new Insets(2, 0, 2, 0));
        row.setAlignment(isSent ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        VBox bubble = new VBox();
        bubble.setSpacing(2);
        bubble.setMaxWidth(260);

        Label messageLabel = new Label(msg.getIsi());
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(240);
        messageLabel.getStyleClass().add(isSent ? "chat-bubble-sent" : "chat-bubble-received");

        // Format time from ISO timestamp
        String timeStr = msg.getCreatedAt();
        if (timeStr != null && timeStr.length() >= 16) {
            try {
                OffsetDateTime odt = OffsetDateTime.parse(timeStr);
                timeStr = odt.format(timeFormatter);
            } catch (Exception ignored) {
                timeStr = timeStr.substring(11, 16); // fallback: "HH:mm"
            }
        }

        Label timeLabel = new Label(timeStr != null ? timeStr : "");
        timeLabel.getStyleClass().add("chat-bubble-time");
        timeLabel.setStyle(isSent ? "-fx-alignment: center-right;" : "-fx-alignment: center-left;");

        bubble.getChildren().addAll(messageLabel, timeLabel);

        Region spacer = new Region();
        spacer.setPrefWidth(50);
        spacer.setMinWidth(50);

        if (isSent) {
            row.getChildren().addAll(spacer, bubble);
        } else {
            row.getChildren().addAll(bubble, spacer);
        }

        messagesContainer.getChildren().add(row);

        // Track the last message ID shown
        if (msg.getIdPesan() != null) {
            lastLoadedMessageId = msg.getIdPesan();
        }
    }

    private void scrollToBottom() {
        messagesScrollPane.setVvalue(1.0);
    }

    // ---- FXML Handlers ----

    @FXML
    private void handleSend(ActionEvent event) {
        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;

        if (serviceRequestId == null) {
            showAlert("Error", "Tidak dapat mengirim pesan: percakapan tidak terhubung ke pesanan.");
            return;
        }

        messageInput.clear();
        messageInput.setDisable(true);

        Thread t = new Thread(() -> {
            ChatMessageDto sent = ChatService.sendMessage(serviceRequestId, text);
            Platform.runLater(() -> {
                messageInput.setDisable(false);
                messageInput.requestFocus();
                if (sent != null) {
                    appendMessageBubble(sent);
                    scrollToBottom();
                } else {
                    showAlert("Gagal Kirim", "Pesan gagal dikirim. Periksa koneksi dan coba lagi.");
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        stopPolling();
        try {
            Main.setRoot("/com/teknisio/fxml/Chat.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate back to Chat: " + e.getMessage());
        }
    }

    @FXML
    private void handleCall(ActionEvent event) {
        showAlert("Voice Call", "Initiating voice call with " + contactName + "...");
    }

    @FXML
    private void handleVideoCall(ActionEvent event) {
        showAlert("Video Call", "Initiating video call with " + contactName + "...");
    }

    @FXML
    private void handleAttach(ActionEvent event) {
        showAlert("Attachment", "Attachment feature will be available in the next update.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
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