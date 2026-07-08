package com.cnr.kochrezepte.controller;

import com.cnr.kochrezepte.dao.KategorieDAO;
import com.cnr.kochrezepte.dao.RezeptDAO;
import com.cnr.kochrezepte.dao.ZutatDAO;
import com.cnr.kochrezepte.model.Kategorie;
import com.cnr.kochrezepte.model.Rezept;
import com.cnr.kochrezepte.model.Zutat;
import com.cnr.kochrezepte.util.AlertUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.cnr.kochrezepte.util.MengeFormatter;

import java.sql.SQLException;

public class RezeptFormController {

    @FXML private Label formTitelLabel;
    @FXML private TextField titelField;
    @FXML private ComboBox<Kategorie> kategorieBox;
    @FXML private Spinner<Integer> zeitSpinner;
    @FXML private TextArea beschreibungField;
    @FXML private TextArea zubereitungField;

    @FXML private TextField neueZutatName;
    @FXML private TextField neueZutatMenge;
    @FXML private TextField neueZutatEinheit;
    @FXML private TableView<Zutat> zutatenTable;
    @FXML private TableColumn<Zutat, String> zutatNameColumn;
    @FXML private TableColumn<Zutat, String> zutatMengeColumn;
    @FXML private TableColumn<Zutat, String> zutatEinheitColumn;

    private final RezeptDAO rezeptDAO = new RezeptDAO();
    private final ZutatDAO zutatDAO = new ZutatDAO();
    private final KategorieDAO kategorieDAO = new KategorieDAO();
    private final ObservableList<Zutat> zutatenListe = FXCollections.observableArrayList();

    private Rezept currentRezept; // null = neues Rezept, sonst wird bearbeitet
    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        zutatNameColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        zutatMengeColumn.setCellValueFactory(d -> new SimpleStringProperty(MengeFormatter.format(d.getValue().getMenge())));
        zutatEinheitColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEinheit()));
        zutatenTable.setItems(zutatenListe);

        zeitSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 600, 30, 5));

        try {
            kategorieBox.getItems().add(null); 
            kategorieBox.getItems().addAll(kategorieDAO.findAll());
        } catch (SQLException e) {
            AlertUtil.showError("Kategorien konnten nicht geladen werden.", e);
        }

        SpinnerValueFactory.IntegerSpinnerValueFactory zeitFactory =
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 600, 30, 5);
        zeitFactory.setConverter(new javafx.util.StringConverter<Integer>() {
        @Override
        public String toString(Integer value) {
            return value == null ? "0" : value.toString();
        }

        @Override
        public Integer fromString(String text) {
            if (text == null || text.isBlank()) {
                return 0;
            }
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException e) {
                Integer aktuell = zeitFactory.getValue();
                return aktuell != null ? aktuell : 0;
            }
        }
    });
zeitSpinner.setValueFactory(zeitFactory);

zeitSpinner.focusedProperty().addListener((obs, warFokussiert, istFokussiert) -> {
    if (!istFokussiert) {
        zeitSpinner.increment(0);
    }
});


    }

    /**
     * Befuellt das Formular mit einem bestehenden Rezept (Bearbeiten-Modus).
     */
    public void loadRezeptForEdit(Rezept rezept) {
        this.currentRezept = rezept;
        formTitelLabel.setText("Rezept bearbeiten");
        titelField.setText(rezept.getTitel());
        beschreibungField.setText(rezept.getBeschreibung());
        zubereitungField.setText(rezept.getZubereitung());
        kategorieBox.setValue(rezept.getKategorie());
        zeitSpinner.getValueFactory().setValue(rezept.getZubereitungszeit());
        zutatenListe.setAll(rezept.getZutaten());
    }

    @FXML
    private void onAddZutat() {
        String name = neueZutatName.getText();
        if (name == null || name.isBlank()) {
            AlertUtil.showWarning("Bitte einen Namen fuer die Zutat angeben.");
            return;
        }
        double menge;
        try {
            menge = neueZutatMenge.getText() == null || neueZutatMenge.getText().isBlank()
                    ? 0
                    : Double.parseDouble(neueZutatMenge.getText().replace(",", "."));
        } catch (NumberFormatException e) {
            AlertUtil.showWarning("Menge muss eine Zahl sein.");
            return;
        }
        String einheit = neueZutatEinheit.getText();

        zutatenListe.add(new Zutat(name, menge, einheit));

        neueZutatName.clear();
        neueZutatMenge.clear();
        neueZutatEinheit.clear();
    }

    @FXML
    private void onRemoveZutat() {
        Zutat ausgewaehlt = zutatenTable.getSelectionModel().getSelectedItem();
        if (ausgewaehlt == null) {
            AlertUtil.showWarning("Bitte zuerst eine Zutat auswaehlen.");
            return;
        }
        zutatenListe.remove(ausgewaehlt);
    }

    @FXML
    private void onSave() {
        if (titelField.getText() == null || titelField.getText().isBlank()) {
            AlertUtil.showWarning("Titel darf nicht leer sein.");
            return;
        }

        boolean istNeu = (currentRezept == null);
        Rezept rezept = istNeu ? new Rezept() : currentRezept;

        rezept.setTitel(titelField.getText());
        rezept.setBeschreibung(beschreibungField.getText());
        rezept.setZubereitung(zubereitungField.getText());
        rezept.setKategorie(kategorieBox.getValue());
        rezept.setZubereitungszeit(zeitSpinner.getValue());
        rezept.setZutaten(new java.util.ArrayList<>(zutatenListe));

        try {
            if (istNeu) {
                rezeptDAO.insert(rezept);
            } else {
                rezeptDAO.update(rezept);
                zutatDAO.deleteByRezeptId(rezept.getId());
                for (Zutat z : rezept.getZutaten()) {
                    z.setRezeptId(rezept.getId());
                    zutatDAO.insert(z);
                }
            }
            mainController.showRezeptListe();
        } catch (SQLException e) {
            AlertUtil.showError("Rezept konnte nicht gespeichert werden.", e);
        }
    }

    @FXML
    private void onCancel() {
        mainController.showRezeptListe();
    }
}