package com.cnr.kochrezepte.controller;

import com.cnr.kochrezepte.dao.KategorieDAO;
import com.cnr.kochrezepte.dao.RezeptDAO;
import com.cnr.kochrezepte.model.Kategorie;
import com.cnr.kochrezepte.model.Rezept;
import com.cnr.kochrezepte.model.Zutat;
import com.cnr.kochrezepte.util.AlertUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class RezeptListController {

    @FXML private TextField searchField;
    @FXML private ComboBox<Kategorie> kategorieFilter;
    @FXML private TableView<Rezept> tableView;
    @FXML private TableColumn<Rezept, String> titelColumn;
    @FXML private TableColumn<Rezept, String> kategorieColumn;
    @FXML private TableColumn<Rezept, Number> zeitColumn;

    @FXML private Label detailTitelLabel;
    @FXML private Label detailInfoLabel;
    @FXML private ListView<Zutat> zutatenListView;
    @FXML private TextArea zubereitungArea;

    private final RezeptDAO rezeptDAO = new RezeptDAO();
    private final KategorieDAO kategorieDAO = new KategorieDAO();
    private final ObservableList<Rezept> rezeptListe = FXCollections.observableArrayList();

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        titelColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitel()));
        kategorieColumn.setCellValueFactory(data -> {
            Kategorie k = data.getValue().getKategorie();
            return new SimpleStringProperty(k != null ? k.getName() : "-");
        });
        zeitColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(
                data.getValue().getZubereitungszeit()));

        tableView.setItems(rezeptListe);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, alt, neu) -> zeigeDetail(neu));

        ladeKategorieFilter();
        loadRezepte();
    }

    private void ladeKategorieFilter() {
        try {
            List<Kategorie> kategorien = kategorieDAO.findAll();
            kategorieFilter.getItems().add(null); // "Alle Kategorien"
            kategorieFilter.getItems().addAll(kategorien);
        } catch (SQLException e) {
            AlertUtil.showError("Kategorien konnten nicht geladen werden.", e);
        }
    }

    public void loadRezepte() {
        try {
            rezeptListe.setAll(rezeptDAO.findAll());
        } catch (SQLException e) {
            AlertUtil.showError("Rezepte konnten nicht geladen werden.", e);
        }
    }

    @FXML
    private void onSearch() {
        String keyword = searchField.getText();
        try {
            if (keyword == null || keyword.isBlank()) {
                loadRezepte();
            } else {
                rezeptListe.setAll(rezeptDAO.search(keyword));
            }
        } catch (SQLException e) {
            AlertUtil.showError("Suche fehlgeschlagen.", e);
        }
    }

    @FXML
    private void onFilterChanged() {
        Kategorie ausgewaehlt = kategorieFilter.getValue();
        try {
            List<Rezept> alle = rezeptDAO.findAll();
            if (ausgewaehlt == null) {
                rezeptListe.setAll(alle);
            } else {
                rezeptListe.setAll(alle.stream()
                        .filter(r -> r.getKategorie() != null && r.getKategorie().getId() == ausgewaehlt.getId())
                        .toList());
            }
        } catch (SQLException e) {
            AlertUtil.showError("Filter konnte nicht angewendet werden.", e);
        }
    }

    @FXML
    private void onNewRezept() {
        mainController.showRezeptForm(null);
    }

    @FXML
    private void onEditRezept() {
        Rezept ausgewaehlt = tableView.getSelectionModel().getSelectedItem();
        if (ausgewaehlt == null) {
            AlertUtil.showWarning("Bitte zuerst ein Rezept auswaehlen.");
            return;
        }
        mainController.showRezeptForm(ausgewaehlt);
    }

    @FXML
    private void onDeleteRezept() {
        Rezept ausgewaehlt = tableView.getSelectionModel().getSelectedItem();
        if (ausgewaehlt == null) {
            AlertUtil.showWarning("Bitte zuerst ein Rezept auswaehlen.");
            return;
        }
        boolean bestaetigt = AlertUtil.showConfirm(
                "Rezept \"" + ausgewaehlt.getTitel() + "\" wirklich loeschen?");
        if (!bestaetigt) {
            return;
        }
        try {
            rezeptDAO.delete(ausgewaehlt.getId());
            loadRezepte();
        } catch (SQLException e) {
            AlertUtil.showError("Rezept konnte nicht geloescht werden.", e);
        }
    }

    private void zeigeDetail(Rezept rezept) {
        if (rezept == null) {
            detailTitelLabel.setText("");
            detailInfoLabel.setText("");
            zutatenListView.getItems().clear();
            zubereitungArea.setText("");
            return;
        }
        detailTitelLabel.setText(rezept.getTitel());
        String kategorieName = rezept.getKategorie() != null ? rezept.getKategorie().getName() : "ohne Kategorie";
        detailInfoLabel.setText(kategorieName + " \u00b7 " + rezept.getZubereitungszeit() + " Min.");
        zutatenListView.setItems(FXCollections.observableArrayList(rezept.getZutaten()));
        zubereitungArea.setText(rezept.getZubereitung());
    }
}
