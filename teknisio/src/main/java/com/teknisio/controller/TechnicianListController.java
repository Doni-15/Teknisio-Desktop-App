package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.dto.DeviceCategoryDto;
import com.teknisio.dto.TechnicianDto;
import com.teknisio.service.DeviceCategoryService;
import com.teknisio.service.TechnicianService;
import com.teknisio.util.ImageUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

public class TechnicianListController implements Initializable {

    // -- State --
    private static String selectedCategoryOnLoad = null; // null = no filter
    private static String selectedCategoryId = null;
    private static TechnicianDto selectedTechnicianDto = null;

    public static void setSelectedCategoryOnLoad(String category) { selectedCategoryOnLoad = category; }
    public static void setSelectedTechnicianDto(TechnicianDto dto) { selectedTechnicianDto = dto; }

    // -- FXML bindings for 4 cards --
    @FXML private HBox card1;
    @FXML private HBox card2;
    @FXML private HBox card3;
    @FXML private HBox card4;

    @FXML private ImageView avatar1;
    @FXML private ImageView avatar2;
    @FXML private ImageView avatar3;
    @FXML private ImageView avatar4;

    @FXML private HBox rating1;
    @FXML private HBox rating2;
    @FXML private HBox rating3;
    @FXML private HBox rating4;

    @FXML private CheckBox check1;
    @FXML private CheckBox check2;
    @FXML private CheckBox check3;
    @FXML private CheckBox check4;

    @FXML private Label tech1Name;
    @FXML private Label tech2Name;
    @FXML private Label tech3Name;
    @FXML private Label tech4Name;

    @FXML private Label tech1Price;
    @FXML private Label tech2Price;
    @FXML private Label tech3Price;
    @FXML private Label tech4Price;

    // -- Overlay & category selector --
    @FXML private VBox floatingOverlay;
    @FXML private Label sliderLabel;
    @FXML private SVGPath sliderIcon;
    @FXML private GridPane overlayCategoryGrid; // dynamic grid in FXML

    // Data
    private HBox[] cards;
    private ImageView[] avatars;
    private HBox[] ratings;
    private CheckBox[] checkboxes;
    private Label[] nameLabels;
    private Label[] priceLabels;

    private List<TechnicianDto> currentTechnicians = new ArrayList<>();
    private List<DeviceCategoryDto> allCategories = new ArrayList<>();
    private int selectedCardIndex = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cards = new HBox[]{card1, card2, card3, card4};
        avatars = new ImageView[]{avatar1, avatar2, avatar3, avatar4};
        ratings = new HBox[]{rating1, rating2, rating3, rating4};
        checkboxes = new CheckBox[]{check1, check2, check3, check4};
        nameLabels = new Label[]{tech1Name, tech2Name, tech3Name, tech4Name};
        priceLabels = new Label[]{tech1Price, tech2Price, tech3Price, tech4Price};

        applyAvatarClips();
        hideAllCards();

        if (sliderLabel != null) sliderLabel.setText("Memuat...");

        // Load categories + technicians from API asynchronously
        Thread t = new Thread(() -> {
            List<DeviceCategoryDto> cats = DeviceCategoryService.getActiveCategories();
            Platform.runLater(() -> {
                allCategories = cats;
                buildOverlayCategoryGrid(cats);

                // Determine which category to show first
                if (selectedCategoryOnLoad != null && !cats.isEmpty()) {
                    DeviceCategoryDto match = findCategoryByName(cats, selectedCategoryOnLoad);
                    if (match != null) {
                        applyCategory(match);
                    } else if (!cats.isEmpty()) {
                        applyCategory(cats.get(0));
                    }
                } else if (!cats.isEmpty()) {
                    applyCategory(cats.get(0));
                } else {
                    if (sliderLabel != null) sliderLabel.setText("Tidak ada kategori");
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private DeviceCategoryDto findCategoryByName(List<DeviceCategoryDto> cats, String name) {
        if (name == null) return null;
        String searchName = name.trim().toLowerCase();
        if (searchName.equals("ac")) {
            searchName = "air conditioner";
        } else if (searchName.equals("fridge") || searchName.equals("kulkas")) {
            searchName = "refrigerator";
        } else if (searchName.equals("tv") || searchName.equals("televisi")) {
            searchName = "television";
        } else if (searchName.equals("kipas")) {
            searchName = "fan";
        } else if (searchName.equals("mesin cuci")) {
            searchName = "washing machine";
        }

        for (DeviceCategoryDto c : cats) {
            if (c.getName() != null) {
                String catName = c.getName().toLowerCase();
                if (catName.equals(searchName) || catName.contains(searchName) || searchName.contains(catName)) {
                    return c;
                }
            }
        }
        return null;
    }

    /** Build the floating overlay grid dynamically from categories */
    private void buildOverlayCategoryGrid(List<DeviceCategoryDto> cats) {
        if (overlayCategoryGrid == null) return;
        overlayCategoryGrid.getChildren().clear();
        int col = 0, row = 0;
        for (DeviceCategoryDto cat : cats) {
            VBox item = createOverlayCategoryItem(cat);
            overlayCategoryGrid.add(item, col, row);
            col++;
            if (col >= 4) { col = 0; row++; }
        }
    }

    private VBox createOverlayCategoryItem(DeviceCategoryDto cat) {
        VBox item = new VBox();
        item.setAlignment(Pos.CENTER);
        item.setSpacing(4);
        item.setPrefWidth(72);
        item.getStyleClass().add("floating-category-item");
        item.setUserData(cat);

        SVGPath icon = new SVGPath();
        icon.setContent(HomeUserController.getCategoryIcon(cat.getName()));
        icon.setScaleX(0.7);
        icon.setScaleY(0.7);
        icon.setFill(Color.web("#2D4B73"));
        StackPane iconWrapper = new StackPane(icon);
        iconWrapper.getStyleClass().add("category-icon-circle-wrapper");
        iconWrapper.setMinSize(44, 44);
        iconWrapper.setMaxSize(44, 44);

        Label lbl = new Label(cat.getName());
        lbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #2D4B73; -fx-font-weight: bold;");
        lbl.setWrapText(true);
        lbl.setAlignment(Pos.CENTER);

        item.getChildren().addAll(iconWrapper, lbl);

        item.setOnMouseClicked(event -> {
            applyCategory(cat);
            if (floatingOverlay != null) floatingOverlay.setVisible(false);
        });

        return item;
    }

    private void applyCategory(DeviceCategoryDto cat) {
        selectedCategoryId = cat.getDeviceCategoryId();
        if (sliderLabel != null) sliderLabel.setText(cat.getName());
        if (sliderIcon != null) sliderIcon.setContent(HomeUserController.getCategoryIcon(cat.getName()));

        // Highlight selected in overlay
        if (overlayCategoryGrid != null) {
            for (javafx.scene.Node n : overlayCategoryGrid.getChildren()) {
                if (n instanceof VBox) {
                    VBox v = (VBox) n;
                    v.getStyleClass().remove("floating-category-item-selected");
                    if (cat.equals(v.getUserData())) {
                        v.getStyleClass().add("floating-category-item-selected");
                    }
                }
            }
        }

        // Load technicians for this category
        hideAllCards();
        if (sliderLabel != null) sliderLabel.setText(cat.getName() + " (Memuat...)");

        Thread t = new Thread(() -> {
            List<TechnicianDto> techs = TechnicianService.searchTechnicians(cat.getDeviceCategoryId());
            Platform.runLater(() -> {
                currentTechnicians = techs;
                if (sliderLabel != null) sliderLabel.setText(cat.getName());
                loadCategoryData(techs);
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private void loadCategoryData(List<TechnicianDto> technicians) {
        for (int i = 0; i < 4; i++) {
            if (i < technicians.size()) {
                TechnicianDto tech = technicians.get(i);
                cards[i].setVisible(true);
                cards[i].setManaged(true);
                nameLabels[i].setText(tech.getName());

                // Build price label from categories or use default
                String priceText = buildPriceDisplay(tech);
                priceLabels[i].setText(priceText);

                // Load base64 photo
                String photo = tech.getProfilePhoto();
                if (photo != null && !photo.isBlank()) {
                    ImageUtil.applyBase64ToImageView(avatars[i], photo);
                } else {
                    try {
                        avatars[i].setImage(new Image(getClass().getResource(
                            "/com/teknisio/assets/profile/profile.png").toExternalForm()));
                    } catch (Exception ignored) {}
                }

                populateStars(ratings[i], (int) Math.round(tech.getRatingDouble()));
            } else {
                cards[i].setVisible(false);
                cards[i].setManaged(false);
            }
        }

        applyAvatarClips();

        // Auto-select first card
        if (!technicians.isEmpty()) {
            selectCard(0);
        } else {
            deselectAllCards();
        }
    }

    private String buildPriceDisplay(TechnicianDto tech) {
        if (tech.getSupportedDeviceCategories() != null && !tech.getSupportedDeviceCategories().isEmpty()) {
            return tech.getSupportedDeviceCategories().get(0).getName() + " Specialist";
        }
        return "General Specialist";
    }

    private void applyAvatarClips() {
        for (ImageView av : avatars) {
            if (av != null) av.setClip(new Circle(30, 30, 30));
        }
    }

    private void hideAllCards() {
        for (HBox card : cards) {
            if (card != null) { card.setVisible(false); card.setManaged(false); }
        }
    }

    private void populateStars(HBox container, int rating) {
        if (container == null) return;
        container.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            SVGPath star = new SVGPath();
            star.setContent("M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
            star.setScaleX(0.4); star.setScaleY(0.4);
            star.setFill(i < rating ? Color.web("#F1C40F") : Color.web("#BDC3C7"));
            StackPane sw = new StackPane(star);
            sw.setMinSize(10, 10); sw.setPrefSize(10, 10); sw.setMaxSize(10, 10);
            sw.setAlignment(Pos.CENTER);
            container.getChildren().add(sw);
        }
    }

    // --- Overlay ---
    @FXML private void handleCategorySelectorClick(MouseEvent event) {
        if (floatingOverlay != null) floatingOverlay.setVisible(true);
    }

    @FXML private void handleCloseOverlay(ActionEvent event) {
        if (floatingOverlay != null) floatingOverlay.setVisible(false);
    }

    // --- Card Selection ---
    @FXML private void handleCheckbox1(ActionEvent event) { selectCard(0); }
    @FXML private void handleCheckbox2(ActionEvent event) { selectCard(1); }
    @FXML private void handleCheckbox3(ActionEvent event) { selectCard(2); }
    @FXML private void handleCheckbox4(ActionEvent event) { selectCard(3); }
    @FXML private void handleCard1Click(MouseEvent event) { selectCard(0); }
    @FXML private void handleCard2Click(MouseEvent event) { selectCard(1); }
    @FXML private void handleCard3Click(MouseEvent event) { selectCard(2); }
    @FXML private void handleCard4Click(MouseEvent event) { selectCard(3); }

    private void selectCard(int index) {
        deselectAllCards();
        selectedCardIndex = index;
        if (index < 0 || index >= cards.length || !cards[index].isVisible()) return;
        if (checkboxes[index] != null) checkboxes[index].setSelected(true);
        cards[index].getStyleClass().clear();
        cards[index].getStyleClass().add("tech-list-card-selected");
    }

    private void deselectAllCards() {
        selectedCardIndex = -1;
        for (int i = 0; i < 4; i++) {
            if (checkboxes[i] != null) checkboxes[i].setSelected(false);
            if (cards[i] != null) {
                cards[i].getStyleClass().clear();
                cards[i].getStyleClass().add("tech-list-card");
            }
        }
    }

    @FXML private void handleBack(ActionEvent event) {
        try { Main.setRoot("/com/teknisio/fxml/home_user.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleMeetTechnician(ActionEvent event) {
        if (selectedCardIndex >= 0 && selectedCardIndex < currentTechnicians.size()) {
            TechnicianDto tech = currentTechnicians.get(selectedCardIndex);
            // Pass selected technician to OrderTechnicianController
            OrderTechnicianController.setSelectedTechnician(tech);
            try { Main.setRoot("/com/teknisio/fxml/OrderTechnician.fxml"); }
            catch (IOException e) { e.printStackTrace(); }
        } else {
            // No card selected — inform user
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Pilih Teknisi");
            alert.setHeaderText(null);
            alert.setContentText("Silakan pilih satu teknisi terlebih dahulu.");
            try {
                alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
                alert.getDialogPane().getStyleClass().add("alert-dialog");
            } catch (Exception ignored) {}
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSelectCategory(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof VBox) {
            VBox item = (VBox) source;
            String categoryName = (String) item.getUserData();
            if (categoryName != null) {
                DeviceCategoryDto match = findCategoryByName(allCategories, categoryName);
                if (match != null) {
                    applyCategory(match);
                }
            }
        }
        if (floatingOverlay != null) {
            floatingOverlay.setVisible(false);
        }
    }

    /** Expose for HomeUserController to access the static selectedLocationText */
    static String selectedLocationText = null;
}
