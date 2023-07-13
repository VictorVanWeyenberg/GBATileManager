package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.*;
import dev.cnpuvache.gba.tile_manager.gui.format.ScreenConverter;
import dev.cnpuvache.gba.tile_manager.util.TileToImageConverter;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
    private Component component;

    @FXML
    void initialize() {
        ctx = componentsCanvas.getGraphicsContext2D();
        ctx.setImageSmoothing(false);
        screensListView.setCellFactory(lv -> {
            TextFieldListCell<Screen> cell = new TextFieldListCell<>();//
            cell.setConverter(new ScreenConverter(cell, this::setError));
            return cell;
        });
        screensListView.setEditable(false);
        screensListView.getSelectionModel().selectedItemProperty().addListener(this::screenListViewSelectedItemChanged);
        componentsCanvasPane.widthProperty().addListener(this::canvasPaneSizeChanged);
        componentsCanvasPane.heightProperty().addListener(this::canvasPaneSizeChanged);
        componentsCanvas.setOnMousePressed(this::mousePressed);
        componentsCanvas.setOnMouseDragged(this::mouseDragged);
        componentsCanvas.setOnMouseReleased(this::mouseReleased);
    }

    @FXML
    void resolveComponentsButtonOnAction(ActionEvent event) {
        String screenName = screensListView.getSelectionModel().getSelectedItem().getName();
        project.resolveComponents(screenName);
        draw();
    }

    @FXML
    void resetComponentsButtonOnAction(ActionEvent event) {
        String screenName = screensListView.getSelectionModel().getSelectedItem().getName();
        project.resetComponents(screenName);
        draw();
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
        draw();
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
        ctx.setLineWidth(3);
        Screen selectedItem = screensListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        String screenName = selectedItem.getName();
        for (Component component : project.getComponents(screenName)) {
            double beginX = (double) component.getBeginX() / 240 * componentsCanvas.getWidth();
            double beginY = (double) component.getBeginY() / 160 * componentsCanvas.getHeight();
            double endX = (double) (component.getEndX() + 8) / 240 * componentsCanvas.getWidth();
            double endY = (double) (component.getEndY() + 8) / 160 * componentsCanvas.getHeight();
            double width = Math.abs(endX - beginX);
            double height = Math.abs(endY - beginY);
            ctx.setStroke(Color.MAGENTA);
            ctx.strokeRect(beginX, beginY, width, height);
            if (component.getNorth() != null) {
                Component north = component.getNorth();
                double componentX = component.getBeginX();
                componentX = componentX + ((component.getEndX() - componentX) / 3);
                double componentY = component.getBeginY();
                double northX = north.getBeginX();
                northX = northX + ((north.getEndX() - northX) / 3);
                double northY = north.getEndY();
                drawRelation(componentX, componentY, northX, northY, Color.RED);
            }
            if (component.getEast() != null) {
                Component east = component.getEast();
                double componentX = component.getEndX();
                double componentY = component.getBeginY();
                componentY = componentY + ((component.getEndY() - componentY) / 3);
                double eastX = east.getBeginX();
                double eastY = east.getBeginY();
                eastY = eastY + ((east.getEndY() - eastY) / 3);
                drawRelation(componentX, componentY, eastX, eastY, Color.YELLOW);
            }
            if (component.getSouth() != null) {
                Component south = component.getSouth();
                double componentX = component.getEndX();
                componentX = componentX - ((componentX - component.getBeginX()) / 3);
                double componentY = component.getEndY();
                double southX = south.getEndX();
                southX = southX - ((southX - south.getBeginX()) / 3);
                double southY = south.getBeginY();
                drawRelation(componentX, componentY, southX, southY, Color.GREEN);
            }
            if (component.getWest() != null) {
                Component west = component.getWest();
                double componentX = component.getBeginX();
                double componentY = component.getEndY();
                componentY = componentY - ((componentY - component.getBeginY()) / 3);
                double westX = west.getEndX();
                double westY = west.getEndY();
                westY = westY - ((westY - west.getBeginY()) / 3);
                drawRelation(componentX, componentY, westX, westY, Color.BLUE);
            }
        }
    }

    private void drawRelation(double x1, double y1, double x2, double y2, Color color) {
        System.out.printf("%f %f %f %f\n", x1, y1, x2, y2);
        ctx.setStroke(color);
        ctx.strokeLine(
                x1 / 240 * componentsCanvas.getWidth(),
                y1 / 160 * componentsCanvas.getHeight(),
                x2 / 240 * componentsCanvas.getWidth(),
                y2 / 160 * componentsCanvas.getHeight()
        );
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

    private void mousePressed(MouseEvent e) {
        Screen selectedItem = screensListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        int beginX = ((int) (e.getX() / componentsCanvas.getWidth() * 30)) * 8;
        int beginY = ((int) (e.getY() / componentsCanvas.getHeight() * 20)) * 8;
        int endX = beginX;
        int endY = beginY;
        String screenName = selectedItem.getName();
        if (e.getButton() == MouseButton.PRIMARY) {
            component = project.assignComponent(screenName, beginX, beginY, endX, endY);
            draw();
        }
        if (e.getButton() == MouseButton.SECONDARY) {
            project.removeComponent(screenName, beginX, beginY);
            draw();
        }
    }

    private void mouseDragged(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            int endX = ((int) (e.getX() / componentsCanvas.getWidth() * 30)) * 8;
            if (endX != component.getEndX()) {
                component.setEndX(endX);
                draw();
            }
        }
    }

    private void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY && component != null) {
            component = null;
            draw();
        }
    }

    public void selected() {
        updateListView();
        draw();
    }
}
