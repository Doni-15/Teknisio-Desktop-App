package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.dto.CreateServiceRequestDto;
import com.teknisio.dto.DeviceCategoryDto;
import com.teknisio.dto.TechnicianDto;
import com.teknisio.service.ServiceRequestService;
import com.teknisio.service.SessionManager;
import com.teknisio.util.ImageUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrderTechnicianController implements Initializable {

    // ---- Static state: set before navigating to this screen ----
    private static TechnicianDto currentTechnician;

    public static void setSelectedTechnician(TechnicianDto tech) {
        currentTechnician = tech;
    }

    // ---- FXML bindings (fixed elements) ----
    @FXML private FlowPane categoryContainer;   // dynamic toggle buttons go here
    @FXML private TextArea descriptionField;
    @FXML private DatePicker datePicker;
    @FXML private ToggleButton timeMorning;
    @FXML private ToggleButton timeAfternoon;
    @FXML private ToggleButton timeEvening;
    @FXML private StackPane techImageWrapper;
    @FXML private ImageView techImageView;
    @FXML private Label techNameLabel;
    @FXML private HBox techRatingBox;
    @FXML private Label techDistanceLabel;
    @FXML private Label techPriceLabel;
    @FXML private HBox techBadgesBox;

    private javafx.scene.control.ToggleGroup timeGroup;
    private List<ToggleButton> categoryToggleButtons = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Crop technician headshot to circle
        if (techImageView != null) {
            Circle clip = new Circle(24, 24, 24);
            techImageView.setClip(clip);
        }

        // 2. Time group
        timeGroup = new javafx.scene.control.ToggleGroup();
        if (timeMorning != null) timeMorning.setToggleGroup(timeGroup);
        if (timeAfternoon != null) timeAfternoon.setToggleGroup(timeGroup);
        if (timeEvening != null) timeEvening.setToggleGroup(timeGroup);
        if (timeAfternoon != null) timeAfternoon.setSelected(true);

        // 3. Default date (tomorrow)
        if (datePicker != null) datePicker.setValue(LocalDate.now().plusDays(1));

        // 4. Fill technician details from selected technician
        loadTechnicianData();
    }

    private void loadTechnicianData() {
        TechnicianDto tech = currentTechnician;
        if (tech == null) return;

        // Name
        if (techNameLabel != null) techNameLabel.setText(tech.getName());

        // Rating stars
        if (techRatingBox != null) populateStars((int) Math.round(tech.getRatingDouble()));

        // Distance (placeholder, no GPS)
        if (techDistanceLabel != null) techDistanceLabel.setText("Location: ±2 Km");

        // Price range / job count
        if (techPriceLabel != null) {
            String jobsText = tech.getTotalJobs() != null ? tech.getTotalJobs() + " pekerjaan" : "—";
            techPriceLabel.setText("Total Pekerjaan: " + jobsText);
        }

        // Profile photo from base64
        if (techImageView != null) {
            String photo = tech.getProfilePhoto();
            if (photo != null && !photo.isBlank()) {
                ImageUtil.applyBase64ToImageView(techImageView, photo);
            } else {
                try {
                    techImageView.setImage(new Image(
                        getClass().getResource("/com/teknisio/assets/profile/profile.png").toExternalForm()));
                } catch (Exception ignored) {}
            }
        }

        // Badges
        if (techBadgesBox != null) populateBadges(tech);

        // Category toggles (all categories technician supports, multi-select)
        if (categoryContainer != null) buildCategoryToggles(tech);
    }

    private void buildCategoryToggles(TechnicianDto tech) {
        categoryContainer.getChildren().clear();
        categoryToggleButtons.clear();

        List<DeviceCategoryDto> cats = tech.getSupportedDeviceCategories();
        if (cats == null || cats.isEmpty()) return;

        for (DeviceCategoryDto cat : cats) {
            VBox tile = new VBox();
            tile.getStyleClass().add("category-toggle-tile");
            tile.setAlignment(Pos.CENTER);

            ToggleButton toggle = new ToggleButton();
            toggle.getStyleClass().add("category-toggle");
            toggle.setEllipsisString("");
            toggle.setUserData(cat.getDeviceCategoryId());

            ImageView icon = new ImageView();
            icon.setFitWidth(24);
            icon.setFitHeight(24);
            icon.setPreserveRatio(true);
            try {
                String path = getCategoryIconPath(cat.getName());
                icon.setImage(new Image(getClass().getResource(path).toExternalForm()));
            } catch (Exception ignored) {}

            javafx.scene.effect.ColorAdjust whiteAdjust = new javafx.scene.effect.ColorAdjust();
            whiteAdjust.setBrightness(1.0);

            toggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    icon.setEffect(whiteAdjust);
                } else {
                    icon.setEffect(null);
                }
            });

            // Initial selection state listener setup
            if (toggle.isSelected()) {
                icon.setEffect(whiteAdjust);
            } else {
                icon.setEffect(null);
            }

            toggle.setGraphic(icon);

            Label lbl = new Label(cat.getName());
            lbl.getStyleClass().add("category-toggle-label");

            tile.getChildren().addAll(toggle, lbl);
            categoryContainer.getChildren().add(tile);
            categoryToggleButtons.add(toggle);
        }

        // Select first by default if only one; or leave all unselected for multi-choice
        if (!categoryToggleButtons.isEmpty()) {
            categoryToggleButtons.get(0).setSelected(true);
            // Trigger selection effect update for the default selected item
            ImageView iv = (ImageView) categoryToggleButtons.get(0).getGraphic();
            if (iv != null) {
                javafx.scene.effect.ColorAdjust whiteAdjust = new javafx.scene.effect.ColorAdjust();
                whiteAdjust.setBrightness(1.0);
                iv.setEffect(whiteAdjust);
            }
        }
    }

    private String getCategoryIconPath(String categoryName) {
        if (categoryName == null) return "/com/teknisio/assets/devices/ac.png";
        switch (categoryName.toLowerCase().trim()) {
            case "ac":
            case "air conditioner":
                return "/com/teknisio/assets/devices/ac.png";
            case "fan":
            case "kipas":
                return "/com/teknisio/assets/devices/fan.png";
            case "mixer":
                return "/com/teknisio/assets/devices/mixer.png";
            case "oven":
                return "/com/teknisio/assets/devices/oven.png";
            case "fridge":
            case "refrigerator":
            case "kulkas":
                return "/com/teknisio/assets/devices/refrigerator.png";
            case "rice cooker":
                return "/com/teknisio/assets/devices/rice_cooker.png";
            case "tv":
            case "television":
            case "televisi":
                return "/com/teknisio/assets/devices/television.png";
            case "washing machine":
            case "mesin cuci":
                return "/com/teknisio/assets/devices/washing_machine.png";
            default:
                return "/com/teknisio/assets/devices/ac.png";
        }
    }

    private void populateStars(int rating) {
        if (techRatingBox == null) return;
        techRatingBox.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            SVGPath star = new SVGPath();
            star.setContent("M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
            star.setScaleX(0.45); star.setScaleY(0.45);
            star.setFill(i < rating ? Color.web("#F1C40F") : Color.web("#BDC3C7"));
            StackPane sw = new StackPane(star);
            sw.setMinSize(11, 11); sw.setPrefSize(11, 11); sw.setMaxSize(11, 11);
            sw.setAlignment(Pos.CENTER);
            techRatingBox.getChildren().add(sw);
        }
    }

    private void populateBadges(TechnicianDto tech) {
        techBadgesBox.getChildren().clear();
        if (tech.getSupportedDeviceCategories() == null) return;
        int count = 0;
        for (DeviceCategoryDto cat : tech.getSupportedDeviceCategories()) {
            if (count >= 3) break;
            StackPane badge = new StackPane();
            badge.getStyleClass().add("tech-badge");
            badge.setMinSize(20, 20);
            badge.setMaxSize(20, 20);
            badge.setAlignment(Pos.CENTER);

            ImageView icon = new ImageView();
            icon.setFitWidth(13);
            icon.setFitHeight(13);
            icon.setPreserveRatio(true);
            try {
                String path = getCategoryIconPath(cat.getName());
                icon.setImage(new Image(getClass().getResource(path).toExternalForm()));
            } catch (Exception ignored) {}

            badge.getChildren().add(icon);
            techBadgesBox.getChildren().add(badge);
            count++;
        }
    }

    @FXML private void handleBack(ActionEvent event) { navigateBack(); }

    @FXML
    private void handleConfirmOrder(ActionEvent event) {
        // Gather selected categories (multi-select)
        List<String> selectedCatIds = new ArrayList<>();
        for (ToggleButton tb : categoryToggleButtons) {
            if (tb.isSelected() && tb.getUserData() != null) {
                selectedCatIds.add(tb.getUserData().toString());
            }
        }

        if (selectedCatIds.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Pilih minimal satu kategori perbaikan.");
            return;
        }

        String description = descriptionField != null ? descriptionField.getText().trim() : "";
        if (description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Harap deskripsikan kerusakan yang perlu diperbaiki.");
            return;
        }

        if (datePicker != null && datePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Pilih tanggal jadwal kunjungan.");
            return;
        }

        if (timeGroup.getSelectedToggle() == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Pilih waktu kunjungan (pagi/siang/malam).");
            return;
        }

        if (currentTechnician == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Tidak ada teknisi yang dipilih.");
            return;
        }

        // Build time note
        ToggleButton selectedTime = (ToggleButton) timeGroup.getSelectedToggle();
        String timeNote = selectedTime != null ? ((VBox) selectedTime.getGraphic() != null
                ? getTimeLabel(selectedTime) : "") : "";

        String address = SessionManager.getAddress();
        if (address == null || address.isBlank()) address = "Alamat belum diatur";

        String addressDetail = "Jadwal: " + datePicker.getValue() + " " + timeNote;

        Double latitude = SessionManager.getLatitude();
        Double longitude = SessionManager.getLongitude();
        if (latitude == null || longitude == null) {
            try {
                com.teknisio.util.GeoLocationUtil.LocationResult loc = com.teknisio.util.GeoLocationUtil.fetchLocation();
                latitude = loc.lat;
                longitude = loc.lon;
                SessionManager.setCoordinates(latitude, longitude);
            } catch (Exception e) {
                latitude = 3.5952;
                longitude = 98.6722;
            }
        }

        CreateServiceRequestDto request = new CreateServiceRequestDto(
            currentTechnician.getTechnicianProfileId(),
            selectedCatIds,
            description,
            address,
            addressDetail,
            latitude,
            longitude
        );

        // Disable confirm button during submission
        // Submit in background thread
        final String techName = currentTechnician.getName();
        final String schedDate = datePicker.getValue().toString();

        Thread t = new Thread(() -> {
            try {
                ServiceRequestService.createServiceRequest(request);
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Order Berhasil",
                        "Order berhasil dibuat!\nTeknisi " + techName + " telah dihubungi.\nJadwal: " + schedDate);
                    navigateBack();
                });
            } catch (IOException | InterruptedException e) {
                String msg = e.getMessage();
                Platform.runLater(() ->
                    showAlert(Alert.AlertType.ERROR, "Gagal Membuat Order", msg != null ? msg : "Terjadi kesalahan."));
                Thread.currentThread().interrupt();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private String getTimeLabel(ToggleButton tb) {
        if (tb == timeMorning) return "(08:00 - 11:00)";
        if (tb == timeAfternoon) return "(12:00 - 15:00)";
        if (tb == timeEvening) return "(16:00 - 18:00)";
        return "";
    }

    private void navigateBack() {
        try { Main.setRoot("/com/teknisio/fxml/home_user.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        try {
            alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("alert-dialog");
        } catch (Exception ignored) {}
        alert.showAndWait();
    }
}
