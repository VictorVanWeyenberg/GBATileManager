/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.Palette;
import dev.cnpuvache.gba.tile_manager.domain.Palette16;
import dev.cnpuvache.gba.tile_manager.domain.RGB15;
import java.io.IOException;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author Reznov
 */
public class EditPaletteSceneController extends BorderPane {

    @FXML
    private Slider sldrPalette;
    @FXML
    private Label lblColor;
    @FXML
    private Label lblError;
    @FXML
    private TextField txfColorRed;
    @FXML
    private TextField txfColorGreen;
    @FXML
    private TextField txfColorBlue;
    @FXML
    private Canvas cvsColor;
    @FXML
    private Canvas cvsPalette;
    @FXML
    private Button btnSetColor;
    
    private GraphicsContext cvsPaletteGCtx, cvsColorGCtx;
    private Palette16 palette;
    private static int colorWidth = 40;
    private int selectedPalette = 0, selectedIndex = 0;
    private final Color GRAYSTROKE = new Color(0.5, 0.5, 0.5, 1),
            AQUASTROKE = new Color(0, 1, 1, 1);

    private Parent parent;

    private boolean listenersSet = false;

    public EditPaletteSceneController() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditPaletteScene.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();

        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        colorWidth = (int) (getWidth() / 4);
    }

    public void setPalette(Palette palette) {
        this.palette = (Palette16) palette;
        showPalette(selectedPalette);
        if (!listenersSet) {
            addListeners();
        }
    }

    private void addListeners() {
        sldrPalette.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            resetError();
            showPalette(newValue.intValue());
        });
        cvsPalette.setOnMouseClicked((MouseEvent event) -> {
            resetError();
            int y = (int) Math.floor(event.getY() / colorWidth);
            int x = (int) Math.floor(event.getX() / colorWidth);
            if (x > 3 || y > 3) return;
            showPalette(selectedPalette, y * 4 + x);
        });
        btnSetColor.setOnAction((ActionEvent event) -> {
            int r = Integer.valueOf(txfColorRed.getText());
            int g = Integer.valueOf(txfColorGreen.getText());
            int b = Integer.valueOf(txfColorBlue.getText());
            try {
                palette.setColor(selectedPalette, selectedIndex, new RGB15(r, g, b));
            } catch (IllegalArgumentException ex) {
                lblError.setText(ex.getMessage());
            }
            showPalette(selectedPalette, selectedIndex);
        });
    }
    
    private void resetError() {
        lblError.setText("");
    }

    private void showPalette(int palette, Integer selectedColor) {
        int colorIndex = 0;
        int rowIndex = 0;
        if (this.cvsPaletteGCtx == null) {
            this.cvsPaletteGCtx = this.cvsPalette.getGraphicsContext2D();
        }
        colorWidth = (int) (cvsPalette.getWidth() / 4);
        for (RGB15 color : ((Palette16) this.palette).getPalette(palette)) {
            this.cvsPaletteGCtx.setFill(color.getColor());
            this.cvsPaletteGCtx.setStroke(GRAYSTROKE);
            this.cvsPaletteGCtx.fillRect(colorIndex * colorWidth, rowIndex * colorWidth, colorWidth, colorWidth);
            this.cvsPaletteGCtx.strokeRect(colorIndex * colorWidth, rowIndex * colorWidth, colorWidth, colorWidth);
            colorIndex = ++colorIndex % 4;
            if (colorIndex == 0) rowIndex++;
        }
        if (selectedColor != null) {
            selectColor(selectedColor.intValue());
        } else {
            selectColor(0);
        }
        selectedPalette = palette;
    }
    
    private void showPalette(int palette) {
        showPalette(palette, null);
    }

    private void selectColor(int index) {
        RGB15 color = ((Palette16) this.palette).getPalette(selectedPalette).get(index);
        if (this.cvsColorGCtx == null) {
            this.cvsColorGCtx = this.cvsColor.getGraphicsContext2D();
        }
        this.cvsColorGCtx.setFill(color.getColor());
        this.cvsColorGCtx.fillRect(0, 0, 100, 100);
        if (this.cvsPaletteGCtx == null) {
            this.cvsPaletteGCtx = this.cvsPalette.getGraphicsContext2D();
        }
        colorWidth = (int) (cvsPalette.getWidth() / 4);
        this.cvsPaletteGCtx.setStroke(AQUASTROKE);
        this.cvsPaletteGCtx.strokeRect((index % 4) * colorWidth, Math.floor(index / 4) * colorWidth, colorWidth, colorWidth);
        lblColor.setText(color.toString());
        txfColorRed.setText(String.valueOf(color.getR()));
        txfColorGreen.setText(String.valueOf(color.getG()));
        txfColorBlue.setText(String.valueOf(color.getB()));
        selectedIndex = index;
    }
    
}
