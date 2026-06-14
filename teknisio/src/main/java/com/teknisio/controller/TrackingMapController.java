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
    private static String currentServiceRequestId = null;

    private WebView webView;
    private Timeline updateTimeline;
    
    private double simulatedDistance = 2.5;
    private int secondsElapsed = 0;
    
    private boolean permissionGranted = false;
    private double startLat;
    private double startLon;
    private double destLat;
    private double destLon;

    public static void setTrackingContext(String role, String name, String address, String serviceRequestId) {
        trackerRole = role;
        targetName = name;
        targetAddress = address;
        currentServiceRequestId = serviceRequestId;
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

        // 4. Configure coordinates and initial distance in background
        if (txtTrackingStatus != null) txtTrackingStatus.setText("Menghubungkan GPS...");
        
        Thread initThread = new Thread(() -> {
            if (permissionGranted) {
                if ("TECHNICIAN".equals(trackerRole)) {
                    // Technician is running the app
                    Double techLat = com.teknisio.service.SessionManager.getLatitude();
                    Double techLon = com.teknisio.service.SessionManager.getLongitude();
                    if (techLat == null || techLon == null) {
                        GeoLocationUtil.LocationResult loc = GeoLocationUtil.fetchLocation();
                        if (loc != null) {
                            techLat = loc.lat;
                            techLon = loc.lon;
                            com.teknisio.service.SessionManager.setCoordinates(loc.lat, loc.lon);
                        } else {
                            techLat = 3.5952;
                            techLon = 98.6722;
                        }
                    }
                    startLat = techLat;
                    startLon = techLon;
                    
                    // Destination is customer's request coordinates or geocoded address
                    com.teknisio.dto.ServiceRequestDto req = com.teknisio.service.TechnicianRequestService.getRequestDetail(currentServiceRequestId);
                    if (req != null && req.getLatitude() != null && req.getLongitude() != null) {
                        destLat = req.getLatitude();
                        destLon = req.getLongitude();
                    } else {
                        GeoLocationUtil.LocationResult destLoc = GeoLocationUtil.geocodeAddress(targetAddress);
                        if (destLoc != null) {
                            destLat = destLoc.lat;
                            destLon = destLoc.lon;
                        } else {
                            destLat = startLat + 0.015;
                            destLon = startLon + 0.015;
                        }
                    }
                } else {
                    // Customer is running the app
                    Double customerLat = com.teknisio.service.SessionManager.getLatitude();
                    Double customerLon = com.teknisio.service.SessionManager.getLongitude();
                    if (customerLat == null || customerLon == null) {
                        GeoLocationUtil.LocationResult loc = GeoLocationUtil.fetchLocation();
                        if (loc != null) {
                            customerLat = loc.lat;
                            customerLon = loc.lon;
                            com.teknisio.service.SessionManager.setCoordinates(loc.lat, loc.lon);
                        } else {
                            customerLat = 3.5952;
                            customerLon = 98.6722;
                        }
                    }
                    destLat = customerLat;
                    destLon = customerLon;
                    
                    // Start is technician's coordinates or geocoded address
                    com.teknisio.dto.ServiceRequestDto req = com.teknisio.service.ServiceRequestService.getServiceRequestDetail(currentServiceRequestId);
                    if (req != null && req.getLatitude() != null && req.getLongitude() != null) {
                        startLat = req.getLatitude();
                        startLon = req.getLongitude();
                    } else {
                        GeoLocationUtil.LocationResult startLoc = GeoLocationUtil.geocodeAddress(targetAddress);
                        if (startLoc != null) {
                            startLat = startLoc.lat;
                            startLon = startLoc.lon;
                        } else {
                            startLat = destLat - 0.015;
                            startLon = destLon - 0.015;
                        }
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
            
            javafx.application.Platform.runLater(() -> {
                // Update text fields
                if (txtDistance != null) txtDistance.setText(String.format("%.1f km", simulatedDistance));
                if (txtLastUpdate != null) txtLastUpdate.setText("Terakhir diupdate: Baru saja");
                
                if (txtTrackingStatus != null) {
                    txtTrackingStatus.setText("Terhubung");
                    txtTrackingStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: #2980B9; -fx-padding: 5 14; -fx-background-radius: 12;");
                }
                
                // Load WebView
                webView = new WebView();
                webView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                mapContainer.getChildren().add(0, webView);
                webView.getEngine().loadContent(getMapHtmlContent());
                
                // Start real-time update timeline
                startRealTimeTimeline();
            });
        });
        initThread.setDaemon(true);
        initThread.start();
    }

    private void startRealTimeTimeline() {
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            Thread updateThread = new Thread(() -> {
                if ("TECHNICIAN".equals(trackerRole)) {
                    // Technician fetches their own current position
                    GeoLocationUtil.LocationResult loc = GeoLocationUtil.fetchLocation();
                    if (loc != null) {
                        startLat = loc.lat;
                        startLon = loc.lon;
                        com.teknisio.service.SessionManager.setCoordinates(loc.lat, loc.lon);
                        
                        // Push coordinates to database
                        com.teknisio.service.TechnicianRequestService.updateRequestLocation(currentServiceRequestId, startLat, startLon);
                        
                        javafx.application.Platform.runLater(() -> {
                            try {
                                webView.getEngine().executeScript("updateMarkers(" + startLat + ", " + startLon + ", " + destLat + ", " + destLon + ");");
                            } catch (Exception ex) {
                                System.err.println("Failed to update markers: " + ex.getMessage());
                            }
                        });
                    }
                } else {
                    // Customer polls request details from database
                    com.teknisio.dto.ServiceRequestDto req = com.teknisio.service.ServiceRequestService.getServiceRequestDetail(currentServiceRequestId);
                    if (req != null && req.getLatitude() != null && req.getLongitude() != null) {
                        startLat = req.getLatitude();
                        startLon = req.getLongitude();
                        
                        javafx.application.Platform.runLater(() -> {
                            try {
                                webView.getEngine().executeScript("updateMarkers(" + startLat + ", " + startLon + ", " + destLat + ", " + destLon + ");");
                            } catch (Exception ex) {
                                System.err.println("Failed to update markers: " + ex.getMessage());
                            }
                        });
                    }
                }
                
                simulatedDistance = GeoLocationUtil.calculateDistance(startLat, startLon, destLat, destLon);
                
                javafx.application.Platform.runLater(() -> {
                    if (txtDistance != null) {
                        txtDistance.setText(String.format("%.1f km", simulatedDistance));
                    }
                    
                    if (txtTrackingStatus != null) {
                        if (simulatedDistance <= 0.05) {
                            txtTrackingStatus.setText("Tiba di Lokasi");
                            txtTrackingStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: #27AE60; -fx-padding: 5 14; -fx-background-radius: 12;");
                            if (txtInfoHint != null) {
                                txtInfoHint.setText("TECHNICIAN".equals(trackerRole)
                                    ? "Anda telah sampai di lokasi pelanggan!"
                                    : "Teknisi telah sampai di lokasi Anda!");
                            }
                        } else if (simulatedDistance < 0.5) {
                            txtTrackingStatus.setText("Hampir Sampai...");
                            txtTrackingStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: #E67E22; -fx-padding: 5 14; -fx-background-radius: 12;");
                            if (txtInfoHint != null) {
                                txtInfoHint.setText("TECHNICIAN".equals(trackerRole)
                                    ? "Silakan menuju ke lokasi pelanggan..."
                                    : "Teknisi sedang menuju lokasi Anda...");
                            }
                        } else {
                            txtTrackingStatus.setText("Dalam Perjalanan");
                            txtTrackingStatus.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: #2980B9; -fx-padding: 5 14; -fx-background-radius: 12;");
                        }
                    }
                    if (txtLastUpdate != null) {
                        txtLastUpdate.setText("Terakhir diupdate: Baru saja");
                    }
                });
            });
            updateThread.setDaemon(true);
            updateThread.start();
        }));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
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
            "        var roadmap = L.tileLayer('https://mt1.google.com/vt/lyrs=m&x={x}&y={y}&z={z}', {\n" +
            "            maxZoom: 20,\n" +
            "            attribution: '© Google'\n" +
            "        });\n" +
            "        var satellite = L.tileLayer('https://mt1.google.com/vt/lyrs=y&x={x}&y={y}&z={z}', {\n" +
            "            maxZoom: 20,\n" +
            "            attribution: '© Google'\n" +
            "        });\n" +
            "        \n" +
            "        var map = L.map('map', {\n" +
            "            zoomControl: true,\n" +
            "            layers: [roadmap]\n" +
            "        }).setView([" + destLat + ", " + destLon + "], 14);\n" +
            "        \n" +
            "        var baseMaps = {\n" +
            "            \"Google Map\": roadmap,\n" +
            "            \"Google Satellite\": satellite\n" +
            "        };\n" +
            "        L.control.layers(baseMaps).addTo(map);\n" +
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
            "            color: '#1A73E8', weight: 4, dashArray: '8, 8'\n" +
            "        }).addTo(map);\n" +
            "        \n" +
            "        function centerMap() {\n" +
            "            var group = new L.featureGroup([customerMarker, techMarker]);\n" +
            "            map.fitBounds(group.getBounds().pad(0.15));\n" +
            "        }\n" +
            "        centerMap();\n" +
            "        \n" +
            "        function updateMarkers(techLat, techLon, custLat, custLon) {\n" +
            "            techMarker.setLatLng([techLat, techLon]);\n" +
            "            customerMarker.setLatLng([custLat, custLon]);\n" +
            "            polyline.setLatLngs([[techLat, techLon], [custLat, custLon]]);\n" +
            "        }\n" +
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
        try {
            webView.getEngine().executeScript("centerMap();");
        } catch (Exception ex) {
            System.err.println("Failed to center map: " + ex.getMessage());
        }
    }
}