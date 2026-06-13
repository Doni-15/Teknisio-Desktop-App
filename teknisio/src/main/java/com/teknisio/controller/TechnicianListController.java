package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.model.Technician;
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

    private static String selectedCategoryOnLoad = "AC";

    public static void setSelectedCategoryOnLoad(String category) {
        selectedCategoryOnLoad = category;
    }

    @FXML
    private HBox card1;
    @FXML
    private HBox card2;
    @FXML
    private HBox card3;
    @FXML
    private HBox card4;

    @FXML
    private ImageView avatar1;
    @FXML
    private ImageView avatar2;
    @FXML
    private ImageView avatar3;
    @FXML
    private ImageView avatar4;

    @FXML
    private HBox rating1;
    @FXML
    private HBox rating2;
    @FXML
    private HBox rating3;
    @FXML
    private HBox rating4;

    @FXML
    private CheckBox check1;
    @FXML
    private CheckBox check2;
    @FXML
    private CheckBox check3;
    @FXML
    private CheckBox check4;

    // Names
    @FXML
    private Label tech1Name;
    @FXML
    private Label tech2Name;
    @FXML
    private Label tech3Name;
    @FXML
    private Label tech4Name;

    // Prices
    @FXML
    private Label tech1Price;
    @FXML
    private Label tech2Price;
    @FXML
    private Label tech3Price;
    @FXML
    private Label tech4Price;

    // Overlay Elements
    @FXML
    private VBox floatingOverlay;
    @FXML
    private Label sliderLabel;
    @FXML
    private SVGPath sliderIcon;

    // Overlay grid items
    @FXML
    private VBox overlayAC;
    @FXML
    private VBox overlayFridge;
    @FXML
    private VBox overlayWashing;
    @FXML
    private VBox overlayOven;
    @FXML
    private VBox overlayTV;
    @FXML
    private VBox overlayFan;
    @FXML
    private VBox overlayCamera;
    @FXML
    private VBox overlayMixer;

    private List<Technician> allTechnicians = new ArrayList<>();
    private HBox[] cards;
    private ImageView[] avatars;
    private HBox[] ratings;
    private CheckBox[] checkboxes;
    private Label[] nameLabels;
    private Label[] priceLabels;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize template arrays
        cards = new HBox[] { card1, card2, card3, card4 };
        avatars = new ImageView[] { avatar1, avatar2, avatar3, avatar4 };
        ratings = new HBox[] { rating1, rating2, rating3, rating4 };
        checkboxes = new CheckBox[] { check1, check2, check3, check4 };
        nameLabels = new Label[] { tech1Name, tech2Name, tech3Name, tech4Name };
        priceLabels = new Label[] { tech1Price, tech2Price, tech3Price, tech4Price };

        // Crop avatars to circles
        applyAvatarClips();

        // Load mock database
        initTechniciansData();

        // Apply pre-selected category
        applyCategory(selectedCategoryOnLoad);
    }

    private void initTechniciansData() {
        allTechnicians.clear();
        
        // AC Specialists
        allTechnicians.add(new Technician("Ahmed Rush", "AC Specialist", 5.0, "Rp50.000 - Rp200.000", "/com/teknisio/assets/technicians/tech_ahmed.png", false));
        allTechnicians.add(new Technician("Evan Bran", "AC Specialist", 4.0, "Rp75.000 - Rp270.000", "/com/teknisio/assets/technicians/tech_devon.png", false));
        allTechnicians.add(new Technician("Irvan Bran", "AC Specialist", 3.0, "Rp50.000 - Rp180.000", "/com/teknisio/assets/technicians/tech_devon.png", false));
        
        // Fridge Specialists
        allTechnicians.add(new Technician("Ben Adams", "Fridge Specialist", 5.0, "Rp45.000 - Rp180.000", "/com/teknisio/assets/technicians/tech_devon.png", false));
        allTechnicians.add(new Technician("Charlie Hugh", "Fridge Specialist", 4.0, "Rp40.000 - Rp150.000", "/com/teknisio/assets/technicians/tech_cedric.png", false));
        allTechnicians.add(new Technician("Ahmed Rush", "Fridge Specialist", 5.0, "Rp50.000 - Rp200.000", "/com/teknisio/assets/technicians/tech_ahmed.png", false));
        
        // Washing Machine Specialists
        allTechnicians.add(new Technician("Hendra Wijaya", "Washing Machine Specialist", 4.0, "Rp45.000 - Rp190.000", "/com/teknisio/assets/technicians/tech_ahmed.png", false));
        allTechnicians.add(new Technician("Ben Adams", "Washing Machine Specialist", 5.0, "Rp45.000 - Rp180.000", "/com/teknisio/assets/technicians/tech_devon.png", false));
        allTechnicians.add(new Technician("Charlie Hugh", "Washing Machine Specialist", 4.0, "Rp40.000 - Rp150.000", "/com/teknisio/assets/technicians/tech_cedric.png", false));
        
        // Filter Camera Specialists
        allTechnicians.add(new Technician("Fajar Siddik", "Filter Camera Specialist", 5.0, "Rp60.000 - Rp220.000", "/com/teknisio/assets/technicians/tech_cedric.png", false));
        allTechnicians.add(new Technician("Ahmed Rush", "Filter Camera Specialist", 5.0, "Rp50.000 - Rp200.000", "/com/teknisio/assets/technicians/tech_ahmed.png", false));
        allTechnicians.add(new Technician("Evan Bran", "Filter Camera Specialist", 4.0, "Rp75.000 - Rp270.000", "/com/teknisio/assets/technicians/tech_devon.png", false));
        
        // Oven Specialists
        allTechnicians.add(new Technician("Charlie Hugh", "Oven Specialist", 4.0, "Rp40.000 - Rp150.000", "/com/teknisio/assets/technicians/tech_cedric.png", false));
        allTechnicians.add(new Technician("Devon Lores", "Oven Specialist", 3.0, "Rp45.000 - Rp180.000", "/com/teknisio/assets/technicians/tech_devon.png", false));
        allTechnicians.add(new Technician("Ben Adams", "Oven Specialist", 5.0, "Rp45.000 - Rp180.000", "/com/teknisio/assets/technicians/tech_devon.png", false));
        
        // Television Specialists
        allTechnicians.add(new Technician("Irvan Bran", "Television Specialist", 4.0, "Rp55.000 - Rp210.000", "/com/teknisio/assets/technicians/tech_devon.png", false));
        allTechnicians.add(new Technician("Ahmed Rush", "Television Specialist", 5.0, "Rp50.000 - Rp200.000", "/com/teknisio/assets/technicians/tech_ahmed.png", false));
        allTechnicians.add(new Technician("Hendra Wijaya", "Television Specialist", 4.0, "Rp45.000 - Rp190.000", "/com/teknisio/assets/technicians/tech_ahmed.png", false));
        
        // Fan Specialists
        allTechnicians.add(new Technician("Rian Kusuma", "Fan Specialist", 5.0, "Rp35.000 - Rp120.000", "/com/teknisio/assets/technicians/tech_cedric.png", false));
        allTechnicians.add(new Technician("Evan Bran", "Fan Specialist", 4.0, "Rp75.000 - Rp270.000", "/com/teknisio/assets/technicians/tech_devon.png", false));
        allTechnicians.add(new Technician("Charlie Hugh", "Fan Specialist", 4.0, "Rp40.000 - Rp150.000", "/com/teknisio/assets/technicians/tech_cedric.png", false));
        
        // Mixer Specialists
        allTechnicians.add(new Technician("Budi Santoso", "Mixer Specialist", 4.0, "Rp30.000 - Rp110.000", "/com/teknisio/assets/technicians/tech_devon.png", false));
        allTechnicians.add(new Technician("Devon Lores", "Mixer Specialist", 3.0, "Rp45.000 - Rp180.000", "/com/teknisio/assets/technicians/tech_devon.png", false));
        allTechnicians.add(new Technician("Ben Adams", "Mixer Specialist", 5.0, "Rp45.000 - Rp180.000", "/com/teknisio/assets/technicians/tech_devon.png", false));
    }

    private void applyAvatarClips() {
        avatar1.setClip(new Circle(30, 30, 30));
        avatar2.setClip(new Circle(30, 30, 30));
        avatar3.setClip(new Circle(30, 30, 30));
        avatar4.setClip(new Circle(30, 30, 30));
    }

    private void populateAllStars() {
        // Not used now, ratings populated per card dynamically in loadCategoryData
    }

    private void populateStars(HBox container, int rating) {
        container.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            SVGPath star = new SVGPath();
            star.setContent("M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
            star.setScaleX(0.4);
            star.setScaleY(0.4);

            if (i < rating) {
                star.setContent("M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
                star.setFill(Color.web("#F1C40F")); // Gold
            } else {
                star.setFill(Color.web("#BDC3C7")); // Gray
            }

            StackPane starWrapper = new StackPane(star);
            starWrapper.setMinSize(10, 10);
            starWrapper.setPrefSize(10, 10);
            starWrapper.setMaxSize(10, 10);
            starWrapper.setAlignment(Pos.CENTER);
            container.getChildren().add(starWrapper);
        }
    }

    // --- Overlay Toggle Handlers ---
    @FXML
    private void handleCategorySelectorClick(MouseEvent event) {
        floatingOverlay.setVisible(true);
    }

    @FXML
    private void handleCloseOverlay(ActionEvent event) {
        floatingOverlay.setVisible(false);
    }

    @FXML
    private void handleSelectCategory(MouseEvent event) {
        VBox selectedBox = (VBox) event.getSource();
        String category = (String) selectedBox.getUserData();
        applyCategory(category);
        floatingOverlay.setVisible(false);
    }

    private void applyCategory(String category) {
        // 1. Update active category indicator label
        sliderLabel.setText(category);

        // 2. Clear highlighting on all grid overlay items, set selected one
        clearOverlaySelection();
        VBox selectedBox = getOverlayBoxForCategory(category);
        if (selectedBox != null) {
            selectedBox.getStyleClass().add("floating-category-item-selected");
        }

        // 3. Swap SVG path for category icon on slider button
        String acPath = "M19 19H5V5h14v14M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2m-1 7h-2V8h2v2m-3 0h-2V8h2v2m-3 0H8V8h2v2m-2 2h8v-2H8v2z";
        String fridgePath = "M19 2H5c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 8H7V5h10v5zm0 10H7v-8h10v8z";
        String washingPath = "M17 1H7c-1.1 0-2 .9-2 2v18c0 1.1.9 2 2 2h10c1.1 0 2-.9 2-2V3c0-1.1-.9-2-2-2zm-5 18c-2.21 0-4-1.79-4-4s1.79-4 4-4 4 1.79 4 4-1.79 4-4 4zm0-6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z";
        String ovenPath = "M21 4H3c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h18c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm-1 12H4V8h16v8zm-2-6h-4v2h4v-2z";
        String tvPath = "M21 3H3c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h5v2h8v-2h5c1.1 0 1.99-.9 1.99-2L23 5c0-1.1-.9-2-2-2zm0 14H3V5h18v12z";
        String fanPath = "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm0-10c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z";
        String cameraPath = "M12 2L1 21h22L12 2zm0 4l7.5 13h-15L12 6zm-1 8h2v2h-2v-2zm0-4h2v2h-2v-2z";
        String mixerPath = "M18 4H6v2h12V4zm-2 4H8v2h8V8zm-1 4H9v6c0 1.66 1.34 3 3 3s3-1.34 3-3v-6z";

        if (category.equals("AC")) {
            sliderIcon.setContent(acPath);
        } else if (category.equals("Fridge")) {
            sliderIcon.setContent(fridgePath);
        } else if (category.equals("Washing Machine")) {
            sliderIcon.setContent(washingPath);
        } else if (category.equals("Oven")) {
            sliderIcon.setContent(ovenPath);
        } else if (category.equals("Television") || category.equals("TV")) {
            sliderIcon.setContent(tvPath);
        } else if (category.equals("Fan")) {
            sliderIcon.setContent(fanPath);
        } else if (category.equals("Filter Camera")) {
            sliderIcon.setContent(cameraPath);
        } else if (category.equals("Mixer")) {
            sliderIcon.setContent(mixerPath);
        }

        loadCategoryData(category);
    }

    private VBox getOverlayBoxForCategory(String category) {
        if (category.equals("AC")) return overlayAC;
        if (category.equals("Fridge")) return overlayFridge;
        if (category.equals("Washing Machine")) return overlayWashing;
        if (category.equals("Oven")) return overlayOven;
        if (category.equals("Television") || category.equals("TV")) return overlayTV;
        if (category.equals("Fan")) return overlayFan;
        if (category.equals("Filter Camera")) return overlayCamera;
        if (category.equals("Mixer")) return overlayMixer;
        return null;
    }

    private void clearOverlaySelection() {
        if (overlayAC != null) overlayAC.getStyleClass().remove("floating-category-item-selected");
        if (overlayFridge != null) overlayFridge.getStyleClass().remove("floating-category-item-selected");
        if (overlayWashing != null) overlayWashing.getStyleClass().remove("floating-category-item-selected");
        if (overlayOven != null) overlayOven.getStyleClass().remove("floating-category-item-selected");
        if (overlayTV != null) overlayTV.getStyleClass().remove("floating-category-item-selected");
        if (overlayFan != null) overlayFan.getStyleClass().remove("floating-category-item-selected");
        if (overlayCamera != null) overlayCamera.getStyleClass().remove("floating-category-item-selected");
        if (overlayMixer != null) overlayMixer.getStyleClass().remove("floating-category-item-selected");
    }

    private boolean matchesCategory(Technician tech, String category) {
        String spec = tech.getSpecialization().toLowerCase();
        String cat = category.toLowerCase();
        if (cat.equals("tv")) cat = "television";
        if (cat.equals("washing")) cat = "washing machine";
        return spec.contains(cat) || cat.contains(spec);
    }

    private void loadCategoryData(String category) {
        List<Technician> filtered = new ArrayList<>();
        for (Technician tech : allTechnicians) {
            if (matchesCategory(tech, category)) {
                filtered.add(tech);
            }
        }

        // Populate cards dynamically
        for (int i = 0; i < 4; i++) {
            if (i < filtered.size()) {
                Technician tech = filtered.get(i);
                cards[i].setVisible(true);
                cards[i].setManaged(true);
                nameLabels[i].setText(tech.getName());
                priceLabels[i].setText(tech.getPriceRange());

                try {
                    avatars[i].setImage(new Image(getClass().getResource(tech.getAvatarPath()).toExternalForm()));
                } catch (Exception e) {
                    System.err.println("Error loading avatar for " + tech.getName() + ": " + e.getMessage());
                }

                populateStars(ratings[i], (int) tech.getRating());
            } else {
                cards[i].setVisible(false);
                cards[i].setManaged(false);
            }
        }

        applyAvatarClips();

        // Select the first card if available
        if (!filtered.isEmpty()) {
            selectCard(1);
        } else {
            deselectAllCards();
        }
    }

    // --- Checkbox Click Handlers ---
    @FXML
    private void handleCheckbox1(ActionEvent event) {
        selectCard(1);
    }

    @FXML
    private void handleCheckbox2(ActionEvent event) {
        selectCard(2);
    }

    @FXML
    private void handleCheckbox3(ActionEvent event) {
        selectCard(3);
    }

    @FXML
    private void handleCheckbox4(ActionEvent event) {
        selectCard(4);
    }

    // --- HBox Card Mouse Click Handlers ---
    @FXML
    private void handleCard1Click(MouseEvent event) {
        selectCard(1);
    }

    @FXML
    private void handleCard2Click(MouseEvent event) {
        selectCard(2);
    }

    @FXML
    private void handleCard3Click(MouseEvent event) {
        selectCard(3);
    }

    @FXML
    private void handleCard4Click(MouseEvent event) {
        selectCard(4);
    }

    private void deselectAllCards() {
        check1.setSelected(false);
        check2.setSelected(false);
        check3.setSelected(false);
        check4.setSelected(false);

        card1.getStyleClass().clear();
        card1.getStyleClass().add("tech-list-card");
        card2.getStyleClass().clear();
        card2.getStyleClass().add("tech-list-card");
        card3.getStyleClass().clear();
        card3.getStyleClass().add("tech-list-card");
        card4.getStyleClass().clear();
        card4.getStyleClass().add("tech-list-card");
    }

    private void selectCard(int index) {
        // Deselect all
        deselectAllCards();

        // Select chosen one
        switch (index) {
            case 1:
                check1.setSelected(true);
                card1.getStyleClass().clear();
                card1.getStyleClass().add("tech-list-card-selected");
                break;
            case 2:
                check2.setSelected(true);
                card2.getStyleClass().clear();
                card2.getStyleClass().add("tech-list-card-selected");
                break;
            case 3:
                check3.setSelected(true);
                card3.getStyleClass().clear();
                card3.getStyleClass().add("tech-list-card-selected");
                break;
            case 4:
                check4.setSelected(true);
                card4.getStyleClass().clear();
                card4.getStyleClass().add("tech-list-card-selected");
                break;
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

    @FXML
    private void handleMeetTechnician(ActionEvent event) {
        try {
            // Transitions to booking order screen
            Main.setRoot("/com/teknisio/fxml/OrderTechnician.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to order screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
