package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class NewTileSceneController extends TitledPane {

    private final Stage stage;
    private final boolean backgroundNotObject;
    private final MainSceneInterface mainScene;
    private final int backgroundNumber;
    private final boolean renaming;
    private String oldName;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField tileNameTextField;

    @FXML
    private TitledPane titledPane;

    public NewTileSceneController(MainSceneInterface mainScene) throws IOException {
        this(mainScene, -1, false, false);
    }

    public NewTileSceneController(MainSceneInterface mainScene, String currentTileName) throws IOException {
        this(mainScene, -1, false, true);
        this.oldName = currentTileName;
        this.tileNameTextField.setText(currentTileName);
        this.tileNameTextField.requestFocus();
        this.tileNameTextField.selectAll();
    }

    public NewTileSceneController(MainSceneInterface mainScene, int backgroundNumber) throws IOException {
        this(mainScene, backgroundNumber, true, false);
    }

    public NewTileSceneController(MainSceneInterface mainScene, int backgroundNumber, String currentTileName) throws IOException {
        this(mainScene, backgroundNumber, true, true);
        this.oldName = currentTileName;
        this.tileNameTextField.setText(currentTileName);
        this.tileNameTextField.requestFocus();
        this.tileNameTextField.selectAll();
    }

    private NewTileSceneController(MainSceneInterface mainScene, int backgroundNumber, boolean backgroundNotObject, boolean renaming) throws IOException {
        this.stage = new Stage();
        this.mainScene = mainScene;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewTileScene.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
        this.backgroundNotObject = backgroundNotObject;
        this.backgroundNumber = backgroundNumber;
        this.renaming = renaming;
        if (backgroundNotObject) {
            titledPane.setText("New Background Tile");
        } else {
            titledPane.setText("New Object Tile");
        }
        tileNameTextField.requestFocus();
    }

    @FXML
    void cancelButtonOnAction(ActionEvent event) {
        stage.close();
    }

    @FXML
    void createButtonOnAction(ActionEvent event) {
        createTile();
    }

    @FXML
    void tileNameTextFieldKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            createTile();
        }
    }

    private void createTile() {
        String tileName = tileNameTextField.getText();
        if (tileName == null || tileName.isBlank()) {
            logError("Please enter a tile name.");
            return;
        }
        if (backgroundNotObject) {
            if (mainScene.getBackgroundTileNames(backgroundNumber).contains(tileName)) {
                logError(String.format("Tile with name %s already exists on background %d.", tileName, backgroundNumber));
                return;
            }
            if (!renaming) {
                mainScene.addBackgroundTile(tileName, backgroundNumber);
            } else if (!tileName.equals(oldName)) {
                mainScene.renameBackgroundTile(oldName, tileName, backgroundNumber);
            }
        } else {
            if (mainScene.getObjectTileNames().contains(tileName)) {
                logError(String.format("Object with name %s already exists.", tileName));
                return;
            }
            if (!renaming) {
                mainScene.addObjectTile(tileName);
            } else if (!tileName.equals(oldName)) {
                mainScene.renameObjectTile(oldName, tileName);
            }
        }
        stage.close();
    }

    private void logError(String message) {
        errorLabel.setTextFill(Color.RED);
        errorLabel.setText(message);
    }

}
