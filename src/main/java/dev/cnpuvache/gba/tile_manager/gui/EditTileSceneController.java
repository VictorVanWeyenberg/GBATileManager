package dev.cnpuvache.gba.tile_manager.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class EditTileSceneController extends TitledPane {

    @FXML
    private Canvas cvsPalette;

    @FXML
    private Canvas cvsTile;

    @FXML
    private Slider sldrPalette;

    public EditTileSceneController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditTileScene.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    @FXML
    void cvsPaletteMouseClicked(MouseEvent event) {

    }

    @FXML
    void cvsTileMouseClicked(MouseEvent event) {

    }

}
