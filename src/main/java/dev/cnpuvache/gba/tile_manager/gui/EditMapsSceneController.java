package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.stream.Collectors;

public class EditMapsSceneController extends TitledPane {

    @FXML
    private ToggleGroup BACKGROUNDS;

    @FXML
    private Pane canvasPane;

    @FXML
    private CheckBox checkBoxShowAllBackgrounds;

    @FXML
    private CheckBox checkHFlip;

    @FXML
    private CheckBox checkVFlip;

    @FXML
    private Canvas cvsBackground;

    @FXML
    private ListView<String> listViewTiles;

    @FXML
    private RadioButton radioBackground1;

    @FXML
    private RadioButton radioBackground2;

    @FXML
    private RadioButton radioBackground3;

    @FXML
    private RadioButton radioBackground4;

    @FXML
    private Slider sliderPalette;

    private final Project project;
    private BackgroundMap backgroundMap;
    private final GraphicsContext cvsBackgroundGCtx;

    public EditMapsSceneController(Project project) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditTileScene.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

        this.project = project;
        cvsBackgroundGCtx = cvsBackground.getGraphicsContext2D();
        updateView();
    }

    private void setMapSize() {
        double paneWidth = canvasPane.getLayoutBounds().getWidth();
        double paneHeight = canvasPane.getLayoutBounds().getHeight();
        double min = Math.max(Math.min(paneWidth, paneHeight), 200);
        if (cvsBackground.getWidth() != min) {
            cvsBackground.setWidth(min);
            cvsBackground.setHeight(min);
            showMap();
        }
    }

    private void showMap() {
        for (int backgroundNumber = 0; backgroundNumber < 4; backgroundNumber++) {
            ScreenData screenData = backgroundMap.getScreenData(backgroundNumber);

        }
    }

    @FXML
    void checkBoxShowAllBackgroundsOnAction(ActionEvent event) {

    }

    @FXML
    void checkHFlipOnAction(ActionEvent event) {

    }

    @FXML
    void checkVFlipOnAction(ActionEvent event) {

    }

    @FXML
    void radioBackground1OnAction(ActionEvent event) {

    }

    @FXML
    void radioBackground2OnAction(ActionEvent event) {

    }

    @FXML
    void radioBackground3OnAction(ActionEvent event) {

    }

    @FXML
    void radioBackground4OnAction(ActionEvent event) {

    }

    @FXML
    void listViewTilesOnMouseClicked(MouseEvent event) {
        int backgroundNumber = getSelectedBackground();
        if (backgroundNumber > -1) {
            String selectedTile = listViewTiles.getSelectionModel().getSelectedItem();
            if (selectedTile == null) listViewTiles.getSelectionModel().selectFirst();
            selectedTile = listViewTiles.getSelectionModel().getSelectedItem();
            if (selectedTile != null) {
                Tile tile = project.getBackgroundTile(backgroundNumber, selectedTile);
            }
        }
    }

    private int getSelectedBackground() {
        int backgroundNumber = -1;
        if (radioBackground1.isSelected()) backgroundNumber = 0;
        else if (radioBackground2.isSelected()) backgroundNumber = 1;
        else if (radioBackground3.isSelected()) backgroundNumber = 2;
        else if (radioBackground4.isSelected()) backgroundNumber = 3;
        return backgroundNumber;
    }

    private void updateView() {
        if (backgroundMap == null) return;
        for (int backgroundNumber = 0; backgroundNumber < 4; backgroundNumber++) {
            for (int y = 0; y < 64; y++) {
                for (int x = 0; x < 512; x++) {
                    ScreenEntry entry = backgroundMap.getScreenData(backgroundNumber).getEntry(x, y);
                    if (entry == null) continue;
                    // entry.
                }
            }
        }

        int backgroundNumber = getSelectedBackground();
        if (backgroundNumber < 0) return;
        listViewTiles.setItems(
                FXCollections.observableList(
                        project.getBackgroundTiles(backgroundNumber).stream()
                                .map(Tile::getName)
                                .collect(Collectors.toList())));
    }

    public void selectMap(String selected) {
        backgroundMap = project.getBackgroundMap(selected);
        updateView();
    }
}
