package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.util.GeoLocationUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
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

    private WebView webView;
    private Timeline updateTimeline;
    
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
            if ("TECHNICIAN".equals(trackerRole)) {
                // The technician is running the app, so technician location is real-time IP Geolocation
                Double techLat = com.teknisio.service.SessionManager.getLatitude();
                Double techLon = com.teknisio.service.SessionManager.getLongitude();
                if (techLat == null || techLon == null) {
                    GeoLocationUtil.LocationResult loc = GeoLocationUtil.fetchLocation();
                    techLat = loc.lat;
                    techLon = loc.lon;
                    com.teknisio.service.SessionManager.setCoordinates(loc.lat, loc.lon);
                }
                startLat = techLat;
                startLon = techLon;
                
                // Destination is customer's request address text
                GeoLocationUtil.LocationResult destLoc = GeoLocationUtil.geocodeAddress(targetAddress);
                if (destLoc != null) {
                    destLat = destLoc.lat;
                    destLon = destLoc.lon;
                } else {
                    destLat = startLat + 0.015;
                    destLon = startLon + 0.015;
                }
            } else {
                // The customer is running the app, so customer location is real-time IP Geolocation
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
                
                // Starting point is technician's profile address text
                GeoLocationUtil.LocationResult startLoc = GeoLocationUtil.geocodeAddress(targetAddress);
                if (startLoc != null) {
                    startLat = startLoc.lat;
                    startLon = startLoc.lon;
                } else {
                    startLat = destLat - 0.015;
                    startLon = destLon - 0.015;
                }
            }
            
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

        // 6. Create and add WebView to show the real interactive map
        webView = new WebView();
        webView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        mapContainer.getChildren().add(0, webView); // Add at bottom of StackPane so overlays are on top

        // 7. Load real Leaflet dark matter map content
        webView.getEngine().loadContent(getMapHtmlContent());

        // 8. Update Timer Timeline
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

    private String getMapHtmlContent() {
        return "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\" />\n" +
            "    <script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\"></script>\n" +
            "    <style>\n" +
            "        html, body, #map { width: 100%; height: 100%; margin: 0; padding: 0; background: #0E0F26; }\n" +
            "        .leaflet-container { background: #0E0F26; }\n" +
            "        .leaflet-bar { border: none !important; }\n" +
            "        .leaflet-control-zoom { margin: 10px !important; }\n" +
            "        .leaflet-control-attribution { display: none !important; }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div id=\"map\"></div>\n" +
            "    <script>\n" +
            "        var map = L.map('map', {zoomControl: true}).setView([" + destLat + ", " + destLon + "], 14);\n" +
            "        L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {\n" +
            "            maxZoom: 19\n" +
            "        }).addTo(map);\n" +
            "        \n" +
            "        var customerIcon = L.icon({\n" +
            "            iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png',\n" +
            "            shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',\n" +
            "            iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34], shadowSize: [41, 41]\n" +
            "        });\n" +
            "        var customerMarker = L.marker([" + destLat + ", " + destLon + "], {icon: customerIcon}).addTo(map)\n" +
            "            .bindPopup('<b>Lokasi Anda</b>').openPopup();\n" +
            "        \n" +
            "        var techIcon = L.icon({\n" +
            "            iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-blue.png',\n" +
            "            shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',\n" +
            "            iconSize: [25, 41], iconAnchor: [12, 41], shadowSize: [41, 41]\n" +
            "        });\n" +
            "        var techMarker = L.marker([" + startLat + ", " + startLon + "], {icon: techIcon}).addTo(map);\n" +
            "        \n" +
            "        var polyline = L.polyline([[" + startLat + ", " + startLon + "], [" + destLat + ", " + destLon + "]], {\n" +
            "            color: '#7C83FF', weight: 4, dashArray: '10, 10'\n" +
            "        }).addTo(map);\n" +
            "        \n" +
            "        var group = new L.featureGroup([customerMarker, techMarker]);\n" +
            "        map.fitBounds(group.getBounds().pad(0.15));\n" +
            "        \n" +
            "        var duration = 15000;\n" +
            "        var startTime = null;\n" +
            "        function animate(timestamp) {\n" +
            "            if (!startTime) startTime = timestamp;\n" +
            "            var elapsed = timestamp - startTime;\n" +
            "            var progress = Math.min(elapsed / duration, 1.0);\n" +
            "            var curLat = " + startLat + " + progress * (" + destLat + " - " + startLat + ");\n" +
            "            var curLon = " + startLon + " + progress * (" + destLon + " - " + startLon + ");\n" +
            "            techMarker.setLatLng([curLat, curLon]);\n" +
            "            polyline.setLatLngs([[curLat, curLon], [" + destLat + ", " + destLon + "]]);\n" +
            "            if (progress < 1.0) {\n" +
            "                requestAnimationFrame(animate);\n" +
            "            } else {\n" +
            "                techMarker.bindPopup('<b>Teknisi Telah Sampai</b>').openPopup();\n" +
            "            }\n" +
            "        }\n" +
            "        requestAnimationFrame(animate);\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>";
    }

    @FXML
    private void handleBack() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
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

        // Reload WebView to restart the animation
        webView.getEngine().loadContent(getMapHtmlContent());

        updateTimeline.stop();
        updateTimeline.playFromStart();
    }
}