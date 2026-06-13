package com.teknisio.controller;

import com.teknisio.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ChatController implements Initializable {

    @FXML
    private VBox activeChatContainer;

    @FXML
    private VBox technicianChatContainer;

    @FXML
    private TextField searchField;

    private List<ChatContact> activeContacts = new ArrayList<>();
    private List<ChatContact> technicianContacts = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadContacts();
        renderAllContacts();

        // Live search filter
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterContacts(newVal);
        });
    }

    private void loadContacts() {
        // Active conversations (ongoing)
        activeContacts.add(new ChatContact(
            "Ahmed Rush",
            "I will come around 2 PM, Please be at home",
            "08:43",
"/com/teknisio/assets/technicians/tech_ahmed.png",
            "Online",
            2,
            true
        ));

        activeContacts.add(new ChatContact(
            "Evan Bran",
            "Thank you for calling me",
            "4/22/46",
"/com/teknisio/assets/technicians/tech_devon.png",
            "Offline",
            0,
            true
        ));

        // Available technicians (not yet chatting)
        technicianContacts.add(new ChatContact(
            "Charlie Hugh",
            "Oven Specialist — available for new orders",
            "",
"/com/teknisio/assets/technicians/tech_cedric.png",
            "Online",
            0,
            false
        ));

        technicianContacts.add(new ChatContact(
            "Ben Adams",
            "Fridge Specialist — ready to help",
            "",
            "/com/teknisio/assets/technicians/tech_devon.png",
            "Offline",
            0,
            false
        ));

        technicianContacts.add(new ChatContact(
            "Diana Rose",
            "Washing Machine Specialist — available",
            "",
            "/com/teknisio/assets/technicians/tech_ahmed.png",
            "Online",
            0,
            false
        ));

        technicianContacts.add(new ChatContact(
            "Franklin Park",
            "TV Specialist — away",
            "",
            "/com/teknisio/assets/technicians/tech_cedric.png",
            "Offline",
            0,
            false
        ));
    }

    private void renderAllContacts() {
        renderActiveContacts(activeContacts);
        renderTechnicianContacts(technicianContacts);
    }

    private void filterContacts(String query) {
        String lowerQuery = query.toLowerCase().trim();

        List<ChatContact> filteredActive;
        List<ChatContact> filteredTech;

        if (lowerQuery.isEmpty()) {
            filteredActive = activeContacts;
            filteredTech = technicianContacts;
        } else {
            filteredActive = activeContacts.stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerQuery)
                    || c.getLastMessage().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());

            filteredTech = technicianContacts.stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerQuery)
                    || c.getLastMessage().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
        }

        renderActiveContacts(filteredActive);
        renderTechnicianContacts(filteredTech);
    }

    private void renderActiveContacts(List<ChatContact> contacts) {
        activeChatContainer.getChildren().clear();

        for (int i = 0; i < contacts.size(); i++) {
            ChatContact contact = contacts.get(i);
            boolean isLast = (i == contacts.size() - 1);
            HBox row = createContactRow(contact, isLast, true);
            activeChatContainer.getChildren().add(row);
        }

        if (contacts.isEmpty()) {
            Label emptyLabel = new Label("No active conversations found.");
            emptyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6F7E91; -fx-padding: 20px; -fx-alignment: center;");
            emptyLabel.setMaxWidth(Double.MAX_VALUE);
            activeChatContainer.getChildren().add(emptyLabel);
        }
    }

    private void renderTechnicianContacts(List<ChatContact> contacts) {
        technicianChatContainer.getChildren().clear();

        for (int i = 0; i < contacts.size(); i++) {
            ChatContact contact = contacts.get(i);
            boolean isLast = (i == contacts.size() - 1);
            HBox row = createContactRow(contact, isLast, false);
            technicianChatContainer.getChildren().add(row);
        }

        if (contacts.isEmpty()) {
            Label emptyLabel = new Label("No technicians found.");
            emptyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6F7E91; -fx-padding: 20px; -fx-alignment: center;");
            emptyLabel.setMaxWidth(Double.MAX_VALUE);
            technicianChatContainer.getChildren().add(emptyLabel);
        }
    }

    private HBox createContactRow(ChatContact contact, boolean isLast, boolean isActiveChat) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(12);
        row.setPadding(new Insets(14, 18, 14, 18));

        if (!isLast) {
            row.setStyle("-fx-border-color: transparent transparent #F1F5F9 transparent; -fx-border-width: 0 0 1px 0; -fx-cursor: hand;");
        } else {
            row.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-cursor: hand;");
        }

        // Add hover style
        row.setOnMouseEntered(e -> row.setStyle(row.getStyle() + "-fx-background-color: #F8FAFC;"));
        row.setOnMouseExited(e -> {
            if (!isLast) {
                row.setStyle("-fx-border-color: transparent transparent #F1F5F9 transparent; -fx-border-width: 0 0 1px 0; -fx-cursor: hand;");
            } else {
                row.setStyle("-fx-border-color: transparent; -fx-border-width: 0; -fx-cursor: hand;");
            }
        });

        // Avatar
        ImageView avatar = new ImageView();
        avatar.setFitHeight(48);
        avatar.setFitWidth(48);
        avatar.setPickOnBounds(true);
        avatar.setPreserveRatio(false);

        try {
            avatar.setImage(new Image(getClass().getResource(contact.getAvatarPath()).toExternalForm()));
        } catch (Exception e) {
            System.err.println("Failed to load avatar: " + contact.getAvatarPath());
        }

        Circle clip = new Circle(24, 24, 24);
        avatar.setClip(clip);

        StackPane avatarWrapper = new StackPane(avatar);
        avatarWrapper.setMinSize(48, 48);
        avatarWrapper.setMaxSize(48, 48);
        avatarWrapper.setAlignment(Pos.CENTER);

        // Online/Offline indicator
        StackPane onlineDot = new StackPane();
        onlineDot.setMinSize(12, 12);
        onlineDot.setMaxSize(12, 12);
        onlineDot.setStyle("-fx-background-radius: 50%; -fx-background-color: "
            + (contact.getStatus().equals("Online") ? "#27AE60" : "#95A5A6") + ";");

        StackPane avatarStack = new StackPane(avatarWrapper, onlineDot);
        StackPane.setAlignment(onlineDot, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(onlineDot, new Insets(0, 0, 0, 0));

        // Middle details
        VBox details = new VBox();
        details.setAlignment(Pos.CENTER_LEFT);
        details.setSpacing(4);
        HBox.setHgrow(details, javafx.scene.layout.Priority.ALWAYS);

        Label nameLabel = new Label(contact.getName());
        nameLabel.getStyleClass().add("chat-name");

        Label messageLabel = new Label(contact.getLastMessage());
        messageLabel.getStyleClass().add("chat-message");

        details.getChildren().addAll(nameLabel, messageLabel);

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
            // "Start Chat" label for technicians
            Label startChatLabel = new Label("Chat");
            startChatLabel.setStyle(
                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #2D4B73;"
                + "-fx-background-color: #E2ECF7; -fx-background-radius: 12px; -fx-padding: 4px 10px;"
            );
            rightBox.getChildren().add(startChatLabel);
        }

        row.getChildren().addAll(avatarStack, details, rightBox);

        // Click handler
        row.setOnMouseClicked(event -> {
            openChatDetail(contact);
            event.consume();
        });

        return row;
    }

    private void openChatDetail(ChatContact contact) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teknisio/fxml/ChatDetail.fxml"));
            Parent root = loader.load();

            ChatDetailController detailController = loader.getController();
            detailController.setContactData(contact.getName(), contact.getAvatarPath(), contact.getStatus());

            // Set the scene root
            javafx.scene.Scene scene = searchField.getScene();
            if (scene != null) {
                scene.setRoot(root);
            }
        } catch (IOException e) {
            System.err.println("Failed to open chat detail: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/home_user.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inner class representing a chat contact.
     */
    public static class ChatContact {
        private String name;
        private String lastMessage;
        private String time;
        private String avatarPath;
        private String status;
        private int unreadCount;
        private boolean isActiveChat;

        public ChatContact(String name, String lastMessage, String time, String avatarPath, String status, int unreadCount, boolean isActiveChat) {
            this.name = name;
            this.lastMessage = lastMessage;
            this.time = time;
            this.avatarPath = avatarPath;
            this.status = status;
            this.unreadCount = unreadCount;
            this.isActiveChat = isActiveChat;
        }

        public String getName() { return name; }
        public String getLastMessage() { return lastMessage; }
        public String getTime() { return time; }
        public String getAvatarPath() { return avatarPath; }
        public String getStatus() { return status; }
        public int getUnreadCount() { return unreadCount; }
        public boolean isActiveChat() { return isActiveChat; }
    }
}