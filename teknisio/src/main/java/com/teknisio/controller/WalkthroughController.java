package com.teknisio.controller;

import com.teknisio.Main;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WalkthroughController implements Initializable {

    @FXML
    private ImageView slideImageView;

    @FXML
    private StackPane dot1;

    @FXML
    private StackPane dot2;

    @FXML
    private StackPane dot3;

    @FXML
    private Button startButton;

    private int currentSlideIndex = 0;
    private Image[] slideImages;
    private StackPane[] dots;
    private Timeline autoSlideTimeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load the slide images
        try {
            slideImages = new Image[] {
                new Image(getClass().getResource("/com/teknisio/assets/onboarding/onboarding_1.png").toExternalForm()),
                new Image(getClass().getResource("/com/teknisio/assets/onboarding/onboarding_2.png").toExternalForm()),
                new Image(getClass().getResource("/com/teknisio/assets/onboarding/onboarding_3.png").toExternalForm())
            };
        } catch (Exception e) {
            System.err.println("Error loading slide images: " + e.getMessage());
            e.printStackTrace();
        }

        dots = new StackPane[] { dot1, dot2, dot3 };

        // Set the initial image
        if (slideImages != null && slideImages.length > 0) {
            slideImageView.setImage(slideImages[0]);
        }

        // Apply a rounded clipping mask to the ImageView to achieve premium rounded corners
        Rectangle clip = new Rectangle(312, 420);
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        slideImageView.setClip(clip);

        // Make the image interactive (clicking changes to next slide)
        slideImageView.setOnMouseClicked(event -> {
            resetAutoSlideTimer();
            goToNextSlide();
        });

        // Initialize and start auto-sliding (every 3.5 seconds)
        setupAutoSlide();
    }

    private void setupAutoSlide() {
        autoSlideTimeline = new Timeline(new KeyFrame(Duration.seconds(3.5), event -> {
            goToNextSlide();
        }));
        autoSlideTimeline.setCycleCount(Timeline.INDEFINITE);
        autoSlideTimeline.play();
    }

    private void resetAutoSlideTimer() {
        if (autoSlideTimeline != null) {
            autoSlideTimeline.stop();
            autoSlideTimeline.playFromStart();
        }
    }

    private void goToNextSlide() {
        if (slideImages == null || slideImages.length == 0) return;

        int nextIndex = (currentSlideIndex + 1) % slideImages.length;
        
        // Premium fade-out and fade-in transition
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), slideImageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.1);
        fadeOut.setOnFinished(event -> {
            slideImageView.setImage(slideImages[nextIndex]);
            updateDots(nextIndex);
            currentSlideIndex = nextIndex;

            FadeTransition fadeIn = new FadeTransition(Duration.millis(250), slideImageView);
            fadeIn.setFromValue(0.1);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void updateDots(int activeIndex) {
        for (int i = 0; i < dots.length; i++) {
            dots[i].getStyleClass().removeAll("slider-dot-active", "slider-dot-inactive");
            if (i == activeIndex) {
                dots[i].getStyleClass().add("slider-dot-active");
            } else {
                dots[i].getStyleClass().add("slider-dot-inactive");
            }
        }
    }

    @FXML
    private void handleStart(ActionEvent event) {
        // Stop the auto-sliding timer before transitioning
        if (autoSlideTimeline != null) {
            autoSlideTimeline.stop();
        }
        
        // Transition to the login page
        try {
            Main.setRoot("/com/teknisio/fxml/login.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load login page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
