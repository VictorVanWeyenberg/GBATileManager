package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.*;
import dev.cnpuvache.gba.tile_manager.gui.format.ScreenConverter;
import dev.cnpuvache.gba.tile_manager.util.TileToImageConverter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.kohsuke.randname.RandomNameGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ScreensTabController {

    private static final int TILE_SIZE = 50;
    private static final RandomNameGenerator RNG = new RandomNameGenerator(new Random().nextInt());

    @FXML
    private ChoiceBox<Backgrounds> backgroundChoiceBox;

    @FXML
    private ChoiceBox<Backgrounds> characterDataBlockChoiceBox;

    @FXML
    private Label errorLabel;

    @FXML
    private CheckBox horizontalFlipCheckBox;

    @FXML
    private Slider paletteNumberSlider;

    @FXML
    private Canvas screenCanvas;

    @FXML
    private Pane screenCanvasPane;

    @FXML
    private ListView<Screen> screensListView;

    @FXML
    private CheckBox showGridsCheckBox;

    @FXML
    private Canvas tilesCanvas;

    @FXML
    private Pane tilesCanvasPane;

    @FXML
    private CheckBox verticalFlipChoiceBox;

    private GraphicsContext tilesCtx;
    private GraphicsContext screenCtx;
    private Project project;
    private int selectedTileIndex = 0;
    private int selectedBackgroundTileIndex = 0;

    enum Backgrounds {
        BG0(0), BG1(1), BG2(2), BG3(3);
        final int index;
        private Backgrounds(int index) { this.index = index; }
        static Backgrounds fromIndex(int index) {
            for (Backgrounds backgrounds : Backgrounds.values()) {
                if (backgrounds.index == index) return backgrounds;
            }
            return BG0;
        }
    }

    public void setProject(Project project) {
        this.project = project;
        updateListView();
        draw();
    }

    private void setError(String error) {
        if (error == null) {
            errorLabel.setDisable(true);
            errorLabel.setText("");
            return;
        }
        errorLabel.setDisable(false);
        errorLabel.setText(error);
    }

    @FXML
    void initialize() {
        // TODO: default tile is not used when null screen entries are drawn.
        // TODO: show grid checkbox.
        tilesCtx = tilesCanvas.getGraphicsContext2D();
        screenCtx = screenCanvas.getGraphicsContext2D();
        tilesCtx.setImageSmoothing(false);
        screenCtx.setImageSmoothing(false);
        screensListView.setCellFactory(lv -> {
            TextFieldListCell<Screen> cell = new TextFieldListCell<>();
            cell.setConverter(new ScreenConverter(cell, this::setError));
            return cell;
        });
        screensListView.setEditable(true);
        screensListView.getSelectionModel().selectedItemProperty().addListener(this::screenListViewSelectedItemChanged);
        backgroundChoiceBox.setItems(FXCollections.observableArrayList(Backgrounds.BG0, Backgrounds.BG1, Backgrounds.BG2, Backgrounds.BG3));
        backgroundChoiceBox.setValue(Backgrounds.BG0);
        backgroundChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::backgroundChoiceBoxValueChanged);
        characterDataBlockChoiceBox.setItems(FXCollections.observableArrayList(Backgrounds.BG0, Backgrounds.BG1, Backgrounds.BG2, Backgrounds.BG3));
        characterDataBlockChoiceBox.setValue(Backgrounds.BG0);
        characterDataBlockChoiceBox.getSelectionModel().selectedItemProperty().addListener(this::characterDataBlockChoiceBoxValueChanged);
        tilesCanvasPane.widthProperty().addListener(this::tilesCanvasPaneSizeChanged);
        tilesCanvasPane.heightProperty().addListener(this::tilesCanvasPaneSizeChanged);
        screenCanvasPane.widthProperty().addListener(this::screenCanvasPaneSizeChanged);
        screenCanvasPane.heightProperty().addListener(this::screenCanvasPaneSizeChanged);
        paletteNumberSlider.valueProperty().addListener(this::paletteNumberSliderValueChanged);
        horizontalFlipCheckBox.selectedProperty().addListener(this::horizontalFlipCheckBoxValueChanged);
        verticalFlipChoiceBox.selectedProperty().addListener(this::verticalFlipCheckBoxValueChanged);
        showGridsCheckBox.setSelected(true);
        showGridsCheckBox.selectedProperty().addListener(this::showGridsCheckBoxValueChanged);
    }

    private void screenListViewSelectedItemChanged(ObservableValue<? extends Screen> observableValue, Screen t0, Screen t1) {
        draw();
    }

    private void backgroundChoiceBoxValueChanged(ObservableValue<? extends Backgrounds> observableValue, Backgrounds t0, Backgrounds t1) {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            setError("No screen selected.");
            return;
        }

        Backgrounds background = backgroundChoiceBox.getValue();
        int characterDataIndex = screen.getCharacterBlockIndexOfScreenDataIndex(background.index);
        characterDataBlockChoiceBox.setValue(Backgrounds.fromIndex(characterDataIndex));
        draw();
        updateEditPane();
    }

    private void characterDataBlockChoiceBoxValueChanged(ObservableValue<? extends Backgrounds> observableValue, Backgrounds t0, Backgrounds t1) {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            setError("No screen selected.");
            return;
        }

        Backgrounds background = backgroundChoiceBox.getValue();
        Backgrounds characterBaseBlock = characterDataBlockChoiceBox.getValue();
        screen.setCharacterBlockIndexOfScreenDataIndex(background.index, characterBaseBlock.index);
        draw();
    }

    private void tilesCanvasPaneSizeChanged(ObservableValue<? extends Number> observableValue, Number t0, Number t1) {
        tilesCanvas.setWidth(tilesCanvasPane.getWidth());
        tilesCanvas.setHeight(tilesCanvasPane.getHeight());
        drawTiles();
    }

    private void screenCanvasPaneSizeChanged(ObservableValue<? extends Number> observableValue, Number t0, Number t1) {
        double paneWidth = screenCanvasPane.getWidth();
        double paneHeight = screenCanvasPane.getHeight();
        double canvasWidth = paneWidth;
        double canvasHeight = paneHeight;
        if (paneWidth < paneHeight * 2 / 3) {
            canvasHeight = paneWidth / 3 * 2;
        } else if (paneHeight < paneWidth / 3 * 2) {
            canvasWidth = paneHeight / 2 * 3;
        } else {
            canvasHeight = paneWidth / 3 * 2;
        }
        screenCanvas.setWidth(canvasWidth);
        screenCanvas.setHeight(canvasHeight);
        drawScreen();
    }

    private void paletteNumberSliderValueChanged(ObservableValue<? extends Number> observableValue, Number t0, Number t1) {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            setError("No screen selected.");
            return;
        }

        Backgrounds background = backgroundChoiceBox.getValue();
        int x = selectedBackgroundTileIndex % 30;
        int y = selectedBackgroundTileIndex / 30;

        ScreenEntry entry = screen.getEntry(background.index, x, y);

        int paletteNumber = (int) paletteNumberSlider.getValue();
        entry.setPaletteNumber(paletteNumber);

        draw();
    }

    private void horizontalFlipCheckBoxValueChanged(ObservableValue<? extends Boolean> observableValue, Boolean t0, Boolean t1) {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            setError("No screen selected.");
            return;
        }

        Backgrounds background = backgroundChoiceBox.getValue();
        int x = selectedBackgroundTileIndex % 30;
        int y = selectedBackgroundTileIndex / 30;

        ScreenEntry entry = screen.getEntry(background.index, x, y);

        boolean horizontalFlip = horizontalFlipCheckBox.isSelected();
        entry.setHorizontalFlip(horizontalFlip);

        draw();
    }

    private void verticalFlipCheckBoxValueChanged(ObservableValue<? extends Boolean> observableValue, Boolean t0, Boolean t1) {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            setError("No screen selected.");
            return;
        }

        Backgrounds background = backgroundChoiceBox.getValue();
        int x = selectedBackgroundTileIndex % 30;
        int y = selectedBackgroundTileIndex / 30;

        ScreenEntry entry = screen.getEntry(background.index, x, y);

        boolean verticalFlip = verticalFlipChoiceBox.isSelected();
        entry.setVerticalFlip(verticalFlip);

        draw();
    }

    private void showGridsCheckBoxValueChanged(ObservableValue<? extends Boolean> observableValue, Boolean t0, Boolean t1) {
        drawScreen();
    }

    @FXML
    void addScreenButtonOnAction(ActionEvent event) {
        if (project == null) {
            setError("First open a project.");
            return;
        }
        project.createScreen(RNG.next());
        updateListView();
    }

    @FXML
    void moveScreenDownButtonOnAction(ActionEvent event) {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            setError("No screen selected.");
            return;
        }
        project.moveScreenDown(screen);
        updateListView();
        int index = screensListView.getItems().indexOf(screen);
        screensListView.getSelectionModel().select(index);
        screensListView.getFocusModel().focus(index);
    }

    @FXML
    void moveScreenUpButtonOnAction(ActionEvent event) {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            setError("No screen selected.");
            return;
        }
        project.moveScreenUp(screen);
        updateListView();
        int index = screensListView.getItems().indexOf(screen);
        screensListView.getSelectionModel().select(index);
        screensListView.getFocusModel().focus(index);
    }

    @FXML
    void removeScreenButtonOnAction(ActionEvent event) {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            setError("No screen selected.");
            return;
        }
        project.removeScreen(screen);
        updateListView();
    }

    @FXML
    void screenCanvasOnMouseClicked(MouseEvent event) {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            setError("No screen selected.");
            return;
        }
        double mouseX = event.getX();
        double mouseY = event.getY();

        int x = (int) (mouseX / screenCanvas.getWidth() * 30);
        int y = (int) (mouseY / screenCanvas.getHeight() * 20);

        selectedBackgroundTileIndex = y * 30 + x;

        Backgrounds background = backgroundChoiceBox.getValue();
        ScreenEntry entry = screen.getEntry(background.index, x, y);
        if (entry == null) {
            entry = new ScreenEntry(
                    selectedTileIndex,
                    horizontalFlipCheckBox.isSelected(),
                    verticalFlipChoiceBox.isSelected(),
                    (int) paletteNumberSlider.getValue()
            );
            screen.setEntry(background.index, x, y, entry);
        } else {
            entry.setTileNumber(selectedTileIndex);
        }

        updateEditPane();
        draw();
    }

    @FXML
    void tilesCanvasOnMouseClicked(MouseEvent event) {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            setError("No screen selected.");
            return;
        }
        Backgrounds background = backgroundChoiceBox.getValue();
        int characterBlockIndex = screen.getCharacterBlockIndexOfScreenDataIndex(background.index);
        List<Tile> tiles = project.getTiles(characterBlockIndex);

        double mouseX = event.getX();
        double mouseY = event.getY();

        int x = (int) (mouseX / tilesCanvas.getWidth() * (tilesCanvas.getWidth() / TILE_SIZE));
        int y = (int) (mouseY / tilesCanvas.getHeight() * (tilesCanvas.getHeight() / TILE_SIZE));

        int index = (int) (y * (tilesCanvas.getWidth() / TILE_SIZE) + x);
        if (index >= tiles.size()) {
            return;
        }
        selectedTileIndex = index;

        draw();
    }

    private void updateEditPane() {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            setError("No screen selected.");
            return;
        }

        Backgrounds backgrounds = backgroundChoiceBox.getValue();
        int x = selectedBackgroundTileIndex % 30;
        int y = selectedBackgroundTileIndex / 30;
        ScreenEntry entry = screen.getEntry(backgrounds.index, x, y);
        if (entry == null) {
            // selectedTileIndex = 0;
            horizontalFlipCheckBox.setSelected(false);
            verticalFlipChoiceBox.setSelected(false);
            paletteNumberSlider.setValue(0);
        } else {
            // selectedTileIndex = entry.getTileNumber();
            horizontalFlipCheckBox.setSelected(entry.isHorizontalFlip());
            verticalFlipChoiceBox.setSelected(entry.isVerticalFlip());
            paletteNumberSlider.setValue(entry.getPaletteNumber());
        }
    }

    private void updateListView() {
        List<Screen> screens = project.getScreens();
        screensListView.setItems(FXCollections.observableList(screens));
    }

    private Palette getPalette() {
        if (project == null) {
            return null;
        }
        return project.getBackgroundPalette();
    }

    private void draw() {
        drawTiles();
        drawScreen();
    }

    private void drawTiles() {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            setError("No screen selected.");
            return;
        }
        Backgrounds background = characterDataBlockChoiceBox.getValue();
        List<Tile> tiles = project.getTiles(background.index);
        Palette palette = getPalette();
        if (palette == null) {
            return;
        }
        int paletteNumber = (int) paletteNumberSlider.getValue();
        RGB15[] colors = palette.getPalette(paletteNumber);
        if (colors == null) {
            return;
        }
        RGB15 backgroundColor = palette.getBackgroundColor();

        tilesCtx.setFill(Color.BLACK);
        tilesCtx.fillRect(0, 0, tilesCanvas.getWidth(), tilesCanvas.getHeight());
        tilesCtx.setFill(project.getBackgroundPalette().getPalette(0)[0].getColor());
        tilesCtx.fillRect(0, 0, tilesCanvas.getWidth(), tilesCanvas.getHeight());

        int x = 0;
        int y = 0;
        int index = 0;
        for (Tile tile : tiles) {
            TileToImageConverter.clearCache();
            Image image = TileToImageConverter.tileToImage(tile, colors, false, false, paletteNumber, index, backgroundColor);
            tilesCtx.drawImage(image, x, y, TILE_SIZE, TILE_SIZE);
            x += TILE_SIZE;
            if (x + TILE_SIZE > tilesCanvas.getWidth()) {
                x = 0;
                y += TILE_SIZE;
            }
            index++;
        }
        x = 0;
        y = 0;
        for (Tile tile : tiles) {
            tilesCtx.setStroke(Color.GRAY);
            tilesCtx.setLineWidth(1.0);
            tilesCtx.strokeRect(x, y, TILE_SIZE, TILE_SIZE);
            x += TILE_SIZE;
            if (x + TILE_SIZE > tilesCanvas.getWidth()) {
                x = 0;
                y += TILE_SIZE;
            }
        }

        x = selectedTileIndex % (int) (tilesCanvas.getWidth() / TILE_SIZE);
        y = selectedTileIndex / (int) (tilesCanvas.getWidth() / TILE_SIZE);
        tilesCtx.setStroke(Color.CYAN);
        tilesCtx.setLineWidth(3.0);
        tilesCtx.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    private void drawEntry(Backgrounds backgroundNumber, int x, int y, ScreenEntry entry) {
        if (project == null) {
            return;
        }

        int tileNumber;
        int paletteNumber;
        boolean horizontalFlip;
        boolean verticalFlip;
        if (entry == null) {
            tileNumber = 0;
            paletteNumber = 0;
            horizontalFlip = false;
            verticalFlip = false;
        } else {
            tileNumber = entry.getTileNumber();
            paletteNumber = entry.getPaletteNumber();
            horizontalFlip = entry.isHorizontalFlip();
            verticalFlip = entry.isVerticalFlip();
        }

        Palette palette = project.getBackgroundPalette();
        RGB15[] colors = palette.getPalette(paletteNumber);
        List<Tile> tiles = project.getTiles(backgroundNumber.index);
        Tile tile;
        try {
            tile = tiles.get(tileNumber);
            if (tile == null) {
                tile = Tile.DEFAULT;
            }
        } catch (IndexOutOfBoundsException e) {
            tile = Tile.DEFAULT;
        }

        RGB15 backgroundRGB15 = palette.getBackgroundColor();
        Image tileImage =
                TileToImageConverter.tileToImage(tile, colors, horizontalFlip, verticalFlip, paletteNumber, tileNumber,
                        backgroundRGB15);
        double tileSize = screenCanvas.getWidth() / 30;

        screenCtx.drawImage(tileImage, x * tileSize, y * tileSize, tileSize, tileSize);
    }

    private synchronized void drawScreen() {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            return;
        }

        screenCtx.setFill(Color.BLACK);
        screenCtx.fillRect(0, 0, screenCanvas.getWidth(), screenCanvas.getHeight());
        screenCtx.setFill(project.getBackgroundPalette().getPalette(0)[0].getColor());
        screenCtx.fillRect(0, 0, screenCanvas.getWidth(), screenCanvas.getHeight());

        double tileSize = screenCanvas.getWidth() / 30;
        List<Backgrounds> backgroundsByLeastPriority = Arrays.asList(Backgrounds.values());
        Collections.reverse(backgroundsByLeastPriority);
        for (Backgrounds background : backgroundsByLeastPriority) {
            TileToImageConverter.clearCache();
            for (int y = 0; y < 20; y++) {
                for (int x = 0; x < 30; x++) {
                    ScreenEntry entry = screen.getEntry(background.index, x, y);
                    drawEntry(background, x, y, entry);
                }
            }
        }

        if (showGridsCheckBox.isSelected()) {
            for (int y = 0; y < 20; y++) {
                for (int x = 0; x < 30; x++) {
                    screenCtx.setStroke(Color.GRAY);
                    screenCtx.setLineWidth(1.0);
                    screenCtx.strokeRect(x * tileSize, y * tileSize, tileSize, tileSize);
                }
            }

            screenCtx.setStroke(Color.CYAN);
            screenCtx.setLineWidth(3.0);
            int x = selectedBackgroundTileIndex % 30;
            int y = selectedBackgroundTileIndex / 30;
            screenCtx.strokeRect(x * tileSize, y * tileSize, tileSize, tileSize);
        }
    }

    public void selected() {
        TileToImageConverter.reset();
        draw();
    }
}
