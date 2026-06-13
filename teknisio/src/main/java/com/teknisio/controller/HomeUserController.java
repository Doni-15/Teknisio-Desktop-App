package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.dto.TechnicianDto;
import com.teknisio.service.SessionManager;
import com.teknisio.service.TechnicianService;
import com.teknisio.util.ImageUtil;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeUserController implements Initializable {

    @FXML private StackPane profileImageWrapper;
    @FXML private ImageView profileImageView;
    @FXML private StackPane carouselContainer;
    @FXML private VBox newsContainer;
    @FXML private Label locationValueLabel;
    @FXML private Label greetingLabel;

    private static String selectedLocationText = "Intertip St., Medan";

    public static void setSelectedLocation(String location) {
        selectedLocationText = location;
    }

    /** Currently selected technician for order screen. */
    private static TechnicianDto selectedTechnician = null;
    public static TechnicianDto getSelectedTechnician() { return selectedTechnician; }

    private List<TechnicianDto> technicians = new ArrayList<>();
    private int currentIndex = 0;
    private List<StackPane> techCards = new ArrayList<>();
    private Timeline autoPlayTimeline;
    private double dragStartX;
    private static final double SWIPE_THRESHOLD = 50.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Restore location label from session or selected text
        if (locationValueLabel != null) {
            String address = SessionManager.getAddress();
            if (address != null && !address.isBlank()) {
                String shortAddr = address;
                if (address.contains(",")) {
                    shortAddr = address.split(",")[0].trim();
                }
                locationValueLabel.setText(shortAddr);
                selectedLocationText = shortAddr;
            } else {
                locationValueLabel.setText(selectedLocationText);
            }
        }

        // Set greeting from session
        if (greetingLabel != null) {
            String name = SessionManager.getName();
            greetingLabel.setText("Hello, " + (name != null ? name.split(" ")[0] : "User") + "!");
        }

        // Apply circular crop on profile image
        if (profileImageView != null) {
            Circle profileClip = new Circle(21, 21, 21);
            profileImageView.setClip(profileClip);
            // Load base64 photo
            String photo = SessionManager.getProfilePhoto();
            if (photo != null && !photo.isBlank()) {
                ImageUtil.applyBase64ToImageView(profileImageView, photo);
            }
        }

        // Load technicians from backend asynchronously
        loadTechniciansFromApi();
    }

    private void loadTechniciansFromApi() {
        Thread thread = new Thread(() -> {
            List<TechnicianDto> result = TechnicianService.searchTechnicians(null);
            Platform.runLater(() -> {
                technicians = result;
                if (!technicians.isEmpty()) {
                    // Start centered on the most-rated technician
                    currentIndex = findFeaturedIndex(technicians);
                    renderTechnicians();
                    setupSwipeSupport(carouselContainer);
                    startAutoPlay();
                } else {
                    // Show empty state in carousel
                    showEmptyCarousel();
                }
                // Also render news (static for now)
                renderNews();
            });
        });
        thread.setDaemon(true);
        thread.start();
    }

    private int findFeaturedIndex(List<TechnicianDto> list) {
        int best = 0;
        double bestRating = -1;
        for (int i = 0; i < list.size(); i++) {
            double r = list.get(i).getRatingDouble();
            if (r > bestRating) { bestRating = r; best = i; }
        }
        return best;
    }

    private void showEmptyCarousel() {
        if (carouselContainer == null) return;
        carouselContainer.getChildren().clear();
        Label empty = new Label("Belum ada teknisi tersedia");
        empty.setStyle("-fx-font-size: 13px; -fx-text-fill: #6F7E91;");
        carouselContainer.getChildren().add(empty);
    }

    private void renderTechnicians() {
        if (carouselContainer == null) return;
        carouselContainer.getChildren().clear();
        techCards.clear();

        for (int index = 0; index < technicians.size(); index++) {
            final int i = index;
            TechnicianDto tech = technicians.get(i);
            StackPane card = new StackPane();
            card.getStyleClass().add("tech-card");

            double cardWidth = 160;
            double cardHeight = 194;

            // Avatar ImageView
            ImageView avatar = new ImageView();
            avatar.setFitWidth(cardWidth);
            avatar.setFitHeight(cardHeight);
            avatar.setPickOnBounds(true);
            avatar.setPreserveRatio(false);

            // Load base64 profile photo
            String photo = tech.getProfilePhoto();
            if (photo != null && !photo.isBlank()) {
                Image img = ImageUtil.imageFromBase64(photo);
                if (img != null) avatar.setImage(img);
            } else {
                // Fallback placeholder avatar image
                try {
                    avatar.setImage(new Image(getClass().getResource("/com/teknisio/assets/profile/profile.png").toExternalForm()));
                } catch (Exception ignored) {}
            }

            Rectangle imgClip = new Rectangle(cardWidth, cardHeight);
            imgClip.setArcWidth(32);
            imgClip.setArcHeight(32);
            avatar.setClip(imgClip);

            // Gradient overlay
            Region gradientOverlay = new Region();
            gradientOverlay.getStyleClass().add("tech-gradient-overlay");
            gradientOverlay.setPrefSize(cardWidth, cardHeight);
            gradientOverlay.setMaxSize(cardWidth, cardHeight);

            // Content pane
            BorderPane contentPane = new BorderPane();
            contentPane.setPrefSize(cardWidth, cardHeight);
            contentPane.setMaxSize(cardWidth, cardHeight);
            contentPane.setPadding(new Insets(12));

            // TOP: Stars + Distance
            HBox topBox = new HBox();
            topBox.setAlignment(Pos.CENTER_LEFT);
            HBox ratingBox = buildStarsBox(tech.getRatingDouble());
            Region spacerTop = new Region();
            HBox.setHgrow(spacerTop, Priority.ALWAYS);
            Label distanceLabel = new Label("±2 Km");
            distanceLabel.getStyleClass().add("tech-distance");
            topBox.getChildren().addAll(ratingBox, spacerTop, distanceLabel);
            contentPane.setTop(topBox);

            // BOTTOM: Name, price, badges, more info
            VBox bottomBox = new VBox();
            bottomBox.setSpacing(4);
            bottomBox.setAlignment(Pos.BOTTOM_LEFT);

            Label nameLabel = new Label(tech.getName());
            nameLabel.getStyleClass().add("tech-name");
            bottomBox.getChildren().add(nameLabel);

            // Specialization label
            String spec = buildSpecializationLabel(tech);
            Label specLabel = new Label(spec);
            specLabel.setStyle("-fx-text-fill: #E2ECF7; -fx-font-size: 8px; -fx-font-weight: bold;");
            bottomBox.getChildren().add(specLabel);

            HBox bottomRow = new HBox();
            bottomRow.setAlignment(Pos.CENTER_LEFT);
            bottomRow.setSpacing(6);

            HBox badgesBox = buildBadgesBox(tech);
            Region spacerBottom = new Region();
            HBox.setHgrow(spacerBottom, Priority.ALWAYS);
            Label moreInfoBtn = new Label("More Info >");
            moreInfoBtn.getStyleClass().add("tech-more-info-btn");
            bottomRow.getChildren().addAll(badgesBox, spacerBottom, moreInfoBtn);
            bottomBox.getChildren().add(bottomRow);
            contentPane.setBottom(bottomBox);

            card.getChildren().addAll(avatar, gradientOverlay, contentPane);

            // Click listener
            card.setOnMouseClicked(event -> {
                if (i == currentIndex) {
                    selectedTechnician = tech;
                    TechnicianListController.setSelectedTechnicianDto(tech);
                    try {
                        Main.setRoot("/com/teknisio/fxml/OrderTechnician.fxml");
                    } catch (IOException e) {
                        System.err.println("Failed to load OrderTechnician: " + e.getMessage());
                    }
                } else {
                    currentIndex = i;
                    updateCarousel();
                    resetAutoPlay();
                }
                event.consume();
            });

            techCards.add(card);
            carouselContainer.getChildren().add(card);
        }

        updateCarousel();
    }

    private HBox buildStarsBox(double rating) {
        HBox box = new HBox();
        box.setSpacing(1);
        box.setAlignment(Pos.CENTER_LEFT);
        int fullStars = (int) Math.round(rating);
        for (int k = 0; k < 5; k++) {
            SVGPath star = new SVGPath();
            star.setContent("M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
            star.setScaleX(0.4);
            star.setScaleY(0.4);
            star.setFill(k < fullStars ? Color.web("#F1C40F") : Color.web("rgba(255,255,255,0.4)"));
            StackPane sw = new StackPane(star);
            sw.setMinSize(10, 10);
            sw.setPrefSize(10, 10);
            sw.setMaxSize(10, 10);
            sw.setAlignment(Pos.CENTER);
            box.getChildren().add(sw);
        }
        return box;
    }

    private String buildSpecializationLabel(TechnicianDto tech) {
        if (tech.getSupportedDeviceCategories() != null && !tech.getSupportedDeviceCategories().isEmpty()) {
            return tech.getSupportedDeviceCategories().get(0).getName() + " Specialist";
        }
        return "General Specialist";
    }

    private HBox buildBadgesBox(TechnicianDto tech) {
        HBox box = new HBox();
        box.setSpacing(4);
        box.setAlignment(Pos.CENTER_LEFT);
        if (tech.getSupportedDeviceCategories() != null) {
            int count = 0;
            for (var cat : tech.getSupportedDeviceCategories()) {
                if (count >= 2) break;
                String svg = getCategoryIcon(cat.getName());
                StackPane badge = createBadge(svg);
                box.getChildren().add(badge);
                count++;
            }
        }
        return box;
    }

    private void updateCarousel() {
        int n = technicians.size();
        if (n == 0) return;

        for (int i = 0; i < n; i++) {
            StackPane card = techCards.get(i);
            int offset = i - currentIndex;
            if (offset < -n / 2) offset += n;
            else if (offset > n / 2) offset -= n;

            if (offset == 0) {
                card.setVisible(true); card.setDisable(false);
                setCardActiveState(card, true);
                animateCard(card, 0, 1.0, 1.0, true);
            } else if (offset == -1) {
                card.setVisible(true); card.setDisable(false);
                setCardActiveState(card, false);
                animateCard(card, -100, 0.8, 0.7, false);
            } else if (offset == 1) {
                card.setVisible(true); card.setDisable(false);
                setCardActiveState(card, false);
                animateCard(card, 100, 0.8, 0.7, false);
            } else {
                card.setVisible(false);
            }
        }
    }

    private void animateCard(StackPane card, double targetX, double targetScale, double targetOpacity, boolean isCenter) {
        if (isCenter) card.toFront();
        TranslateTransition tt = new TranslateTransition(Duration.millis(350), card);
        tt.setToX(targetX);
        ScaleTransition st = new ScaleTransition(Duration.millis(350), card);
        st.setToX(targetScale); st.setToY(targetScale);
        FadeTransition ft = new FadeTransition(Duration.millis(350), card);
        ft.setToValue(targetOpacity);
        new ParallelTransition(tt, st, ft).play();
    }

    private void setCardActiveState(StackPane card, boolean isActive) {
        card.getStyleClass().clear();
        card.getStyleClass().add(isActive ? "tech-card-active" : "tech-card");
        if (card.getChildren().size() < 3) return;
        BorderPane contentPane = (BorderPane) card.getChildren().get(2);
        if (contentPane.getTop() != null) {
            contentPane.getTop().setVisible(isActive);
            contentPane.getTop().setManaged(isActive);
        }
        VBox bottomBox = (VBox) contentPane.getBottom();
        if (bottomBox != null && bottomBox.getChildren().size() >= 3) {
            bottomBox.getChildren().get(1).setVisible(isActive);
            bottomBox.getChildren().get(1).setManaged(isActive);
            HBox bottomRow = (HBox) bottomBox.getChildren().get(2);
            if (bottomRow.getChildren().size() >= 2) {
                bottomRow.getChildren().get(1).setVisible(isActive);
                bottomRow.getChildren().get(1).setManaged(isActive);
            }
        }
    }

    private void startAutoPlay() {
        if (autoPlayTimeline != null) autoPlayTimeline.stop();
        autoPlayTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> slideNext()));
        autoPlayTimeline.setCycleCount(Timeline.INDEFINITE);
        autoPlayTimeline.play();
    }

    private void resetAutoPlay() {
        if (autoPlayTimeline != null) autoPlayTimeline.playFromStart();
    }

    private void slideNext() {
        int n = technicians.size();
        if (n == 0) return;
        currentIndex = (currentIndex + 1) % n;
        updateCarousel();
    }

    private void slidePrev() {
        int n = technicians.size();
        if (n == 0) return;
        currentIndex = (currentIndex - 1 + n) % n;
        updateCarousel();
    }

    private void setupSwipeSupport(StackPane container) {
        container.setOnMousePressed(event -> {
            dragStartX = event.getX();
            if (autoPlayTimeline != null) autoPlayTimeline.pause();
        });
        container.setOnMouseReleased(event -> {
            double delta = event.getX() - dragStartX;
            if (Math.abs(delta) > SWIPE_THRESHOLD) {
                if (delta > 0) slidePrev(); else slideNext();
            }
            if (autoPlayTimeline != null) autoPlayTimeline.playFromStart();
        });
    }

    private void renderNews() {
        if (newsContainer == null) return;
        newsContainer.getChildren().clear();

        // Static news articles
        String[][] newsData = {
            {"Cara agar perabotan mu awet", "2 April 2026", "5 min read", "/com/teknisio/assets/news/news_prep.png"},
            {"Waspada arus listrik dirumah mu", "2 April 2026", "8 min read", "/com/teknisio/assets/news/news_ac.png"},
        };

        for (String[] data : newsData) {
            HBox card = new HBox();
            card.getStyleClass().add("news-card");
            card.setSpacing(12);
            HBox.setHgrow(card, Priority.ALWAYS);

            ImageView thumb = new ImageView();
            thumb.setFitWidth(60);
            thumb.setFitHeight(60);
            thumb.setPickOnBounds(true);
            thumb.setPreserveRatio(false);
            try {
                thumb.setImage(new Image(getClass().getResource(data[3]).toExternalForm()));
            } catch (Exception ignored) {}
            Rectangle thumbClip = new Rectangle(60, 60);
            thumbClip.setArcWidth(16);
            thumbClip.setArcHeight(16);
            thumb.setClip(thumbClip);

            VBox details = new VBox();
            details.setSpacing(4);
            details.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(details, Priority.ALWAYS);

            Label titleLabel = new Label(data[0]);
            titleLabel.getStyleClass().add("news-title");

            HBox metaBox = new HBox();
            metaBox.setSpacing(8);
            Label dateLabel = new Label(data[1]);
            dateLabel.getStyleClass().add("news-meta");
            Label timeLabel = new Label("• " + data[2]);
            timeLabel.getStyleClass().add("news-meta");
            metaBox.getChildren().addAll(dateLabel, timeLabel);
            details.getChildren().addAll(titleLabel, metaBox);

            SVGPath arrow = new SVGPath();
            arrow.setContent("M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z");
            arrow.setFill(Color.web("#6F7E91"));
            arrow.setScaleX(0.8);
            arrow.setScaleY(0.8);

            card.getChildren().addAll(thumb, details, arrow);

            final String title = data[0];
            final String date = data[1];
            card.setOnMouseClicked(event -> {
                try {
                    Main.setRoot("/com/teknisio/fxml/News.fxml");
                } catch (IOException e) {
                    System.err.println("Failed to load News: " + e.getMessage());
                }
            });

            newsContainer.getChildren().add(card);
        }
    }

    private StackPane createBadge(String svgPathContent) {
        SVGPath path = new SVGPath();
        path.setContent(svgPathContent);
        path.setScaleX(0.45);
        path.setScaleY(0.45);
        path.setFill(Color.web("#2D4B73"));
        StackPane badge = new StackPane(path);
        badge.getStyleClass().add("tech-badge");
        return badge;
    }

    /** Map category name to SVG icon path */
    public static String getCategoryIcon(String categoryName) {
        if (categoryName == null) return "M12 2L1 21h22L12 2z";
        switch (categoryName.toLowerCase()) {
            case "ac": return "M19 19H5V5h14v14M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2m-1 7h-2V8h2v2m-3 0h-2V8h2v2m-3 0H8V8h2v2m-2 2h8v-2H8v2z";
            case "kulkas": case "fridge": return "M19 2H5c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 8H7V5h10v5zm0 10H7v-8h10v8z";
            case "mesin cuci": case "washing machine": return "M17 1H7c-1.1 0-2 .9-2 2v18c0 1.1.9 2 2 2h10c1.1 0 2-.9 2-2V3c0-1.1-.9-2-2-2zm-5 18c-2.21 0-4-1.79-4-4s1.79-4 4-4 4 1.79 4 4-1.79 4-4 4zm0-6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z";
            case "oven": return "M21 4H3c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h18c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm-1 12H4V8h16v8zm-2-6h-4v2h4v-2z";
            case "tv": case "televisi": case "television": return "M21 3H3c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h5v2h8v-2h5c1.1 0 1.99-.9 1.99-2L23 5c0-1.1-.9-2-2-2zm0 14H3V5h18v12z";
            case "kipas": case "fan": return "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm0-10c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z";
            case "kamera": case "camera": case "filter camera": return "M9 2L7.17 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2h-3.17L15 2H9zm3 15c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5z";
            case "mixer": return "M18 4H6v2h12V4zm-2 4H8v2h8V8zm-1 4H9v6c0 1.66 1.34 3 3 3s3-1.34 3-3v-6z";
            default: return "M22.7 19l-9.1-9.1c.9-2.3.4-5-1.5-6.9-2-2-5-2.4-7.4-1.3L9 6 6 9 1.6 4.3C.5 6.7.9 9.8 2.9 11.8c1.9 1.9 4.6 2.4 6.9 1.5l9.1 9.1c.4.4 1 .4 1.4 0l2.3-2.3c.5-.4.5-1.1.1-1.5z";
        }
    }

    // Navigation handlers
    @FXML private void handleAccountTab(MouseEvent event) {
        try { Main.setRoot("/com/teknisio/fxml/UserProfile.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleChatTab(MouseEvent event) {
        try { Main.setRoot("/com/teknisio/fxml/Chat.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleHistoryTab(MouseEvent event) {
        try { Main.setRoot("/com/teknisio/fxml/History.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleLocationClick(MouseEvent event) {
        try { Main.setRoot("/com/teknisio/fxml/Location.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void handleCategoryClick(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof VBox) {
            VBox tile = (VBox) source;
            for (javafx.scene.Node child : tile.getChildren()) {
                if (child instanceof Label) {
                    String category = ((Label) child).getText();
                    TechnicianListController.setSelectedCategoryOnLoad(category);
                    break;
                }
            }
        }
        try { Main.setRoot("/com/teknisio/fxml/TechnicianList.fxml"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleSeeAllNews(MouseEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/News.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to News: " + e.getMessage());
        }
    }
}
