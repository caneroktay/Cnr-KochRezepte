package com.cnr.kochrezepte.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Kleine Hilfsklasse fuer Fehlermeldungen/Bestaetigungen (siehe Pflichtenheft
 * Punkt 5: "Fehlerbehandlung: einfache Fehlermeldungen").
 */
public final class AlertUtil {

    private AlertUtil() {
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText("Fehler");
        alert.showAndWait();
    }

    public static void showError(String message, Exception e) {
        showError(message + "\n\n(" + e.getMessage() + ")");
    }

    public static void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static boolean showConfirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }
}