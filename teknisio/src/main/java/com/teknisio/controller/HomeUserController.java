package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.model.News;
import com.teknisio.model.Technician;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeUserController implements Initializable {

    @FXML
    private StackPane profileImageWrapper;

    @FXML
    private ImageView profileImageView;

    @FXML
    private StackPane carouselContainer;

    @FXML
    private VBox newsContainer;

    @FXML
    private Label locationValueLabel;

    private static String selectedLocationText = "Intertip St., Medan";
    
    /**
     * Called by LocationController when a new location is selected.
     */
    public static void setSelectedLocation(String location) {
        selectedLocationText = location;
    }

    private List<Technician> technicians = new ArrayList<>();
    private List<News> newsList = new ArrayList<>();

    private int currentIndex = 1; // Default focused on Ahmed Rush (index 1)
    private List<StackPane> techCards = new ArrayList<>();
    private Timeline autoPlayTimeline;
    private double dragStartX;
    private static final double SWIPE_THRESHOLD = 50.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Restore selected location text
        if (locationValueLabel != null && selectedLocationText != null) {
            locationValueLabel.setText(selectedLocationText);
        }

        // 1. Circular Crop on Profile Image
        Circle profileClip = new Circle(21, 21, 21);
        profileImageView.setClip(profileClip);

        // 2. Initialize Dummy Data
        loadDummyData();

        // 3. Render Carousel Technician Cards
        renderTechnicians();

        // 4. Set up Swipe and Autoplay support
        setupSwipeSupport(carouselContainer);
        startAutoPlay();

        // 5. Render News List
        renderNews();
    }

    private void loadDummyData() {
        // Technicians matching mockup names and info
        technicians.add(new Technician(
            "Charlie Hugh",
            "Oven Specialist",
            4.8,
            "Rp 40.000 - Rp 150.000",
            "/com/teknisio/assets/technicians/tech_cedric.png",
            false
        ));
        
        technicians.add(new Technician(
            "Ahmed Rush",
            "AC Specialist",
            5.0,
            "Rp 50.000 - Rp 200.000",
            "/com/teknisio/assets/technicians/tech_ahmed.png",
            true // Featured center card
        ));

        technicians.add(new Technician(
            "Ben Adams",
            "Fridge Specialist",
            4.7,
            "Rp 45.000 - Rp 180.000",
            "/com/teknisio/assets/technicians/tech_devon.png",
            false
        ));

        // News Articles
        newsList.add(new News(
            "Cara agar perabotan mu awet",
            "2 April 2026",
            "/com/teknisio/assets/news/news_prep.png",
            "5 min read"
        ));
        newsList.add(new News(
            "Waspada arus listrik dirumah mu",
            "2 April 2026",
            "/com/teknisio/assets/news/news_ac.png",
            "8 min read"
        ));
    }

    private void renderTechnicians() {
        carouselContainer.getChildren().clear();
        techCards.clear();

        for (int index = 0; index < technicians.size(); index++) {
            final int i = index;
            Technician tech = technicians.get(i);
            StackPane card = new StackPane();
            card.getStyleClass().add("tech-card");

            double cardWidth = 160;
            double cardHeight = 194;

            // 1. ImageView for Avatar background
            ImageView avatar = new ImageView();
            avatar.setFitWidth(cardWidth);
            avatar.setFitHeight(cardHeight);
            avatar.setPickOnBounds(true);
            avatar.setPreserveRatio(false);

            try {
                avatar.setImage(new Image(getClass().getResource(tech.getAvatarPath()).toExternalForm()));
            } catch (Exception e) {
                System.err.println("Failed to load tech avatar: " + tech.getAvatarPath());
            }

            // Round the corners of the avatar image
            Rectangle imgClip = new Rectangle(cardWidth, cardHeight);
            imgClip.setArcWidth(32);
            imgClip.setArcHeight(32);
            avatar.setClip(imgClip);

            // 2. Gradient Overlay for text contrast at the bottom of the card
            Region gradientOverlay = new Region();
            gradientOverlay.getStyleClass().add("tech-gradient-overlay");
            gradientOverlay.setPrefSize(cardWidth, cardHeight);
            gradientOverlay.setMaxSize(cardWidth, cardHeight);

            // 3. Content overlay layout
            BorderPane contentPane = new BorderPane();
            contentPane.setPrefSize(cardWidth, cardHeight);
            contentPane.setMaxSize(cardWidth, cardHeight);
            contentPane.setPadding(new Insets(12));

            // -- TOP SECTION -- (Stars and Distance)
            HBox topBox = new HBox();
            topBox.setAlignment(Pos.CENTER_LEFT);
            
            // Stars HBox (Gold)
            HBox ratingBox = new HBox();
            ratingBox.setSpacing(1);
            ratingBox.setAlignment(Pos.CENTER_LEFT);
            
            int fullStars = (int) Math.round(tech.getRating());
            for (int k = 0; k < 5; k++) {
                SVGPath star = new SVGPath();
                star.setContent("M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
                star.setScaleX(0.4);
                star.setScaleY(0.4);
                
                if (k < fullStars) {
                    star.setFill(Color.web("#F1C40F")); // Gold
                } else {
                    star.setFill(Color.web("rgba(255, 255, 255, 0.4)"));
                }
                
                StackPane starWrapper = new StackPane(star);
                starWrapper.setMinSize(10, 10);
                starWrapper.setPrefSize(10, 10);
                starWrapper.setMaxSize(10, 10);
                starWrapper.setAlignment(Pos.CENTER);
                ratingBox.getChildren().add(starWrapper);
            }
            
            Region spacerTop = new Region();
            HBox.setHgrow(spacerTop, Priority.ALWAYS);
            
            // Distance Tag
            Label distanceLabel = new Label("±2 Km");
            distanceLabel.getStyleClass().add("tech-distance");
            
            topBox.getChildren().addAll(ratingBox, spacerTop, distanceLabel);
            contentPane.setTop(topBox);

            // -- BOTTOM SECTION -- (Name, Price, Badges, Button)
            VBox bottomBox = new VBox();
            bottomBox.setSpacing(4);
            bottomBox.setAlignment(Pos.BOTTOM_LEFT);

            // Name
            Label nameLabel = new Label(tech.getName());
            nameLabel.getStyleClass().add("tech-name");
            bottomBox.getChildren().add(nameLabel);

            // Price (toggled via active state)
            Label priceLabel = new Label(tech.getPriceRange());
            priceLabel.getStyleClass().add("tech-price");
            priceLabel.setStyle("-fx-text-fill: #E2ECF7; -fx-font-size: 8px; -fx-font-weight: bold;");
            bottomBox.getChildren().add(priceLabel);

            // Badges & Action Button row
            HBox bottomRow = new HBox();
            bottomRow.setAlignment(Pos.CENTER_LEFT);
            bottomRow.setSpacing(6);

            // Badges HBox (Small circles)
            HBox badgesBox = new HBox();
            badgesBox.setSpacing(4);
            badgesBox.setAlignment(Pos.CENTER_LEFT);

            // Add dummy badges based on specialization
            if (tech.getName().contains("Charlie")) {
                badgesBox.getChildren().addAll(
                    createBadge("M21 3H3c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h5v2h8v-2h5c1.1 0 1.99-.9 1.99-2L23 5c0-1.1-.9-2-2-2zm0 14H3V5h18v12z"), // TV/Screen
                    createBadge("M21 4H3c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h18c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm-1 12H4V8h16v8zm-2-6h-4v2h4v-2z") // Oven
                );
            } else if (tech.getName().contains("Ahmed")) {
                badgesBox.getChildren().addAll(
                    createBadge("M19 19H5V5h14v14M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2m-1 7h-2V8h2v2m-3 0h-2V8h2v2m-3 0H8V8h2v2m-2 2h8v-2H8v2z"), // AC
                    createBadge("M19 2H5c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 8H7V5h10v5zm0 10H7v-8h10v8z") // Fridge
                );
            } else {
                badgesBox.getChildren().addAll(
                    createBadge("M19 2H5c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 8H7V5h10v5zm0 10H7v-8h10v8z"), // Fridge
                    createBadge("M22.7 19l-9.1-9.1c.9-2.3.4-5-1.5-6.9-2-2-5-2.4-7.4-1.3L9 6 6 9 1.6 4.3C.5 6.7.9 9.8 2.9 11.8c1.9 1.9 4.6 2.4 6.9 1.5l9.1 9.1c.4.4 1 .4 1.4 0l2.3-2.3c.5-.4.5-1.1.1-1.5z") // Tools
                );
            }

            bottomRow.getChildren().add(badgesBox);

            Region spacerBottom = new Region();
            HBox.setHgrow(spacerBottom, Priority.ALWAYS);
            
            Label moreInfoBtn = new Label("More Info >");
            moreInfoBtn.getStyleClass().add("tech-more-info-btn");
            
            bottomRow.getChildren().addAll(spacerBottom, moreInfoBtn);

            bottomBox.getChildren().add(bottomRow);
            contentPane.setBottom(bottomBox);

            // Assemble StackPane layers
            card.getChildren().addAll(avatar, gradientOverlay, contentPane);

            // Click listener
            card.setOnMouseClicked(event -> {
                if (i == currentIndex) {
                    try {
                        Main.setRoot("/com/teknisio/fxml/OrderTechnician.fxml");
                    } catch (IOException e) {
                        System.err.println("Failed to load OrderTechnician page: " + e.getMessage());
                        e.printStackTrace();
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

        // Align the cards initially
        updateCarousel();
    }

    private void updateCarousel() {
        int n = technicians.size();
        if (n == 0) return;

        for (int i = 0; i < n; i++) {
            StackPane card = techCards.get(i);
            
            int offset = i - currentIndex;
            if (offset < -n / 2) {
                offset += n;
            } else if (offset > n / 2) {
                offset -= n;
            }

            if (offset == 0) {
                card.setVisible(true);
                card.setDisable(false);
                setCardActiveState(card, true);
                animateCard(card, 0, 1.0, 1.0, true);
            } else if (offset == -1) {
                card.setVisible(true);
                card.setDisable(false);
                setCardActiveState(card, false);
                animateCard(card, -100, 0.8, 0.7, false);
            } else if (offset == 1) {
                card.setVisible(true);
                card.setDisable(false);
                setCardActiveState(card, false);
                animateCard(card, 100, 0.8, 0.7, false);
            } else {
                card.setVisible(false);
            }
        }
    }

    private void animateCard(StackPane card, double targetX, double targetScale, double targetOpacity, boolean isCenter) {
        if (isCenter) {
            card.toFront();
        }

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), card);
        tt.setToX(targetX);

        ScaleTransition st = new ScaleTransition(Duration.millis(350), card);
        st.setToX(targetScale);
        st.setToY(targetScale);

        FadeTransition ft = new FadeTransition(Duration.millis(350), card);
        ft.setToValue(targetOpacity);

        ParallelTransition pt = new ParallelTransition(tt, st, ft);
        pt.play();
    }

    private void setCardActiveState(StackPane card, boolean isActive) {
        card.getStyleClass().clear();
        if (isActive) {
            card.getStyleClass().add("tech-card-active");
        } else {
            card.getStyleClass().add("tech-card");
        }

        BorderPane contentPane = (BorderPane) card.getChildren().get(2);
        
        // Stars & Distance tag at the top
        if (contentPane.getTop() != null) {
            contentPane.getTop().setVisible(isActive);
            contentPane.getTop().setManaged(isActive);
        }

        // Bottom section toggles
        VBox bottomBox = (VBox) contentPane.getBottom();
        if (bottomBox != null && bottomBox.getChildren().size() >= 3) {
            // Price range label
            bottomBox.getChildren().get(1).setVisible(isActive);
            bottomBox.getChildren().get(1).setManaged(isActive);

            // BottomRow details
            HBox bottomRow = (HBox) bottomBox.getChildren().get(2);
            if (bottomRow.getChildren().size() >= 2) {
                // More Info button
                bottomRow.getChildren().get(1).setVisible(isActive);
                bottomRow.getChildren().get(1).setManaged(isActive);
            }
        }
    }

    private void startAutoPlay() {
        if (autoPlayTimeline != null) {
            autoPlayTimeline.stop();
        }
        autoPlayTimeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            slideNext();
        }));
        autoPlayTimeline.setCycleCount(Timeline.INDEFINITE);
        autoPlayTimeline.play();
    }

    private void resetAutoPlay() {
        if (autoPlayTimeline != null) {
            autoPlayTimeline.playFromStart();
        }
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
            if (autoPlayTimeline != null) {
                autoPlayTimeline.pause();
            }
        });

        container.setOnMouseReleased(event -> {
            double dragEndX = event.getX();
            double deltaX = dragEndX - dragStartX;
            
            if (Math.abs(deltaX) > SWIPE_THRESHOLD) {
                if (deltaX > 0) {
                    slidePrev();
                } else {
                    slideNext();
                }
            }
            
            if (autoPlayTimeline != null) {
                autoPlayTimeline.playFromStart();
            }
        });
    }

    private void renderNews() {
        newsContainer.getChildren().clear();

        for (News news : newsList) {
            HBox card = new HBox();
            card.getStyleClass().add("news-card");
            card.setSpacing(12);
            HBox.setHgrow(card, Priority.ALWAYS);

            // Thumbnail
            ImageView thumb = new ImageView();
            thumb.setFitWidth(60);
            thumb.setFitHeight(60);
            thumb.setPickOnBounds(true);
            thumb.setPreserveRatio(false);

            try {
                thumb.setImage(new Image(getClass().getResource(news.getThumbnailPath()).toExternalForm()));
            } catch (Exception e) {
                System.err.println("Failed to load news thumbnail: " + news.getThumbnailPath());
            }

            // Round the corners of the thumbnail
            Rectangle thumbClip = new Rectangle(60, 60);
            thumbClip.setArcWidth(16);
            thumbClip.setArcHeight(16);
            thumb.setClip(thumbClip);

            // Details
            VBox details = new VBox();
            details.setSpacing(4);
            details.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(details, Priority.ALWAYS);

            Label titleLabel = new Label(news.getTitle());
            titleLabel.getStyleClass().add("news-title");

            HBox metaBox = new HBox();
            metaBox.setSpacing(8);
            
            Label dateLabel = new Label(news.getDate());
            dateLabel.getStyleClass().add("news-meta");
            
            Label timeLabel = new Label("• " + news.getReadTime());
            timeLabel.getStyleClass().add("news-meta");
            
            metaBox.getChildren().addAll(dateLabel, timeLabel);
            details.getChildren().addAll(titleLabel, metaBox);

            // Chevron Spacer and Arrow
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.NEVER);
            
            SVGPath arrow = new SVGPath();
            arrow.setContent("M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z");
            arrow.setFill(Color.web("#6F7E91"));
            arrow.setScaleX(0.8);
            arrow.setScaleY(0.8);

            card.getChildren().addAll(thumb, details, spacer, arrow);

            // Action handler on news click
            card.setOnMouseClicked(event -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("News Article Selection");
                alert.setHeaderText(null);
                alert.setContentText("You selected: \"" + news.getTitle() + "\"\nPublished on: " + news.getDate());
                alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
                alert.getDialogPane().getStyleClass().add("alert-dialog");
                alert.showAndWait();
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

    @FXML
    private void handleAccountTab(MouseEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/UserProfile.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to UserProfile page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChatTab(MouseEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/Chat.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to Chat page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleHistoryTab(MouseEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/History.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to History page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLocationClick(MouseEvent event) {
        try {
            Main.setRoot("/com/teknisio/fxml/Location.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to Location page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCategoryClick(MouseEvent event) {
        Object source = event.getSource();
        if (source instanceof VBox) {
            VBox tile = (VBox) source;
            for (javafx.scene.Node child : tile.getChildren()) {
                if (child instanceof Label) {
                    Label label = (Label) child;
                    String category = label.getText();
                    TechnicianListController.setSelectedCategoryOnLoad(category);
                    break;
                }
            }
        }
        try {
            Main.setRoot("/com/teknisio/fxml/TechnicianList.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to TechnicianList: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
