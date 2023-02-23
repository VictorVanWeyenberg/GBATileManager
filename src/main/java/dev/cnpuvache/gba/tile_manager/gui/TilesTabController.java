package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.Palette;
import dev.cnpuvache.gba.tile_manager.domain.Project;
import dev.cnpuvache.gba.tile_manager.domain.RGB15;
import dev.cnpuvache.gba.tile_manager.domain.Tile;
import dev.cnpuvache.gba.tile_manager.gui.format.TileConverter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.kohsuke.randname.RandomNameGenerator;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TilesTabController {

    private static final int PALETTE_COLOR_SIZE = 50;
    private static final RandomNameGenerator RNG = new RandomNameGenerator(new Random().nextInt());

    @FXML
    private Button addTileButton;

    @FXML
    private ChoiceBox<CharacterBlock> characterBlockChoiceBox;

    @FXML
    private Label errorLabel;

    @FXML
    private Button moveTileDownButton;

    @FXML
    private Button moveTileUpButton;

    @FXML
    private Canvas paletteCanvas;

    @FXML
    private Pane paletteCanvasPane;

    @FXML
    private Slider paletteSlider;

    @FXML
    private Button removeTileButton;

    @FXML
    private Canvas tileCanvas;

    @FXML
    private Pane tileCanvasPane;

    @FXML
    private ListView<Tile> tilesListView;

    private Project project;
    private GraphicsContext paletteCtx;
    private GraphicsContext tileCtx;
    private int selectedPaletteIndex = 0;
    private int selectedTilePixelIndex = 0;

    private enum CharacterBlock {
        BG0(0), BG1(1), BG2(2), BG3(3), OBJECTS(4);
        final int index;
        CharacterBlock(int index) { this.index = index; }
    }

    public void setProject(Project project) {
        this.project = project;
        updateListView();
        draw();
    }

    private Palette getPalette() {
        if (project == null) {
            return null;
        }
        if (characterBlockChoiceBox.getValue() == CharacterBlock.OBJECTS) {
            return project.getObjectPalette();
        } else {
            return project.getBackgroundPalette();
        }
    }

    @FXML
    void initialize() {
        setError(null);
        this.paletteCtx = paletteCanvas.getGraphicsContext2D();
        this.tileCtx = tileCanvas.getGraphicsContext2D();
        characterBlockChoiceBox.setItems(FXCollections.observableArrayList(CharacterBlock.BG0, CharacterBlock.BG1,
                CharacterBlock.BG2, CharacterBlock.BG3, CharacterBlock.OBJECTS));
        characterBlockChoiceBox.setValue(CharacterBlock.BG0);
        characterBlockChoiceBox.valueProperty().addListener(this::characterBlockChoiceBoxValueChanged);
        paletteSlider.valueProperty().addListener(this::paletteSliderValueChanged);
        tilesListView.getSelectionModel().selectedItemProperty().addListener(this::tilesListViewSelectedItemChanged);
        paletteCanvasPane.widthProperty().addListener(this::resizePaletteCanvas);
        paletteCanvasPane.heightProperty().addListener(this::resizePaletteCanvas);
        tileCanvasPane.widthProperty().addListener(this::resizeTileCanvas);
        tileCanvasPane.heightProperty().addListener(this::resizeTileCanvas);
        tilesListView.setEditable(true);
        tilesListView.setCellFactory(lv -> {
            TextFieldListCell<Tile> cell = new TextFieldListCell<>();
            cell.setConverter(new TileConverter(cell, this::setError));
            return cell;
        });
    }

    private void characterBlockChoiceBoxValueChanged(ObservableValue<? extends CharacterBlock> observableValue, CharacterBlock t0, CharacterBlock t1) {
        updateListView();
        draw();
    }

    private void paletteSliderValueChanged(ObservableValue<? extends Number> observableValue, Number t0, Number t1) {
        draw();
    }

    private void tilesListViewSelectedItemChanged(ObservableValue<? extends Tile> observableValue, Tile t0, Tile t1) {
        drawTile();
    }

    private void resizePaletteCanvas(ObservableValue<? extends Number> observableValue, Number t0, Number t1) {
        paletteCanvas.setWidth(paletteCanvasPane.getWidth());
        paletteCanvas.setHeight(paletteCanvasPane.getHeight());
        drawPalette();
    }

    private void resizeTileCanvas(ObservableValue<? extends Number> observableValue, Number t0, Number t1) {
        double width = tileCanvasPane.getWidth();
        double height = tileCanvasPane.getHeight();
        double size = Math.min(width, height);
        tileCanvas.setWidth(size);
        tileCanvas.setHeight(size);
        drawTile();
    }

    private void setError(String error) {
        if (error == null) {
            errorLabel.setText("");
            errorLabel.setDisable(true);
            return;
        }
        errorLabel.setDisable(false);
        errorLabel.setText(error);
    }

    @FXML
    void addTileButtonOnAction(ActionEvent event) {
        if (project == null) {
            setError("First open a project.");
            return;
        }
        CharacterBlock characterBlock = characterBlockChoiceBox.getValue();
        List<String> names = tilesListView.getItems().stream()
                .map(Tile::getName)
                .collect(Collectors.toList());
        String name = RNG.next();
        while (names.contains(name)) {
            name = RNG.next();
        }
        try {
            project.addTile(characterBlock.index, RNG.next());
            updateListView();
        } catch (IndexOutOfBoundsException e) {
            setError(e.getMessage());
        }
    }

    @FXML
    void moveTileDownButtonOnAction(ActionEvent event) {
        Tile tile = tilesListView.getSelectionModel().getSelectedItem();
        if (tile == null) {
            setError("No tile selected.");
            return;
        }
        CharacterBlock characterBlock = characterBlockChoiceBox.getValue();
        project.moveTileDown(characterBlock.index, tile.getName());
        updateListView();
        int index = tilesListView.getItems().indexOf(tile);
        tilesListView.getSelectionModel().select(index);
        tilesListView.getFocusModel().focus(index);
    }

    @FXML
    void moveTileUpButtonOnAction(ActionEvent event) {
        Tile tile = tilesListView.getSelectionModel().getSelectedItem();
        if (tile == null) {
            setError("No tile selected.");
            return;
        }
        CharacterBlock characterBlock = characterBlockChoiceBox.getValue();
        project.moveTileUp(characterBlock.index, tile.getName());
        updateListView();
        int index = tilesListView.getItems().indexOf(tile);
        tilesListView.getSelectionModel().select(index);
        tilesListView.getFocusModel().focus(index);
    }

    @FXML
    void paletteCanvasOnMouseClicked(MouseEvent event) {
        double mouseX = event.getX();
        double paletteColorSize = Math.min(PALETTE_COLOR_SIZE, paletteCanvas.getHeight());
        int index = (int) (mouseX * 16 / (paletteColorSize * 16));
        if (index < 0 || index > 15) {
            return;
        }
        selectedPaletteIndex = index;
        drawPalette();
    }

    @FXML
    void removeTileButtonOnAction(ActionEvent event) {
        Tile tile = tilesListView.getSelectionModel().getSelectedItem();
        if (tile == null) {
            setError("No tile selected.");
            return;
        }
        CharacterBlock characterBlock = characterBlockChoiceBox.getValue();
        project.removeTile(characterBlock.index, tile.getName());
        updateListView();
    }

    @FXML
    void tileCanvasOnMouseClicked(MouseEvent event) {
        Tile tile = tilesListView.getSelectionModel().getSelectedItem();
        if (tile == null) {
            setError("No tile selected.");
            return;
        }
        double mouseX = event.getX();
        double mouseY = event.getY();
        double tileCanvasSize = tileCanvas.getWidth();
        int x = (int) (mouseX * 8 / tileCanvasSize);
        int y = (int) (mouseY * 8 / tileCanvasSize);
        selectedTilePixelIndex = y * 8 + x;
        tile.setTileData(x, y, selectedPaletteIndex);
        drawTile();
    }

    private void updateListView() {
        CharacterBlock characterBlock = characterBlockChoiceBox.getValue();
        List<Tile> tiles = project.getTiles(characterBlock.index);
        tilesListView.setItems(FXCollections.observableList(tiles));
    }

    private void draw() {
        drawPalette();
        drawTile();
    }

    private void drawPalette() {
        if (project == null) {
            setError("First open a project.");
            return;
        }
        int paletteNumber = (int) paletteSlider.getValue();
        Palette palette;
        if (characterBlockChoiceBox.getValue() == CharacterBlock.OBJECTS) {
            palette = project.getObjectPalette();
        } else {
            palette = project.getBackgroundPalette();
        }
        RGB15[] colors = palette.getPalette(paletteNumber);
        double paletteColorSize = Math.min(PALETTE_COLOR_SIZE, paletteCanvas.getHeight());
        for (int index = 0; index < colors.length; index++) {
            paletteCtx.setFill(colors[index].getColor());
            paletteCtx.fillRect(index * paletteColorSize, 0, paletteColorSize, paletteColorSize);
        }
        for (int index = 0; index < colors.length; index++) {
            paletteCtx.setStroke(Color.GRAY);
            paletteCtx.setLineWidth(1.0);
            paletteCtx.strokeRect(index * paletteColorSize, 0, paletteColorSize, paletteColorSize);
        }
        paletteCtx.setStroke(Color.CYAN);
        paletteCtx.setLineWidth(3.0);
        paletteCtx.strokeRect(selectedPaletteIndex * paletteColorSize, 0, paletteColorSize, paletteColorSize);
    }

    private void drawTile() {
        if (project == null) {
            setError("First open a project.");
            return;
        }

        Tile tile = tilesListView.getSelectionModel().getSelectedItem();
        if (tile == null) {
            return;
        }
        double canvasWidth = tileCanvas.getWidth();
        double canvasHeight = tileCanvas.getHeight();
        double pixelSize = Math.min(canvasWidth, canvasHeight) / 8;

        Palette palette = getPalette();
        int paletteNumber = (int) paletteSlider.getValue();
        RGB15[] colors = palette.getPalette(paletteNumber);

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int colorIndex = tile.getTileData(x, y);
                tileCtx.setFill(colors[colorIndex].getColor());
                tileCtx.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
            }
        }

        tileCtx.setStroke(Color.GRAY);
        tileCtx.setLineWidth(1.0);
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                tileCtx.strokeRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
            }
        }

        tileCtx.setStroke(Color.CYAN);
        tileCtx.setLineWidth(3.0);
        int x = selectedTilePixelIndex % 8;
        int y = selectedTilePixelIndex / 8;
        tileCtx.strokeRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
    }

    public void selected() {
        updateListView();
        draw();
    }

}

