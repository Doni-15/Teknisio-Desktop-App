package com.teknisio.controller;

import com.teknisio.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class OrderTechnicianController implements Initializable {

    @FXML
    private ToggleButton categoryAc;

    @FXML
    private ToggleButton categoryFridge;

    @FXML
    private TextArea descriptionField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ToggleButton timeMorning;

    @FXML
    private ToggleButton timeAfternoon;

    @FXML
    private ToggleButton timeEvening;

    @FXML
    private StackPane techImageWrapper;

    @FXML
    private ImageView techImageView;

    @FXML
    private Label techNameLabel;

    @FXML
    private HBox techRatingBox;

    @FXML
    private Label techDistanceLabel;

    @FXML
    private Label techPriceLabel;

    @FXML
    private HBox techBadgesBox;

    private ToggleGroup categoryGroup;
    private ToggleGroup timeGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Crop technician headshot to circular bounds
        Circle clip = new Circle(24, 24, 24);
        techImageView.setClip(clip);

        // 2. Group Categories ToggleButtons
        categoryGroup = new ToggleGroup();
        categoryAc.setToggleGroup(categoryGroup);
        categoryFridge.setToggleGroup(categoryGroup);
        categoryAc.setSelected(true); // Default category selection

        // 3. Group Time Segments ToggleButtons
        timeGroup = new ToggleGroup();
        timeMorning.setToggleGroup(timeGroup);
        timeAfternoon.setToggleGroup(timeGroup);
        timeEvening.setToggleGroup(timeGroup);
        timeAfternoon.setSelected(true); // Default active segment matching mockup

        // 4. Default Date Selection to Today or Tomorrow
        datePicker.setValue(LocalDate.now().plusDays(1));

        // 5. Populate Rating Stars for Ahmed Rush (5.0 rating)
        populateStars(5);

        // 6. Populate Specialization Badges
        populateBadges();
    }

    private void populateStars(int rating) {
        techRatingBox.getChildren().clear();
        for (int i = 0; i < 5; i++) {
            SVGPath star = new SVGPath();
            star.setContent("M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
            star.setScaleX(0.45);
            star.setScaleY(0.45);

            if (i < rating) {
                star.setFill(Color.web("#F1C40F")); // Gold
            } else {
                star.setFill(Color.web("#BDC3C7")); // Gray
            }

            StackPane starWrapper = new StackPane(star);
            starWrapper.setMinSize(11, 11);
            starWrapper.setPrefSize(11, 11);
            starWrapper.setMaxSize(11, 11);
            starWrapper.setAlignment(Pos.CENTER);
            techRatingBox.getChildren().add(starWrapper);
        }
    }

    private void populateBadges() {
        techBadgesBox.getChildren().clear();
        // AC Icon Badge
        techBadgesBox.getChildren().add(createBadge("M19 19H5V5h14v14M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2"));
        // Fridge Icon Badge
        techBadgesBox.getChildren().add(createBadge("M19 2H5c-1.1 0-2 .9-2 2v16c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 8H7V5h10v5z"));
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
    private void handleBack(ActionEvent event) {
        navigateHome();
    }

    @FXML
    private void handleConfirmOrder(ActionEvent event) {
        // Validation Checks
        if (categoryGroup.getSelectedToggle() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a category.");
            return;
        }

        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please describe the damages.");
            return;
        }

        if (datePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please pick a date for the schedule.");
            return;
        }

        if (timeGroup.getSelectedToggle() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a preferred time segment.");
            return;
        }

        // Show Success popup
        showAlert(Alert.AlertType.INFORMATION, "Order Placed", 
            "Order Confirmed!\nYour repair request has been successfully assigned to " + techNameLabel.getText() + 
            " for " + datePicker.getValue() + ".");

        // Redirect back home
        navigateHome();
    }

    private void navigateHome() {
        try {
            Main.setRoot("/com/teknisio/fxml/home_user.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("alert-dialog");
        
        alert.showAndWait();
    }
}
