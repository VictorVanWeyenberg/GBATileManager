package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.Palette;
import dev.cnpuvache.gba.tile_manager.domain.Project;
import dev.cnpuvache.gba.tile_manager.domain.RGB15;
import dev.cnpuvache.gba.tile_manager.gui.format.IntRangeStringConverter;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class PalettesTabController {

    @FXML
    private TextField blueIntensityTextField;

    @FXML
    private Canvas canvas;

    @FXML
    private Pane canvasPane;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField greenIntensityTextField;

    @FXML
    private ChoiceBox<PaletteMode> paletteMode;

    @FXML
    private Slider paletteNumberSlider;

    @FXML
    private TextField redIntensityTextField;

    @FXML
    private Button setColorButton;

    private Project project;
    private GraphicsContext ctx;
    private int selectedIndex = 0;

    enum PaletteMode {
        BACKGROUND(0), OBJECT(1);
        final int index;
        PaletteMode(int index) {
            this.index = index;
        }
    }

    @FXML
    void initialize() {
        // TODO: move palette slider under palette mode.
        setError(null);
        ctx = canvas.getGraphicsContext2D();
        IntRangeStringConverter converter = new IntRangeStringConverter(0, 31, this::setError);
        redIntensityTextField.setTextFormatter(new TextFormatter<>(converter));
        greenIntensityTextField.setTextFormatter(new TextFormatter<>(converter));
        blueIntensityTextField.setTextFormatter(new TextFormatter<>(converter));
        paletteMode.setItems(FXCollections.observableArrayList(PaletteMode.BACKGROUND, PaletteMode.OBJECT));
        paletteMode.setValue(PaletteMode.BACKGROUND);
        paletteMode.valueProperty().addListener(this::paletteModeChanged);
        paletteNumberSlider.valueProperty().addListener(this::paletteNumberSliderChanged);
        canvasPane.widthProperty().addListener(this::canvasPaneSizeChanged);
        canvasPane.heightProperty().addListener(this::canvasPaneSizeChanged);
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

    private void canvasPaneSizeChanged(ObservableValue<? extends Number> observableValue, Number t0, Number t1) {
        double canvasPaneWidth = canvasPane.getWidth();
        double canvasPaneHeight = canvasPane.getHeight();
        double canvasSize = Math.min(canvasPaneWidth, canvasPaneHeight);
        canvas.setWidth(canvasSize);
        canvas.setHeight(canvasSize);
        draw();
    }

    private void paletteModeChanged(ObservableValue<? extends PaletteMode> observableValue, PaletteMode t0, PaletteMode t1) {
        updateEditColorPane();
        draw();
    }

    private void paletteNumberSliderChanged(ObservableValue<? extends Number> observableValue, Number t0, Number t1) {
        updateEditColorPane();
        draw();
    }

    public void setProject(Project project) {
        this.project = project;
        canvasPaneSizeChanged(null, null, null);
        updateEditColorPane();
    }

    @FXML
    void setColorButtonOnAction(ActionEvent event) {
        setError(null);

        if (project == null) {
            setError("First open a project.");
            return;
        }

        String redText = redIntensityTextField.getText();
        if (redText == null || redText.trim().isBlank()) {
            setError("Red intensity is empty.");
            return;
        }
        int r;
        try {
            r = Integer.parseInt(redText);
        } catch (NumberFormatException e) {
            setError("Red intensity must be a number.");
            return;
        }
        if (r < 0 || r > 31) {
            setError("Red intensity must be in range of [0:31].");
            return;
        }

        String greenText = greenIntensityTextField.getText();
        if (greenText == null || greenText.trim().isBlank()) {
            setError("Green intensity is empty.");
            return;
        }
        int g;
        try {
            g = Integer.parseInt(greenText);
        } catch (NumberFormatException e) {
            setError("Green intensity must be a number.");
            return;
        }
        if (g < 0 || g > 31) {
            setError("Green intensity must be in range of [0:31].");
            return;
        }

        String blueText = blueIntensityTextField.getText();
        if (blueText == null || blueText.trim().isBlank()) {
            setError("Blue intensity is empty.");
            return;
        }
        int b;
        try {
            b = Integer.valueOf(blueText);
        } catch (NumberFormatException e) {
            setError("Blue intensity must be a number.");
            return;
        }
        if (b < 0 || b > 31) {
            setError("Blue intensity must be in range of [0:31].");
            return;
        }

        Palette palette;
        if (paletteMode.getValue() == PaletteMode.BACKGROUND) {
            palette = project.getBackgroundPalette();
        } else {
            palette = project.getObjectPalette();
        }
        int paletteNumber = (int) paletteNumberSlider.getValue();

        try {
            RGB15 color = new RGB15(r, g, b);
            palette.setColor(paletteNumber, selectedIndex, color);
        } catch (Exception e) {
            setError(e.getMessage());
            return;
        }

        if (selectedIndex < 15) {
            selectedIndex++;
            updateEditColorPane();
        }
        draw();
        redIntensityTextField.requestFocus();
    }

    @FXML
    public void canvasMouseClicked(MouseEvent mouseEvent) {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();
        int x = (int) (mouseX * 4 / canvas.getWidth());
        int y = (int) (mouseY * 4 / canvas.getHeight());
        selectedIndex = y * 4 + x;
        updateEditColorPane();
        draw();
        redIntensityTextField.requestFocus();
    }

    private void updateEditColorPane() {
        if (project == null) {
            return;
        }
        Palette palette;
        if (paletteMode.getValue() == PaletteMode.BACKGROUND) {
            palette = project.getBackgroundPalette();
        } else {
            palette = project.getObjectPalette();
        }
        int paletteNumber = (int) paletteNumberSlider.getValue();
        redIntensityTextField.setText(Integer.toString(palette.getPalette(paletteNumber)[selectedIndex].getR()));
        greenIntensityTextField.setText(Integer.toString(palette.getPalette(paletteNumber)[selectedIndex].getG()));
        blueIntensityTextField.setText(Integer.toString(palette.getPalette(paletteNumber)[selectedIndex].getB()));
    }

    private void draw() {
        double canvasSize = canvas.getWidth();
        double colorSize = canvasSize / 4;
        PaletteMode mode = paletteMode.getValue();
        Palette palette;
        if (mode == PaletteMode.BACKGROUND) {
            palette = project.getBackgroundPalette();
        } else {
            palette = project.getObjectPalette();
        }
        int paletteNumber = (int) paletteNumberSlider.getValue();
        RGB15[] colors = palette.getPalette(paletteNumber);
        for (int index = 0; index < colors.length; index++) {
            RGB15 color = colors[index];
            ctx.setFill(color.getColor());
            double colorX = (index % 4) * colorSize;
            double colorY = (index / 4) * colorSize;
            ctx.fillRect(colorX, colorY, colorSize, colorSize);
        }
        for (int index = 0; index < colors.length; index++) {
            double colorX = (index % 4) * colorSize;
            double colorY = (index / 4) * colorSize;
            ctx.setStroke(Color.GRAY);
            ctx.setLineWidth(1);
            ctx.strokeRect(colorX, colorY, colorSize, colorSize);
        }
        double colorX = (selectedIndex % 4) * colorSize;
        double colorY = (selectedIndex / 4) * colorSize;
        ctx.setStroke(Color.CYAN);
        ctx.setLineWidth(3);
        ctx.strokeRect(colorX, colorY, colorSize, colorSize);
    }

    public void selected() {
    }
}
