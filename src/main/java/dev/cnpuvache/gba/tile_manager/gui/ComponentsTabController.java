package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.*;
import dev.cnpuvache.gba.tile_manager.gui.format.ScreenConverter;
import dev.cnpuvache.gba.tile_manager.util.TileToImageConverter;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ComponentsTabController {

    @FXML
    private Canvas componentsCanvas;

    @FXML
    private Pane componentsCanvasPane;

    @FXML
    private ListView<Screen> screensListView;

    private Project project;
    private GraphicsContext ctx;

    @FXML
    void initialize() {
        ctx = componentsCanvas.getGraphicsContext2D();
        ctx.setImageSmoothing(false);
        screensListView.setCellFactory(lv -> {
            TextFieldListCell<Screen> cell = new TextFieldListCell<>();
            cell.setConverter(new ScreenConverter(cell, this::setError));
            return cell;
        });
        screensListView.setEditable(false);
        screensListView.getSelectionModel().selectedItemProperty().addListener(this::screenListViewSelectedItemChanged);
        componentsCanvasPane.widthProperty().addListener(this::canvasPaneSizeChanged);
        componentsCanvasPane.heightProperty().addListener(this::canvasPaneSizeChanged);
    }

    private void setError(String s) {

    }

    private void canvasPaneSizeChanged(ObservableValue<? extends Number> observableValue, Number t0, Number t1) {
        double paneWidth = componentsCanvasPane.getWidth();
        double paneHeight = componentsCanvasPane.getHeight();
        double canvasWidth = paneWidth;
        double canvasHeight = paneHeight;
        if (paneWidth < paneHeight * 2 / 3) {
            canvasHeight = paneWidth / 3 * 2;
        } else if (paneHeight < paneWidth / 3 * 2) {
            canvasWidth = paneHeight / 2 * 3;
        } else {
            canvasHeight = paneWidth / 3 * 2;
        }
        componentsCanvas.setWidth(canvasWidth);
        componentsCanvas.setHeight(canvasHeight);
        drawScreen();
    }

    public void setProject(Project project) {
        this.project = project;
        updateListView();
        canvasPaneSizeChanged(null, null, null);
    }

    private void updateListView() {
        List<Screen> screens = project.getScreens();
        screensListView.setItems(FXCollections.observableList(screens));
    }

    private void screenListViewSelectedItemChanged(ObservableValue<? extends Screen> observableValue, Screen t0, Screen t1) {
        draw();
    }

    private void draw() {
        drawScreen();
        drawComponents();
    }

    private void drawComponents() {
    }

    private void drawScreen() {
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            return;
        }

        ctx.setFill(Color.BLACK);
        ctx.fillRect(0, 0, componentsCanvas.getWidth(), componentsCanvas.getHeight());
        ctx.setFill(project.getBackgroundPalette().getPalette(0)[0].getColor());
        ctx.fillRect(0, 0, componentsCanvas.getWidth(), componentsCanvas.getHeight());

        double tileSize = componentsCanvas.getWidth() / 30;
        List<ScreensTabController.Backgrounds> backgroundsByLeastPriority = Arrays.asList(ScreensTabController.Backgrounds.values());
        Collections.reverse(backgroundsByLeastPriority);
        for (ScreensTabController.Backgrounds background : backgroundsByLeastPriority) {
            TileToImageConverter.clearCache();
            for (int y = 0; y < 20; y++) {
                for (int x = 0; x < 30; x++) {
                    ScreenEntry entry = screen.getEntry(background.index, x, y);
                    drawEntry(background, x, y, entry);
                }
            }
        }

        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 30; x++) {
                ctx.setStroke(Color.GRAY);
                ctx.setLineWidth(1.0);
                ctx.strokeRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }
    }

    private void drawEntry(ScreensTabController.Backgrounds backgroundNumber, int x, int y, ScreenEntry entry) {
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
        double tileSize = componentsCanvas.getWidth() / 30;

        ctx.drawImage(tileImage, x * tileSize, y * tileSize, tileSize, tileSize);
    }

}
