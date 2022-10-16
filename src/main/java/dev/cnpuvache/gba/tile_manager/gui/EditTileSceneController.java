package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;

public class EditTileSceneController extends TitledPane {

    @FXML
    private Canvas cvsPalette;

    @FXML
    private Canvas cvsTile;

    @FXML
    private Slider sldrPalette;

    private final Project project;

    private GraphicsContext cvsPaletteGCtx, cvsTileGCtx;

    private Tile tile;
    private int paletteNumber;
    private boolean backgroundNotObject;

    private final Color GRAYSTROKE = new Color(0.5, 0.5, 0.5, 1),
            AQUASTROKE = new Color(0, 1, 1, 1);
    private int selectedTilePixel;
    private int selectedColor;

    public EditTileSceneController(Project project) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditTileScene.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

        this.project = project;

        cvsPaletteGCtx = cvsPalette.getGraphicsContext2D();
        cvsTileGCtx = cvsTile.getGraphicsContext2D();
        sldrPalette.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number selected) {
                setPaletteNumber(selected.intValue());
            }
        });

        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        showPalette();
        autosize();
    }

    private void setSize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    public Palette16 getPalette16() {
        Palette palette = project.getDefaultBackgroundPalette();
        if (palette == null) {
            System.out.println("Default background palette is null.");
            return null;
        }
        Project.PaletteType paletteType = project.getPaletteType();
        if (paletteType == null) {
            System.out.println("Palette type is null.");
            return null;
        }
        if (palette instanceof Palette16 && paletteType == Project.PaletteType.PALETTE256) {
            System.out.println("Palette type does not match palette object type.");
            return null;
        }
        if (!(palette instanceof Palette16)) {
            System.out.println("Palette not of type Palette16.");
            return null;
        }
        return (Palette16) palette;
    }

    public void setPaletteNumber(int paletteNumber) {
        if (this.backgroundNotObject) {
            Palette16 palette16 = getPalette16();
            if (palette16 == null) return;
            if (palette16.getPalette(paletteNumber) == null) return;
            this.paletteNumber = paletteNumber;
            showPalette();
        }
    }

    private void showPalette() {
        System.out.println("Showing palette...");
        Palette16 palette16 = getPalette16();
        if (palette16 == null) {
            return;
        }
        int colorIndex = 0;
        int rowIndex = 0;
        int colorWidth = (int) (cvsPalette.getWidth() / 4);
        for (RGB15 color : palette16.getPalette(paletteNumber)) {
            this.cvsPaletteGCtx.setFill(color.getColor());
            this.cvsPaletteGCtx.setStroke(rowIndex * 4 + colorIndex == selectedColor ? AQUASTROKE : GRAYSTROKE);
            this.cvsPaletteGCtx.fillRect(colorIndex * colorWidth, rowIndex * colorWidth, colorWidth, colorWidth);
            this.cvsPaletteGCtx.strokeRect(colorIndex * colorWidth, rowIndex * colorWidth, colorWidth, colorWidth);
            colorIndex = ++colorIndex % 4;
            if (colorIndex == 0) rowIndex++;
        }
    }

    public void setTile(Tile tile, boolean backgroundNotObject) {
        this.tile = tile;
        this.backgroundNotObject = backgroundNotObject;
        showTile();
        showPalette();
    }

    private void showTile() {
        if (tile == null) return;
        Palette16 palette = getPalette16();
        if (palette == null) {
            return;
        }
        int pixelWidth = (int) (cvsTile.getWidth() / 8);
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int pixel = tile.getTileData(x, y);
                Color fill = palette.getPalette(paletteNumber).get(pixel).getColor();
                cvsTileGCtx.setFill(fill);
                cvsTileGCtx.setStroke(y * 8 + x == selectedTilePixel ? AQUASTROKE : GRAYSTROKE);
                cvsTileGCtx.fillRect(x * pixelWidth, y * pixelWidth, pixelWidth, pixelWidth);
                cvsTileGCtx.strokeRect(x * pixelWidth, y * pixelWidth, pixelWidth, pixelWidth);
            }
        }
    }

    @FXML
    void cvsPaletteMouseClicked(MouseEvent event) {
        int pixelWidth = (int) (cvsPalette.getWidth() / 4);
        int y = (int) Math.floor(event.getY() / pixelWidth);
        int x = (int) Math.floor(event.getX() / pixelWidth);
        this.selectedColor = y * 4 + x;
        System.out.println(selectedColor);
        showPalette();
    }

    @FXML
    void cvsTileMouseClicked(MouseEvent event) {
        int pixelWidth = (int) (cvsTile.getWidth() / 8);
        int y = (int) Math.floor(event.getY() / pixelWidth);
        int x = (int) Math.floor(event.getX() / pixelWidth);
        this.selectedTilePixel = y * 8 + x;
        System.out.println(selectedTilePixel);
        showTile();
    }
}
