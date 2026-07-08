package com.cnr.kochrezepte;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 960, 720);
        stage.setTitle("CNR Kochrezepte");
        stage.setScene(scene);
        stage.setMinWidth(760);
        stage.setMinHeight(680);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// Wenn Sie den Code direkt in der IDE ausführen möchten, 
// verwenden Sie bitte die Datei mainWrapper.java. ;)