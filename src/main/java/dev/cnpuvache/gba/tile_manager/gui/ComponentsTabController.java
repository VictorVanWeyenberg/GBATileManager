package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.*;
import dev.cnpuvache.gba.tile_manager.gui.format.ScreenConverter;
import dev.cnpuvache.gba.tile_manager.persistence.CachingManager;
import dev.cnpuvache.gba.tile_manager.util.TileToImageConverter;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ComponentsTabController {

    @FXML
    private Canvas componentsCanvas;

    @FXML
    private Pane componentsCanvasPane;

    @FXML
    private ListView<Screen> screensListView;

    @FXML
    private ChoiceBox<String> callbacksChoiceBox;

    @FXML
    private TextField argsTextField;

    private Project project;
    private GraphicsContext ctx;
    private Component component;
    private int selectedComponentIndex = -1;

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
        componentsCanvas.setOnMouseClicked(this::mouseClicked);
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

    @FXML
    void loadCallbackHeaderOnAction(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose callback header file.");
        File file = chooser.showOpenDialog(new Stage());
        Optional.ofNullable(file).ifPresent(this::parseHeader);
    }

    private void parseHeader(File headerFile) {
        try {
            String headerContent = Files.readString(headerFile.toPath());
            Matcher matcher = Pattern.compile("(?<=\\n|^)(?:\\w+\\s*\\**\\s+)+(\\w+)\\s*\\([^;]*;")
                    .matcher(headerContent);
            List<String> matches = new ArrayList<>();
            while (matcher.find()) {
                matches.add(matcher.group(1));
            }
            callbacksChoiceBox.setItems(FXCollections.observableList(matches));
            if (!matches.isEmpty()) {
                CachingManager.getInstance().setLatestLoadedCallbackHeaderFile(headerFile);
            }
        } catch (IOException e) {
            System.out.println("Could not read header file. " + headerFile.getAbsolutePath());
        }
    }

    @FXML
    void setArgsButtonOnAction(ActionEvent event) {
        if (selectedComponentIndex < 0) {
            return;
        }
        Screen screen = screensListView.getSelectionModel().getSelectedItem();
        if (screen == null) {
            return;
        }
        String screenName = screen.getName();
        List<Component> components = project.getComponents(screenName);
        String callback = callbacksChoiceBox.getSelectionModel().getSelectedItem();
        if (callback != null && !callback.isBlank()) {
            components.get(selectedComponentIndex)
                    .setCallbackIndex(callbacksChoiceBox.getSelectionModel().getSelectedIndex());
        }
        String argsText = argsTextField.getText();
        if (argsText != null && !argsText.isBlank()) {
            List<Integer> args = Arrays.stream(argsText.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            components.get(selectedComponentIndex)
                    .setArgs(args);
        }
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
        List<Component> components = project.getComponents(screenName);
        for (Component component : components) {
            double beginX = (double) component.getBeginX() / 240 * componentsCanvas.getWidth();
            double beginY = (double) component.getBeginY() / 160 * componentsCanvas.getHeight();
            double endX = (double) (component.getEndX() + 8) / 240 * componentsCanvas.getWidth();
            double endY = (double) (component.getEndY() + 8) / 160 * componentsCanvas.getHeight();
            double width = Math.abs(endX - beginX);
            double height = Math.abs(endY - beginY);
            ctx.setStroke(components.indexOf(component) == selectedComponentIndex ? Color.LIME : Color.MAGENTA);
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
        if (!e.isDragDetect()) {
            return;
        }
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

    private void mouseClicked(MouseEvent e) {
        Screen selectedItem = screensListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        String screenName = selectedItem.getName();
        List<Component> components = project.getComponents(screenName);
        int mx = ((int) (e.getX() / componentsCanvas.getWidth() * 30)) * 8;
        int my = ((int) (e.getY() / componentsCanvas.getWidth() * 30)) * 8;
        for (int index = 0; index < components.size(); index++) {
            Component component = components.get(index);
            if (mx >= component.getBeginX() && mx <= component.getEndX() && my >= component.getBeginY() && my <= component.getEndY()) {
                this.selectedComponentIndex = index;
                break;
            }
        }
        Component component = components.get(selectedComponentIndex);
        callbacksChoiceBox.getSelectionModel().select(component.getCallbackIndex());
        argsTextField.setText(component.getArgs().stream().map(String::valueOf).reduce((s1,s2)->s1+","+s2).orElse(""));
        draw();
    }

    public void selected() {
        CachingManager.getInstance()
                .getLatestLoadedCallbackHeaderFile()
                .ifPresent(this::parseHeader);
        updateListView();
        draw();
    }
}
