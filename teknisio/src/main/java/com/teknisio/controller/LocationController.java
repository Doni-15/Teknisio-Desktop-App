package com.teknisio.controller;

import com.teknisio.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LocationController implements Initializable {

    @FXML
    private TextField searchAddressField;

    @FXML
    private VBox savedAddressContainer;

    @FXML
    private VBox recentLocationContainer;

    private static String backRoute = "/com/teknisio/fxml/home_user.fxml";

    public static void setBackRoute(String route) {
        backRoute = route;
    }

    private List<LocationItem> savedAddresses = new ArrayList<>();
    private List<LocationItem> recentLocations = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadLocationData();
        renderAllLocations();

        // Live search filter
        searchAddressField.textProperty().addListener((obs, oldVal, newVal) -> {
            filterLocations(newVal);
        });
    }

    private void loadLocationData() {
        // Saved Addresses
        savedAddresses.add(new LocationItem(
            "Home",
            "Intertip St. No. 42, Medan, North Sumatra",
            "Intertip St., Medan",
            true // currently selected
        ));

        savedAddresses.add(new LocationItem(
            "Office",
            "Jl. Gatot Subroto No. 88, Medan, North Sumatra",
            "Gatot Subroto St., Medan",
            false
        ));

        // Recent Locations
        recentLocations.add(new LocationItem(
            "Mall Centre Point",
            "Jl. Timor No. 10, Medan, North Sumatra",
            "Timor St., Medan",
            false
        ));

        recentLocations.add(new LocationItem(
            "Grand Aston Hotel",
            "Jl. Balai Kota No. 1, Medan, North Sumatra",
            "Balai Kota St., Medan",
            false
        ));

        recentLocations.add(new LocationItem(
            "Universitas Sumatera Utara",
            "Jl. Dr. Mansyur No. 9, Medan, North Sumatra",
            "Dr. Mansyur St., Medan",
            false
        ));

        recentLocations.add(new LocationItem(
            "Sun Plaza",
            "Jl. KH. Zainul Arifin No. 7, Medan, North Sumatra",
            "Zainul Arifin St., Medan",
            false
        ));
    }

    private void renderAllLocations() {
        renderSavedAddresses(savedAddresses);
        renderRecentLocations(recentLocations);
    }

    private void filterLocations(String query) {
        String lowerQuery = query.toLowerCase().trim();

        List<LocationItem> filteredSaved;
        List<LocationItem> filteredRecent;

        if (lowerQuery.isEmpty()) {
            filteredSaved = savedAddresses;
            filteredRecent = recentLocations;
        } else {
            filteredSaved = savedAddresses.stream()
                .filter(l -> l.getLabel().toLowerCase().contains(lowerQuery)
                    || l.getFullAddress().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());

            filteredRecent = recentLocations.stream()
                .filter(l -> l.getLabel().toLowerCase().contains(lowerQuery)
                    || l.getFullAddress().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
        }

        renderSavedAddresses(filteredSaved);
        renderRecentLocations(filteredRecent);
    }

    private void renderSavedAddresses(List<LocationItem> items) {
        savedAddressContainer.getChildren().clear();

        for (int i = 0; i < items.size(); i++) {
            LocationItem item = items.get(i);
            boolean isLast = (i == items.size() - 1);
            HBox row = createLocationRow(item, isLast, true);
            savedAddressContainer.getChildren().add(row);
        }

        if (items.isEmpty()) {
            Label emptyLabel = new Label("No saved addresses match your search.");
            emptyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6F7E91; -fx-padding: 20px; -fx-alignment: center;");
            emptyLabel.setMaxWidth(Double.MAX_VALUE);
            savedAddressContainer.getChildren().add(emptyLabel);
        }
    }

    private void renderRecentLocations(List<LocationItem> items) {
        recentLocationContainer.getChildren().clear();

        for (int i = 0; i < items.size(); i++) {
            LocationItem item = items.get(i);
            boolean isLast = (i == items.size() - 1);
            HBox row = createLocationRow(item, isLast, false);
            recentLocationContainer.getChildren().add(row);
        }

        if (items.isEmpty()) {
            Label emptyLabel = new Label("No recent locations match your search.");
            emptyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6F7E91; -fx-padding: 20px; -fx-alignment: center;");
            emptyLabel.setMaxWidth(Double.MAX_VALUE);
            recentLocationContainer.getChildren().add(emptyLabel);
        }
    }

    private HBox createLocationRow(LocationItem item, boolean isLast, boolean isSaved) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(12);
        row.setPadding(new Insets(14, 18, 14, 18));
        row.setStyle("-fx-cursor: hand;");

        if (!isLast) {
            row.setStyle(row.getStyle() + "-fx-border-color: transparent transparent #F1F5F9 transparent; -fx-border-width: 0 0 1px 0;");
        }

        // Hover effect
        row.setOnMouseEntered(e -> row.setStyle(row.getStyle() + "-fx-background-color: #F8FAFC;"));
        row.setOnMouseExited(e -> {
            row.setStyle("-fx-cursor: hand;");
            if (!isLast) {
                row.setStyle(row.getStyle() + "-fx-border-color: transparent transparent #F1F5F9 transparent; -fx-border-width: 0 0 1px 0;");
            }
        });

        // Location Pin Icon
        StackPane iconWrapper = new StackPane();
        iconWrapper.setMinSize(40, 40);
        iconWrapper.setMaxSize(40, 40);
        iconWrapper.getStyleClass().add("category-icon-circle-wrapper");

        SVGPath pinIcon = new SVGPath();
        pinIcon.setContent("M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z");
        pinIcon.setFill(Color.web("#2D4B73"));
        pinIcon.setScaleX(0.6);
        pinIcon.setScaleY(0.6);

        iconWrapper.getChildren().add(pinIcon);

        // Address Details
        VBox details = new VBox();
        details.setAlignment(Pos.CENTER_LEFT);
        details.setSpacing(4);
        HBox.setHgrow(details, Priority.ALWAYS);

        // Label and Selected badge row
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.setSpacing(8);

        Label nameLabel = new Label(item.getLabel());
        nameLabel.getStyleClass().add("chat-name");

        titleRow.getChildren().add(nameLabel);

        // Show "Selected" badge if this is the currently selected address
        if (item.isSelected()) {
            Label selectedBadge = new Label("Selected");
            selectedBadge.setStyle(
                "-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #27AE60;"
                + "-fx-background-color: #E8F8F0; -fx-background-radius: 8px; -fx-padding: 2px 8px;"
            );
            titleRow.getChildren().add(selectedBadge);
        }

        Label addressLabel = new Label(item.getFullAddress());
        addressLabel.getStyleClass().add("chat-message");
        addressLabel.setWrapText(true);

        details.getChildren().addAll(titleRow, addressLabel);

        // Right arrow
        SVGPath arrow = new SVGPath();
        arrow.setContent("M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z");
        arrow.setFill(Color.web("#6F7E91"));
        arrow.setScaleX(0.8);
        arrow.setScaleY(0.8);

        row.getChildren().addAll(iconWrapper, details, arrow);

        // Click handler: Select this location
        row.setOnMouseClicked(event -> {
            selectLocation(item);
            event.consume();
        });

        return row;
    }

    private void selectLocation(LocationItem item) {
        // Update all saved addresses to not selected
        for (LocationItem saved : savedAddresses) {
            saved.setSelected(false);
        }
        // Mark this one as selected
        item.setSelected(true);

        // Update HomeUserController's location text via a static reference
        HomeUserController.setSelectedLocation(item.getShortAddress());

        // Re-render to reflect selection
        renderSavedAddresses(savedAddresses);

        // Save selected address to backend database asynchronously
        Thread t = new Thread(() -> {
            com.teknisio.service.UserService.updateProfile(java.util.Map.of("address", item.getFullAddress()));
        });
        t.setDaemon(true);
        t.start();

        // Show confirmation and navigate back
        showLocationSelectedAlert(item);
    }

    private void showLocationSelectedAlert(LocationItem item) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Location Updated");
        alert.setHeaderText(null);
        alert.setContentText("Location set to:\n" + item.getLabel() + " — " + item.getFullAddress());
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("alert-dialog");

        alert.setOnHidden(e -> {
            try {
                Main.setRoot(backRoute);
            } catch (IOException ex) {
                System.err.println("Failed to navigate back: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        alert.showAndWait();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Main.setRoot(backRoute);
        } catch (IOException e) {
            System.err.println("Failed to navigate back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUseCurrentLocation(ActionEvent event) {
        // Simulate GPS fetch and update backend
        String gpsAddr = "Jl. Gatot Subroto No. 88, Medan, North Sumatra";
        Thread t = new Thread(() -> {
            com.teknisio.service.UserService.updateProfile(java.util.Map.of("address", gpsAddr));
            javafx.application.Platform.runLater(() -> {
                HomeUserController.setSelectedLocation("GPS — Medan");
            });
        });
        t.setDaemon(true);
        t.start();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Current Location");
        alert.setHeaderText(null);
        alert.setContentText("Using your current GPS location.\nLat: 3.5952, Long: 98.6722\n(Medan, North Sumatra)");
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("alert-dialog");

        alert.setOnHidden(e -> {
            try {
                Main.setRoot(backRoute);
            } catch (IOException ex) {
                System.err.println("Failed to navigate back: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        alert.showAndWait();
    }

    /**
     * Inner class representing a location item.
     */
    public static class LocationItem {
        private String label;
        private String fullAddress;
        private String shortAddress;
        private boolean selected;

        public LocationItem(String label, String fullAddress, String shortAddress, boolean selected) {
            this.label = label;
            this.fullAddress = fullAddress;
            this.shortAddress = shortAddress;
            this.selected = selected;
        }

        public String getLabel() { return label; }
        public String getFullAddress() { return fullAddress; }
        public String getShortAddress() { return shortAddress; }
        public boolean isSelected() { return selected; }
        public void setSelected(boolean selected) { this.selected = selected; }
    }
}