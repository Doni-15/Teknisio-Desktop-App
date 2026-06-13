package com.teknisio.controller;

import com.teknisio.Main;
import com.teknisio.dto.DeviceCategoryDto;
import com.teknisio.service.DeviceCategoryService;
import com.teknisio.service.TechnicianService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TechnicianSkillController implements Initializable {

    @FXML private Label txtSkillSubtitle;
    @FXML private VBox layoutCurrentSkills;
    @FXML private Label txtCurrentSkillEmpty;
    @FXML private VBox layoutAvailableSkills;
    @FXML private Label txtAvailableSkillEmpty;
    @FXML private Button btnRefreshSkills;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSkills();
    }

    private void loadSkills() {
        if (txtCurrentSkillEmpty != null) {
            txtCurrentSkillEmpty.setText("Memuat keahlian...");
            txtCurrentSkillEmpty.setVisible(true);
        }
        if (txtAvailableSkillEmpty != null) {
            txtAvailableSkillEmpty.setText("Memuat kategori...");
            txtAvailableSkillEmpty.setVisible(true);
        }
        if (layoutCurrentSkills != null) {
            layoutCurrentSkills.getChildren().clear();
        }
        if (layoutAvailableSkills != null) {
            layoutAvailableSkills.getChildren().clear();
        }

        Thread t = new Thread(() -> {
            List<DeviceCategoryDto> allCats = DeviceCategoryService.getActiveCategories();
            List<DeviceCategoryDto> myCats = TechnicianService.getMyDeviceCategories();

            List<String> myCatIds = myCats.stream()
                .map(DeviceCategoryDto::getDeviceCategoryId)
                .collect(Collectors.toList());

            List<DeviceCategoryDto> availableCats = allCats.stream()
                .filter(c -> !myCatIds.contains(c.getDeviceCategoryId()))
                .collect(Collectors.toList());

            Platform.runLater(() -> {
                // Populate active skills
                if (myCats.isEmpty()) {
                    if (txtCurrentSkillEmpty != null) {
                        txtCurrentSkillEmpty.setText("Belum ada keahlian aktif.");
                        txtCurrentSkillEmpty.setVisible(true);
                    }
                } else {
                    if (txtCurrentSkillEmpty != null) txtCurrentSkillEmpty.setVisible(false);
                    for (DeviceCategoryDto cat : myCats) {
                        if (layoutCurrentSkills != null) {
                            layoutCurrentSkills.getChildren().add(buildSkillRow(cat, true));
                        }
                    }
                }

                // Populate available skills
                if (availableCats.isEmpty()) {
                    if (txtAvailableSkillEmpty != null) {
                        txtAvailableSkillEmpty.setText("Semua kategori sudah menjadi keahlian kamu.");
                        txtAvailableSkillEmpty.setVisible(true);
                    }
                } else {
                    if (txtAvailableSkillEmpty != null) txtAvailableSkillEmpty.setVisible(false);
                    for (DeviceCategoryDto cat : availableCats) {
                        if (layoutAvailableSkills != null) {
                            layoutAvailableSkills.getChildren().add(buildSkillRow(cat, false));
                        }
                    }
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private HBox buildSkillRow(DeviceCategoryDto cat, boolean isCurrent) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(10);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px; -fx-border-width: 1px;");

        Label nameLabel = new Label(cat.getName() != null ? cat.getName() : "Kategori");
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        Button actionBtn = new Button();
        if (isCurrent) {
            actionBtn.setText("Hapus");
            actionBtn.setStyle("-fx-background-color: #FFE5E5; -fx-text-fill: #EF4444; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-padding: 6 12; -fx-cursor: hand;");
            actionBtn.setOnAction(e -> handleRemoveSkill(cat));
        } else {
            actionBtn.setText("Tambah");
            actionBtn.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0284C7; -fx-background-radius: 8px; -fx-font-weight: bold; -fx-padding: 6 12; -fx-cursor: hand;");
            actionBtn.setOnAction(e -> handleAddSkill(cat));
        }

        row.getChildren().addAll(nameLabel, actionBtn);
        return row;
    }

    private void handleAddSkill(DeviceCategoryDto cat) {
        Thread t = new Thread(() -> {
            boolean ok = TechnicianService.addDeviceCategory(cat.getDeviceCategoryId());
            Platform.runLater(() -> {
                if (ok) {
                    loadSkills();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menambah keahlian.");
                    alert.showAndWait();
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }

    private void handleRemoveSkill(DeviceCategoryDto cat) {
        Thread t = new Thread(() -> {
            boolean ok = TechnicianService.removeDeviceCategory(cat.getDeviceCategoryId());
            Platform.runLater(() -> {
                if (ok) {
                    loadSkills();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menghapus keahlian.");
                    alert.showAndWait();
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void handleBack() {
        try {
            Main.setRoot("/com/teknisio/fxml/TechnicianHome.fxml");
        } catch (IOException e) {
            System.err.println("Failed to navigate back: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadSkills();
    }
}