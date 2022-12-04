package dev.cnpuvache.gba.tile_manager.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.io.IOException;

public class NewMapSceneController extends TitledPane {

    @FXML
    private TextField textFieldMapName;

    @FXML
    private Label labelError;

    private final MainSceneInterface mainScene;
    private final Stage stage;

    public NewMapSceneController(MainSceneInterface mainScene) throws IOException {
        this.mainScene = mainScene;
        this.stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewMapScene.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
        textFieldMapName.requestFocus();
    }

    @FXML
    void buttonCancelOnAction(ActionEvent event) {
        stage.close();
    }

    @FXML
    void buttonCreateOnAction(ActionEvent event) {
        createMap();
    }

    private void createMap() {
        String mapName = textFieldMapName.getText();
        if (mapName == null || mapName.isBlank()) {
            labelError.setText("Map name is empty.");
            return;
        }
        mainScene.createMap(mapName);
        stage.close();
    }

}
