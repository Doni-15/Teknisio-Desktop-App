package com.teknisio.controller;

import com.teknisio.Main;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChatDetailController implements Initializable {

    @FXML
    private ImageView contactAvatar;

    @FXML
    private Label contactNameLabel;

    @FXML
    private Label contactStatusLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private VBox messagesContainer;

    @FXML
    private ScrollPane messagesScrollPane;

    @FXML
    private TextField messageInput;

    @FXML
    private Button sendBtn;

    private String contactName;
    private String contactAvatarBase64; // base64 string
    private String contactStatus;
    private List<ChatMessage> messages = new ArrayList<>();
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Circular crop on avatar
        Circle clip = new Circle(20, 20, 20);
        contactAvatar.setClip(clip);

        // Load default data (will be overridden if setData is called)
        contactName = "Teknisi";
        contactAvatarBase64 = null;
        contactStatus = "Online";

        applyContactData();

        // Load dummy messages
        loadDummyMessages();
        renderMessages();

        // Enable send on Enter key
        messageInput.setOnAction(this::handleSend);

        // Auto-scroll to bottom after rendering
        Platform.runLater(this::scrollToBottom);
    }

    /**
     * Set contact data before this controller displays.
     */
    public void setContactData(String name, String avatarBase64, String status) {
        this.contactName = name;
        this.contactAvatarBase64 = avatarBase64;
        this.contactStatus = status;
    }

    private void applyContactData() {
        contactNameLabel.setText(contactName);
        contactStatusLabel.setText(contactStatus);

        if (contactStatus != null && contactStatus.contains("Online")) {
            contactStatusLabel.setStyle("-fx-text-fill: #27AE60;");
        } else {
            contactStatusLabel.setStyle("-fx-text-fill: #95A5A6;");
        }

        // Load base64 photo or fallback to placeholder
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

    private void loadDummyMessages() {
        messages.clear();

        if (contactName.contains("Ahmed")) {
            messages.add(new ChatMessage("Hello, I'm Ahmed Rush, your AC specialist.", ChatMessageType.RECEIVED, "08:30"));
            messages.add(new ChatMessage("Hi Ahmed! My AC is not cooling properly.", ChatMessageType.SENT, "08:32"));
            messages.add(new ChatMessage("I see. Can you describe the issue more? Is it completely not cooling or just low cooling?", ChatMessageType.RECEIVED, "08:33"));
            messages.add(new ChatMessage("It's barely cooling. The room temperature stays at 28°C.", ChatMessageType.SENT, "08:35"));
            messages.add(new ChatMessage("I understand. I can come over today around 2 PM. Will you be available?", ChatMessageType.RECEIVED, "08:40"));
            messages.add(new ChatMessage("Yes, I'll be at home. Please come.", ChatMessageType.SENT, "08:41"));
            messages.add(new ChatMessage("I will come around 2 PM, Please be at home.", ChatMessageType.RECEIVED, "08:43"));
        } else if (contactName.contains("Evan")) {
            messages.add(new ChatMessage("Hi, this is Evan Bran, your technician.", ChatMessageType.RECEIVED, "09:10"));
            messages.add(new ChatMessage("Hello Evan! Thanks for accepting my request.", ChatMessageType.SENT, "09:12"));
            messages.add(new ChatMessage("You're welcome! I'll fix your fridge issue.", ChatMessageType.RECEIVED, "09:14"));
            messages.add(new ChatMessage("Thank you for calling me.", ChatMessageType.RECEIVED, "09:15"));
        } else {
            messages.add(new ChatMessage("Hello! How can I help you today?", ChatMessageType.RECEIVED, "10:00"));
            messages.add(new ChatMessage("Hi, I have a repair request.", ChatMessageType.SENT, "10:02"));
            messages.add(new ChatMessage("Sure, I'm on my way. See you soon.", ChatMessageType.RECEIVED, "10:05"));
        }
    }

    private void renderMessages() {
        messagesContainer.getChildren().clear();

        String currentDate = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        for (ChatMessage msg : messages) {
            // Add message bubble
            HBox messageRow = createMessageBubble(msg);
            messagesContainer.getChildren().add(messageRow);
        }
    }

    private HBox createMessageBubble(ChatMessage msg) {
        HBox row = new HBox();
        row.setPadding(new Insets(2, 0, 2, 0));

        boolean isSent = msg.getType() == ChatMessageType.SENT;

        if (isSent) {
            row.setAlignment(Pos.CENTER_RIGHT);
        } else {
            row.setAlignment(Pos.CENTER_LEFT);
        }

        // Main bubble VBox
        VBox bubble = new VBox();
        bubble.setSpacing(2);
        bubble.setMaxWidth(260);

        // Message text
        Label messageLabel = new Label(msg.getContent());
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(240);

        if (isSent) {
            messageLabel.getStyleClass().add("chat-bubble-sent");
        } else {
            messageLabel.getStyleClass().add("chat-bubble-received");
        }

        // Time label
        Label timeLabel = new Label(msg.getTime());
        timeLabel.getStyleClass().add("chat-bubble-time");
        if (isSent) {
            timeLabel.setStyle("-fx-alignment: center-right;");
        } else {
            timeLabel.setStyle("-fx-alignment: center-left;");
        }

        bubble.getChildren().addAll(messageLabel, timeLabel);

        // Add spacing for alignment
        Region spacer = new Region();
        spacer.setPrefWidth(50);
        spacer.setMinWidth(50);

        if (isSent) {
            row.getChildren().addAll(spacer, bubble);
        } else {
            row.getChildren().addAll(bubble, spacer);
        }

        return row;
    }

    @FXML
    private void handleSend(ActionEvent event) {
        String text = messageInput.getText().trim();
        if (text.isEmpty()) {
            return;
        }

        // Add sent message
        String currentTime = LocalDateTime.now().format(timeFormatter);
        ChatMessage sentMsg = new ChatMessage(text, ChatMessageType.SENT, currentTime);
        messages.add(sentMsg);

        // Add to UI
        HBox bubble = createMessageBubble(sentMsg);
        messagesContainer.getChildren().add(bubble);

        // Clear input
        messageInput.clear();

        // Scroll to bottom
        Platform.runLater(this::scrollToBottom);

        // Simulate auto-reply after a short delay
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                String reply = getAutoReply(text);
                String replyTime = LocalDateTime.now().format(timeFormatter);
                ChatMessage replyMsg = new ChatMessage(reply, ChatMessageType.RECEIVED, replyTime);
                messages.add(replyMsg);

                HBox replyBubble = createMessageBubble(replyMsg);
                messagesContainer.getChildren().add(replyBubble);

                scrollToBottom();
            });
        }).start();
    }

    private String getAutoReply(String userMessage) {
        String lower = userMessage.toLowerCase();
        if (lower.contains("hello") || lower.contains("hi") || lower.contains("halo")) {
            return "Hello! How can I assist you with your repair?";
        } else if (lower.contains("thanks") || lower.contains("thank you") || lower.contains("makasih") || lower.contains("terima kasih")) {
            return "You're welcome! 😊 Let me know if you need anything else.";
        } else if (lower.contains("ac") || lower.contains("pendingin")) {
            return "I can help with your AC issue. Can you tell me the brand and model?";
        } else if (lower.contains("price") || lower.contains("harga") || lower.contains("biaya")) {
            return "The price depends on the issue. Typically, it ranges from Rp 50.000 to Rp 200.000.";
        } else if (lower.contains("arrive") || lower.contains("datang") || lower.contains("jam")) {
            return "I'll be there at the scheduled time. Please wait for me.";
        } else if (lower.contains("ok") || lower.contains("oke") || lower.contains("baik")) {
            return "Great! I've noted that. See you soon.";
        } else {
            return "Thank you for your message. I'll get back to you shortly.";
        }
    }

    private void scrollToBottom() {
        // Scroll to the bottom of the messages
        messagesScrollPane.setVvalue(1.0);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/Chat.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate back to Chat: " + e.getMessage());
            e.printStackTrace();
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
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("alert-dialog");
        alert.showAndWait();
    }

    /**
     * Inner enum for message type.
     */
    public enum ChatMessageType {
        SENT,
        RECEIVED
    }

    /**
     * Inner class representing a single chat message.
     */
    public static class ChatMessage {
        private String content;
        private ChatMessageType type;
        private String time;

        public ChatMessage(String content, ChatMessageType type, String time) {
            this.content = content;
            this.type = type;
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public ChatMessageType getType() {
            return type;
        }

        public String getTime() {
            return time;
        }
    }
}