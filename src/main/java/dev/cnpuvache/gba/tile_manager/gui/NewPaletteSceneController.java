package dev.cnpuvache.gba.tile_manager.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class NewPaletteSceneController extends TitledPane {

    private final Stage stage;
    private final MainSceneInterface mainScene;
    private final boolean backgroundNotObject;
    @FXML
    private Button cancelButton;

    @FXML
    private Button createButton;

    @FXML
    private CheckBox makeDefaultCheckBox;

    @FXML
    private TextField paletteNameTextField;

    @FXML
    private TitledPane titledPane;

    @FXML
    private Label errorLabel;

    public NewPaletteSceneController(MainSceneInterface mainScene, boolean backgroundNotObject) throws IOException {
        this.stage = new Stage();
        this.mainScene = mainScene;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewPaletteScene.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
        this.backgroundNotObject = backgroundNotObject;
        if (backgroundNotObject) {
            titledPane.setText("New Background Palette");
        } else {
            titledPane.setText("New Object Palette");
        }
        paletteNameTextField.requestFocus();
    }

    @FXML
    void cancelButtonOnAction(ActionEvent event) {
        this.stage.close();
    }

    @FXML
    void createButtonOnAction(ActionEvent event) {
        createPalette();
    }

    @FXML
    void paletteNameTextFieldKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            createPalette();
        }
    }

    private void createPalette() {
        String paletteName = paletteNameTextField.getText();
        if (paletteName == null || paletteName.isBlank()) {
            errorLabel.setText("Palette Name must be filled in.");
            return;
        }
        boolean makeDefault = makeDefaultCheckBox.isSelected();
        if (backgroundNotObject) {
            if (mainScene.getBackgroundPaletteNames().contains(paletteName)) {
                errorLabel.setText(String.format("Background palette %s already exists in this project.", paletteName));
                return;
            }
            mainScene.addBackgroundPalette(paletteName, makeDefault);
        } else {
            if (mainScene.getObjectPaletteNames().contains(paletteName)) {
                errorLabel.setText(String.format("Object palette %s already exists in this project.", paletteName));
                return;
            }
            mainScene.addObjectPalette(paletteName, makeDefault);
        }
        this.stage.close();
    }

}

