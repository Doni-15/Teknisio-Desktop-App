package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.util.GeoLocationUtil;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class TrackingMapController implements Initializable {

    @FXML
    private Label txtTrackingTitle;
    @FXML
    private Label txtLiveBadge;
    @FXML
    private StackPane mapContainer;
    @FXML
    private Label mapPlaceholder;
    @FXML
    private Label txtTrackingStatus;
    @FXML
    private VBox layoutInfoCard;
    @FXML
    private Label txtTechnicianName;
    @FXML
    private Label txtLastUpdate;
    @FXML
    private Label txtDistance;
    @FXML
    private Label txtInfoHint;
    @FXML
    private Button btnCenterMap;

    private static String trackerRole = "CUSTOMER"; // "CUSTOMER" or "TECHNICIAN"
    private static String targetName = "Doni (Teknisi)";
    private static String targetAddress = "Jl. Gatot Subroto No. 88, Medan";

    private Pane mapPane;
    private PathTransition pathTransition;
    private Timeline updateTimeline;
    private Timeline dashAnimation;
    private Polyline routeLine;
    private StackPane techMarker;
    private Path trackPath;
    
    private double simulatedDistance = 2.5;
    private int secondsElapsed = 0;
    
    private boolean permissionGranted = false;
    private double startLat;
    private double startLon;
    private double destLat;
    private double destLon;

    public static void setTrackingContext(String role, String name, String address) {
        trackerRole = role;
        targetName = name;
        targetAddress = address;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Hide the loading placeholder
        if (mapPlaceholder != null) {
            mapPlaceholder.setVisible(false);
        }

        // 2. Set live badge visible
        if (txtLiveBadge != null) {
            txtLiveBadge.setVisible(true);
        }

        // 3. Ask for Location Permission
        Alert permissionAlert = new Alert(Alert.AlertType.CONFIRMATION);
        permissionAlert.setTitle("Izin Akses Lokasi");
        permissionAlert.setHeaderText("Izinkan Teknisio mengakses lokasi Anda saat ini?");
        permissionAlert.setContentText("Kami membutuhkan izin lokasi untuk melakukan perhitungan rute GPS secara real-time.");
        
        ButtonType allowButton = new ButtonType("Izinkan");
        ButtonType denyButton = new ButtonType("Tolak", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        permissionAlert.getButtonTypes().setAll(allowButton, denyButton);
        
        permissionAlert.getDialogPane().getStylesheets().add(getClass().getResource("/com/teknisio/css/style.css").toExternalForm());
        permissionAlert.getDialogPane().getStyleClass().add("alert-dialog");

        Optional<ButtonType> permResult = permissionAlert.showAndWait();
        if (permResult.isPresent() && permResult.get() == allowButton) {
            permissionGranted = true;
        }

        // 4. Configure coordinates and initial distance
        if (permissionGranted) {
            Double customerLat = com.teknisio.service.SessionManager.getLatitude();
            Double customerLon = com.teknisio.service.SessionManager.getLongitude();
            if (customerLat == null || customerLon == null) {
                GeoLocationUtil.LocationResult loc = GeoLocationUtil.fetchLocation();
                customerLat = loc.lat;
                customerLon = loc.lon;
                com.teknisio.service.SessionManager.setCoordinates(loc.lat, loc.lon);
            }
            destLat = customerLat;
            destLon = customerLon;
            startLat = destLat - 0.015;
            startLon = destLon - 0.015;
            
            simulatedDistance = GeoLocationUtil.calculateDistance(startLat, startLon, destLat, destLon);
        } else {
            // Fallback to default Medan coordinates
            destLat = 3.5952;
            destLon = 98.6722;
            startLat = destLat - 0.015;
            startLon = destLon - 0.015;
            simulatedDistance = 2.5;
        }

        // 5. Configure text fields based on role
        if ("TECHNICIAN".equals(trackerRole)) {
            if (txtTrackingTitle != null) txtTrackingTitle.setText("Navigasi ke Pelanggan");
            if (txtTechnicianName != null) txtTechnicianName.setText(targetName);
            if (txtInfoHint != null) {
                txtInfoHint.setText(permissionGranted ? "Silakan menuju ke lokasi pelanggan..." : "Mode Simulasi (Izin Akses Lokasi Ditolak)");
            }
            if (txtTrackingStatus != null) txtTrackingStatus.setText("Navigasi Dimulai...");
        } else {
            if (txtTrackingTitle != null) txtTrackingTitle.setText("Lacak Teknisi");
            if (txtTechnicianName != null) txtTechnicianName.setText(targetName + " (Teknisi)");
            if (txtInfoHint != null) {
                txtInfoHint.setText(permissionGranted ? "Teknisi sedang menuju lokasi Anda..." : "Mode Simulasi (Izin Akses Lokasi Ditolak)");
            }
            if (txtTrackingStatus != null) txtTrackingStatus.setText("Menghubungkan...");
        }
        if (txtDistance != null) txtDistance.setText(String.format("%.1f km", simulatedDistance));
        if (txtLastUpdate != null) txtLastUpdate.setText("Terakhir diupdate: Baru saja");

        // 6. Create and add drawing pane
        mapPane = new Pane();
        mapPane.setPrefSize(360, 450);
        mapPane.setStyle("-fx-background-color: #0E0F26;");
        mapContainer.getChildren().add(0, mapPane); // Add at bottom of StackPane so status overlays are on top

        // 7. Draw background streets (Road lines)
        // Horizontal streets
        drawRoad(20, 200, 340, 200);
        drawRoad(20, 300, 340, 300);
        // Vertical streets
        drawRoad(80, 50, 80, 450);
        drawRoad(250, 50, 250, 450);
        drawRoad(180, 50, 180, 200);

        // 8. Draw route path
        routeLine = new Polyline();
        routeLine.getPoints().addAll(
            80.0, 420.0,
            80.0, 300.0,
            250.0, 300.0,
            250.0, 200.0,
            180.0, 200.0
        );
        routeLine.setStroke(Color.web("#7C83FF"));
        routeLine.setStrokeWidth(4.0);
        routeLine.getStrokeDashArray().addAll(10.0, 10.0);
        mapPane.getChildren().add(routeLine);

        // Animate route dashes flowing
        dashAnimation = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            routeLine.setStrokeDashOffset(routeLine.getStrokeDashOffset() - 1);
        }));
        dashAnimation.setCycleCount(Timeline.INDEFINITE);
        dashAnimation.play();

        // 9. Draw Destination Marker (Red pin with pulse ring)
        StackPane destMarker = new StackPane();
        destMarker.setLayoutX(180 - 15);
        destMarker.setLayoutY(200 - 15);
        destMarker.setPrefSize(30, 30);

        Circle pulseCircle = new Circle(15, 15, 6);
        pulseCircle.setStroke(Color.web("#FF4444"));
        pulseCircle.setStrokeWidth(2.0);
        pulseCircle.setFill(Color.TRANSPARENT);

        Circle outerCircle = new Circle(15, 15, 8);
        outerCircle.setFill(Color.web("#FF4444"));

        Circle innerCircle = new Circle(15, 15, 3);
        innerCircle.setFill(Color.WHITE);

        destMarker.getChildren().addAll(pulseCircle, outerCircle, innerCircle);
        mapPane.getChildren().add(destMarker);

        // Pulse animation
        ScaleTransition st = new ScaleTransition(Duration.seconds(1.5), pulseCircle);
        st.setToX(4.0); st.setToY(4.0);
        FadeTransition ft = new FadeTransition(Duration.seconds(1.5), pulseCircle);
        ft.setFromValue(1.0); ft.setToValue(0.0);
        ParallelTransition pt = new ParallelTransition(st, ft);
        pt.setCycleCount(Animation.INDEFINITE);
        pt.play();

        // Label for destination
        Label destLabel = new Label("Tujuan");
        destLabel.setStyle("-fx-text-fill: #FF4444; -fx-font-size: 8px; -fx-font-weight: bold; -fx-background-color: rgba(14, 15, 38, 0.7); -fx-padding: 1 4; -fx-background-radius: 4;");
        destLabel.setLayoutX(180 - 12);
        destLabel.setLayoutY(200 - 30);
        mapPane.getChildren().add(destLabel);

        // 10. Draw moving GPS locator (blue dot with soft glow)
        techMarker = new StackPane();
        techMarker.setPrefSize(30, 30);

        Circle glowCircle = new Circle(15, 15, 10);
        glowCircle.setFill(Color.web("rgba(124, 131, 255, 0.25)"));

        Circle dotCircle = new Circle(15, 15, 6);
        dotCircle.setFill(Color.web("#7C83FF"));

        Circle innerDot = new Circle(15, 15, 2);
        innerDot.setFill(Color.WHITE);

        techMarker.getChildren().addAll(glowCircle, dotCircle, innerDot);
        mapPane.getChildren().add(techMarker);

        // 11. Path Transition for moving locator
        trackPath = new Path();
        trackPath.getElements().add(new MoveTo(80, 420));
        trackPath.getElements().add(new LineTo(80, 300));
        trackPath.getElements().add(new LineTo(250, 300));
        trackPath.getElements().add(new LineTo(250, 200));
        trackPath.getElements().add(new LineTo(180, 200));

        pathTransition = new PathTransition();
        pathTransition.setNode(techMarker);
        pathTransition.setPath(trackPath);
        pathTransition.setDuration(Duration.seconds(15));
        pathTransition.setCycleCount(1);
        pathTransition.play();

        // 12. Update Timer Timeline
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            
            // Calculate current technician position based on timeline progress (0.0 to 1.0)
            double progress = (double) secondsElapsed / 15.0;
            if (progress > 1.0) progress = 1.0;
            
            double currentLat = startLat + progress * (destLat - startLat);
            double currentLon = startLon + progress * (destLon - startLon);
            
            simulatedDistance = GeoLocationUtil.calculateDistance(currentLat, currentLon, destLat, destLon);
            
            if (txtDistance != null) {
                txtDistance.setText(String.format("%.1f km", simulatedDistance));
            }

            // Update status text dynamically based on progress
            if (txtTrackingStatus != null) {
                if (simulatedDistance <= 0.05 || progress >= 1.0) {
                    txtTrackingStatus.setText("Tiba di Lokasi");
                    txtTrackingStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: #27AE60; -fx-padding: 5 14; -fx-background-radius: 12;");
                    
                    if (txtInfoHint != null) {
                        txtInfoHint.setText("TECHNICIAN".equals(trackerRole)
                            ? "Anda telah sampai di lokasi pelanggan!"
                            : "Teknisi telah sampai di lokasi Anda!");
                    }
                } else if (simulatedDistance < 0.5) {
                    txtTrackingStatus.setText("Hampir Sampai...");
                } else if (simulatedDistance < 1.5) {
                    txtTrackingStatus.setText("Dalam Perjalanan...");
                } else {
                    txtTrackingStatus.setText("Mencari Rute...");
                }
            }

            // Update last updated text
            if (txtLastUpdate != null) {
                txtLastUpdate.setText("Terakhir diupdate: Baru saja");
            }
        }));
        updateTimeline.setCycleCount(15);
        updateTimeline.play();
    }

    private void drawRoad(double startX, double startY, double endX, double endY) {
        // Draw the thick dark road surface
        Line roadSurface = new Line(startX, startY, endX, endY);
        roadSurface.setStroke(Color.web("#1E203C"));
        roadSurface.setStrokeWidth(16.0);
        
        // Draw the thin dashed centerline
        Line centerline = new Line(startX, startY, endX, endY);
        centerline.setStroke(Color.web("#383B65"));
        centerline.setStrokeWidth(1.0);
        centerline.getStrokeDashArray().addAll(5.0, 5.0);

        mapPane.getChildren().addAll(roadSurface, centerline);
    }

    @FXML
    private void handleBack() {
        try {
            if ("TECHNICIAN".equals(trackerRole)) {
                Main.setRoot("/com/teknisio/fxml/TechnicianRequestDetail.fxml");
            } else {
                Main.setRoot("/com/teknisio/fxml/ServiceRequestDetail.fxml");
            }
        } catch (IOException e) {
            System.err.println("Failed to navigate back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCenterMap() {
        // Reset and restart the simulation so user can watch it again
        if (permissionGranted) {
            simulatedDistance = GeoLocationUtil.calculateDistance(startLat, startLon, destLat, destLon);
        } else {
            simulatedDistance = 2.5;
        }
        secondsElapsed = 0;
        
        if (txtTrackingStatus != null) {
            txtTrackingStatus.setText("Menghubungkan...");
            txtTrackingStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: #AAAACC; -fx-background-color: #CC1A1A2E; -fx-padding: 5 14;");
        }
        if (txtDistance != null) txtDistance.setText(String.format("%.1f km", simulatedDistance));
        if (txtInfoHint != null) {
            txtInfoHint.setText("TECHNICIAN".equals(trackerRole)
                ? (permissionGranted ? "Silakan menuju ke lokasi pelanggan..." : "Mode Simulasi (Izin Akses Lokasi Ditolak)")
                : (permissionGranted ? "Teknisi sedang menuju lokasi Anda..." : "Mode Simulasi (Izin Akses Lokasi Ditolak)"));
        }

        pathTransition.stop();
        pathTransition.playFromStart();

        updateTimeline.stop();
        updateTimeline.playFromStart();
    }
}