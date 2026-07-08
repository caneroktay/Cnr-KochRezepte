package com.cnr.kochrezepte.controller;

import com.cnr.kochrezepte.dao.KategorieDAO;
import com.cnr.kochrezepte.model.Kategorie;
import com.cnr.kochrezepte.util.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class KategorieController {

    @FXML private TextField nameField;
    @FXML private ListView<Kategorie> kategorieListView;

    private final KategorieDAO kategorieDAO = new KategorieDAO();
    private final ObservableList<Kategorie> kategorien = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        kategorieListView.setItems(kategorien);
        loadKategorien();
    }

    private void loadKategorien() {
        try {
            kategorien.setAll(kategorieDAO.findAll());
        } catch (SQLException e) {
            AlertUtil.showError("Kategorien konnten nicht geladen werden.", e);
        }
    }

    @FXML
    private void onAdd() {
        String name = nameField.getText();
        if (name == null || name.isBlank()) {
            AlertUtil.showWarning("Bitte einen Namen fuer die Kategorie angeben.");
            return;
        }
        try {
            Kategorie kategorie = new Kategorie();
            kategorie.setName(name.trim());
            kategorieDAO.insert(kategorie);
            nameField.clear();
            loadKategorien();
        } catch (SQLException e) {
            // z.B. Verletzung von UNIQUE(Name) - siehe Schema
            AlertUtil.showError("Kategorie konnte nicht angelegt werden (existiert der Name evtl. bereits?).", e);
        }
    }

    @FXML
    private void onDelete() {
        Kategorie ausgewaehlt = kategorieListView.getSelectionModel().getSelectedItem();
        if (ausgewaehlt == null) {
            AlertUtil.showWarning("Bitte zuerst eine Kategorie auswaehlen.");
            return;
        }
        boolean bestaetigt = AlertUtil.showConfirm(
                "Kategorie \"" + ausgewaehlt.getName() + "\" wirklich loeschen?\n" +
                "Zugehoerige Rezepte bleiben erhalten und verlieren nur ihre Kategorie.");
        if (!bestaetigt) {
            return;
        }
        try {
            kategorieDAO.delete(ausgewaehlt.getId());
            loadKategorien();
        } catch (SQLException e) {
            AlertUtil.showError("Kategorie konnte nicht geloescht werden.", e);
        }
    }
}
