package com.cnr.kochrezepte.controller;

import com.cnr.kochrezepte.model.Rezept;
import com.cnr.kochrezepte.util.AlertUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Button navRezepteButton;

    @FXML
    private Button navKategorienButton;

    @FXML
    private void initialize() {
        showRezeptListe();
    }

    @FXML
    private void onNavRezepte() {
        showRezeptListe();
    }

    @FXML
    private void onNavKategorien() {
        showKategorieVerwaltung();
    }

    public void showRezeptListe() {
        FXMLLoader loader = loadView("/fxml/rezept-liste.fxml");
        if (loader != null) {
            RezeptListController controller = loader.getController();
            controller.setMainController(this);
        }
        markActive(navRezepteButton, navKategorienButton);
    }

    public void showRezeptForm(Rezept rezeptZumBearbeiten) {
        FXMLLoader loader = loadView("/fxml/rezept-form.fxml");
        if (loader != null) {
            RezeptFormController controller = loader.getController();
            controller.setMainController(this);
            if (rezeptZumBearbeiten != null) {
                controller.loadRezeptForEdit(rezeptZumBearbeiten);
            }
        }
        markActive(navRezepteButton, navKategorienButton);
    }

    public void showKategorieVerwaltung() {
        loadView("/fxml/kategorie.fxml");
        markActive(navKategorienButton, navRezepteButton);
    }

    private void markActive(Button aktiv, Button inaktiv) {
        aktiv.getStyleClass().removeAll("nav-inactive");
        if (!aktiv.getStyleClass().contains("nav-active")) {
            aktiv.getStyleClass().add("nav-active");
        }
        inaktiv.getStyleClass().removeAll("nav-active");
        if (!inaktiv.getStyleClass().contains("nav-inactive")) {
            inaktiv.getStyleClass().add("nav-inactive");
        }
    }

    private FXMLLoader loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);
            return loader;
        } catch (IOException e) {
            AlertUtil.showError("Ansicht konnte nicht geladen werden: " + fxmlPath, e);
            return null;
        }
    }
}