/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.CachingManager;
import dev.cnpuvache.gba.tile_manager.domain.Palette16;
import dev.cnpuvache.gba.tile_manager.domain.Project;

import java.io.*;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import dev.cnpuvache.gba.tile_manager.domain.Tile;
import dev.cnpuvache.gba.tile_manager.io.ProjectJsonConverter;
import javafx.collections.FXCollections;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Reznov
 */
public class MainSceneController extends BorderPane implements MainSceneInterface {

    // region: ATTRIBUTES ----------------------------------------------------------------------------------------------

    // region: FXML ATTRIBUTES -----------------------------------------------------------------------------------------
    
    @FXML
    private MenuItem menuItemNewProject;
    @FXML
    private MenuItem menuItemOpen;
    @FXML
    private MenuItem menuItemSaveAs;
    @FXML
    private MenuItem menuItemSave;
    @FXML
    private MenuItem menuItemCompileToC;
    @FXML
    private ListView<String> listViewObjectPalettes;
    @FXML
    private ListView<String> listViewBackgroundPalettes;
    @FXML
    private ListView<String> listViewObjectTiles;
    @FXML
    private ListView<String> listViewBackgroundTiles;
    @FXML
    private ListView<String> listViewMaps;
    @FXML
    private Label altLabel;
    @FXML
    private Accordion accordion;
    @FXML
    private ChoiceBox<Backgrounds> choiceBoxBackgroundTiles;
    // endregion
    private Project currentProject;
    private EditPaletteSceneController editPaletteSceneController;
    private EditTileSceneController editTileSceneController;
    // endregion

    public MainSceneController(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainScene.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        accordion.getPanes().get(0).setExpanded(true);
        listViewBackgroundPalettes.getSelectionModel().selectedItemProperty().addListener((o, s, v) -> { if (v != null && !v.equals(s)) selectBackgroundPalette(); });
        listViewObjectPalettes.getSelectionModel().selectedItemProperty().addListener((o, s, v) -> { if (v != null && !v.equals(s)) selectObjectPalette(); });
        listViewBackgroundTiles.getSelectionModel().selectedItemProperty().addListener((o, s, v) -> { if (v != null && !v.equals(s)) selectBackgroundTile(); });
        listViewObjectTiles.getSelectionModel().selectedItemProperty().addListener((o, s, v) -> { if (v != null && !v.equals(s)) selectObjectTile(); });
        choiceBoxBackgroundTiles.setItems(FXCollections.observableArrayList(Backgrounds.values()));
        choiceBoxBackgroundTiles.getSelectionModel().selectFirst();
        choiceBoxBackgroundTiles.getSelectionModel().selectedIndexProperty().addListener((o, n, v) -> updateView());
    }

    // region: 0 MAIN SCENE STUFF --------------------------------------------------------------------------------------

    // region: 0.1 FXML LISTENERS --------------------------------------------------------------------------------------

    @FXML
    public void mainOnKeyPressed(KeyEvent event) {
        if (event.isAltDown()) {
            switch (event.getText()) {
                case "1": case "&":
                    focusObjectPalettesListView();
                    break;
                case "2": case "é":
                    focusBackgroundPalettesListView();
                    break;
                case "3": case "\"":
                    focusObjectTilesListView();
                    break;
                case "4": case "'":
                    focusBackgroundTilesListView();
                    break;
            }
        }
    }

    // endregion

    private void updateView() {
        if (currentProject != null) {
            listViewObjectPalettes.setItems(FXCollections.observableArrayList(this.currentProject.getObjectPalettes().keySet()));
            listViewBackgroundPalettes.setItems(FXCollections.observableArrayList(this.currentProject.getBackgroundPalettes().keySet()));
            listViewObjectTiles.setItems(FXCollections.observableArrayList(this.currentProject.getObjectTiles().stream().map(Tile::getName).collect(Collectors.toList())));
            int backgroundIndex = choiceBoxBackgroundTiles.getSelectionModel().getSelectedIndex();
            listViewBackgroundTiles.setItems(FXCollections.observableArrayList(this.currentProject.getBackgroundTiles(backgroundIndex).stream().map(Tile::getName).collect(Collectors.toList())));
            listViewMaps.setItems(FXCollections.observableArrayList(currentProject.getBackgroundMapsNames()));
        }
        menuItemSave.setDisable(currentProject == null || currentProject.getLocation() == null);
        menuItemSaveAs.setDisable(currentProject == null);
        menuItemCompileToC.setDisable(currentProject == null);
    }

    private void altLog(String message) {
        altLabel.setTextFill(Color.BLACK);
        altLabel.setText(message);
    }

    private void altError(String message) {
        altLabel.setTextFill(Color.RED);
        altLabel.setText(message);
    }

    @Override
    public void setProject(Project project) {
        this.currentProject = project;
        updateView();
        altLog("Opened project " + project.getName());
    }

    // endregion

    // region: 1 MENU --------------------------------------------------------------------------------------------------

    @FXML
    public void menuItemNewProjectOnAction() {
        try {
            new NewProjectSceneController(this);
        } catch (IOException ex) {
            Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void menuItemOpenOnAction() {
        FileChooser chooser = new FileChooser();
        chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("GBA Project", "*.gbaproj"));
        File src = chooser.showOpenDialog(new Stage());
        if (src == null) {
            return;
        }
        try {
            byte[] data = Files.readAllBytes(src.toPath());
            currentProject = ProjectJsonConverter.fromJson(data);
            currentProject.setLocation(src);
            CachingManager.getInstance().setLatestOpenedProject(src);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            updateView();
        }
    }

    @FXML
    public void menuItemSaveOnAction() {
        if (this.currentProject == null || this.currentProject.getLocation() == null) {
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(this.currentProject.getLocation())) {
            byte[] data = ProjectJsonConverter.toJson(currentProject);
            int len = 1024;
            for (int off = 0; off < data.length; off += len) {
                if (off + len > data.length) {
                    len = data.length - off;
                }
                fos.write(data, off, len);
            }
            CachingManager.getInstance().setLatestOpenedProject(this.currentProject.getLocation());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void menuItemSaveAsOnAction() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(currentProject.getName());
        File dest = chooser.showSaveDialog(new Stage());
        this.currentProject.setLocation(dest);
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            byte[] data = ProjectJsonConverter.toJson(currentProject);
            int len = 1024;
            for (int off = 0; off < data.length; off += len) {
                if (off + len > data.length) {
                    len = data.length - off;
                }
                fos.write(data, off, len);
            }
            CachingManager.getInstance().setLatestOpenedProject(this.currentProject.getLocation());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void menuItemCompileToCOnAction() {
    }

    // endregion

    // region: 2 PALETTES ----------------------------------------------------------------------------------------------

    // region: 2.1 OBJECT PALETTES -------------------------------------------------------------------------------------

    // region: 2.1.1 FXML LISTENERS ------------------------------------------------------------------------------------

    @FXML
    public void listViewObjectPalettesMouseClicked(MouseEvent event) {
        selectObjectPalette();
    }

    @FXML
    public void btnAddObjectPaletteClicked() {
        if (currentProject == null) {
            altError("You can't add a palette until you've opened or created a project");
            return;
        }
        try {
            new NewPaletteSceneController(this, false);
        } catch (IOException ex) {
            Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void btnDeleteObjectPaletteClicked() {
        if (currentProject == null) {
            altError("You can't delete a palette until you've opened or created a project");
            return;
        }
        String selectedPalette = listViewObjectPalettes.getSelectionModel().getSelectedItem();
        if (selectedPalette == null || selectedPalette.isBlank()) {
            altLog("Select an object palette to delete.");
            return;
        }
        currentProject.deleteObjectPalette(selectedPalette);
        updateView();
    }

    @FXML
    public void btnMakeDefaultObjectPaletteClicked() {
        String selected = listViewObjectPalettes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        currentProject.setDefaultObjectPalette(selected);
    }

    // endregion

    @Override
    public void addObjectPalette(String paletteName, boolean makeDefault) {
        currentProject.addObjectPalette(paletteName, new Palette16(paletteName), makeDefault);
        updateView();
        altLog("Added object palette " + paletteName);
    }

    @Override
    public Collection<String> getObjectPaletteNames() {
        return listViewObjectPalettes.getItems();
    }

    private void selectObjectPalette() {
        try {
            String selected = listViewObjectPalettes.getSelectionModel().getSelectedItem();
            if (selected == null) listViewObjectPalettes.getSelectionModel().selectFirst();
            if (MainSceneController.this.editPaletteSceneController == null) {
                MainSceneController.this.editPaletteSceneController = new EditPaletteSceneController();
            }
            setCenter(editPaletteSceneController);
            MainSceneController.this.editPaletteSceneController.setPalette(currentProject.getObjectPalette(selected));
        } catch (IOException ex) {
            Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void focusObjectPalettesListView() {
        listViewObjectPalettes.requestFocus();
        TitledPane pane = accordion.getPanes().stream()
                .filter(p -> p.getText().toLowerCase().contains("palette"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Palette pane missing???"));
        pane.setExpanded(true);
        selectObjectPalette();
    }

    // endregion

    // region: 2.2 BACKGROUND PALETTES ---------------------------------------------------------------------------------

    // region: 2.2.1 FXML LISTENERS ------------------------------------------------------------------------------------

    @FXML
    public void listViewBackgroundPalettesMouseClicked(MouseEvent event) {
        selectBackgroundPalette();
    }

    @FXML
    public void btnAddBackgroundPaletteClicked() {
        if (currentProject == null) {
            altError("You can't add a palette until you've opened or created a project");
            return;
        }
        try {
            new NewPaletteSceneController(this, true);
        } catch (IOException ex) {
            Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void btnDeleteBackgroundPaletteClicked() {
        if (currentProject == null) {
            altError("You can't delete a palette until you've opened or created a project");
            return;
        }
        String selectedPalette = listViewObjectPalettes.getSelectionModel().getSelectedItem();
        if (selectedPalette == null || selectedPalette.isBlank()) {
            altLog("Select a background palette to delete.");
            return;
        }
        currentProject.deleteBackgroundPalette(selectedPalette);
        updateView();
    }

    @FXML
    public void btnMakeDefaultBackgroundPaletteClicked() {
        String selected = listViewBackgroundPalettes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        currentProject.setDefaultBackgroundPalette(selected);
    }

    // endregion

    @Override
    public void addBackgroundPalette(String paletteName, boolean makeDefault) {
        currentProject.addBackgroundPalette(paletteName, new Palette16(paletteName), makeDefault);
        updateView();
        altLog("Added background palette " + paletteName);
    }

    @Override
    public Collection<String> getBackgroundPaletteNames() {
        return listViewBackgroundPalettes.getItems();
    }

    private void selectBackgroundPalette() {
        try {
            String selected = listViewBackgroundPalettes.getSelectionModel().getSelectedItem();
            if (selected == null) listViewBackgroundPalettes.getSelectionModel().selectFirst();
            if (MainSceneController.this.editPaletteSceneController == null) {
                MainSceneController.this.editPaletteSceneController = new EditPaletteSceneController();
            }
            setCenter(editPaletteSceneController);
            MainSceneController.this.editPaletteSceneController.setPalette(currentProject.getBackgroundPalette(selected));
        } catch (IOException ex) {
            Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void focusBackgroundPalettesListView() {
        listViewBackgroundPalettes.requestFocus();
        TitledPane pane = accordion.getPanes().stream()
                .filter(p -> p.getText().toLowerCase().contains("palette"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Palette pane missing???"));
        pane.setExpanded(true);
        selectBackgroundPalette();
    }

    // endregion

    // endregion

    // region: 3 TILES -------------------------------------------------------------------------------------------------

    // region: 3.1 OBJECT TILES ----------------------------------------------------------------------------------------

    // region: 3.1.1 FXML LISTENERS ------------------------------------------------------------------------------------

    @FXML
    public void listViewObjectTilesMouseClicked() {
        selectObjectTile();
    }

    @FXML
    public void listViewObjectTilesKeyPressed(KeyEvent event) {
        String selectedTile = listViewObjectTiles.getSelectionModel().getSelectedItem();
        if (event.getCode() == KeyCode.R) {
            if (selectedTile == null || selectedTile.isBlank()) {
                altError("Select a tile to rename.");
                return;
            }
            try {
                new NewTileSceneController(this, selectedTile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (event.isAltDown()) {
            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
                if (selectedTile == null || selectedTile.isBlank()) {
                    altError("Select a tile to move.");
                    return;
                }
            }
            if (event.getCode() == KeyCode.UP) {
                currentProject.moveObjectTileUp(selectedTile);
            } else if (event.getCode() == KeyCode.DOWN) {
                currentProject.moveObjectTileDown(selectedTile);
            }
            updateView();
        }
    }

    @FXML
    public void btnAddObjectTileOnAction() {
        try {
            new NewTileSceneController(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void btnDeleteObjectTileOnAction() {
        if (currentProject == null) {
            altError("You can't delete a tile until you've opened or created a project");
            return;
        }
        String selectedTile = listViewObjectTiles.getSelectionModel().getSelectedItem();
        if (selectedTile == null || selectedTile.isBlank()) {
            altLog("Select a tile to delete.");
            return;
        }
        if (!currentProject.deleteObjectTileFromCharacterData(selectedTile)) {
            altError(String.format("Unable to remove tile %s from project object character data.", selectedTile));
        }
        updateView();
    }

    // endregion

    @Override
    public Collection<String> getObjectTileNames() {
        return currentProject.getObjectTiles().stream()
                .map(Tile::getName)
                .collect(Collectors.toList());
    }

    @Override
    public void addObjectTile(String tileName) {
        System.out.println("Adding object tile");
        if (getObjectTileNames().contains(tileName)) {
            altError(String.format("Object tile with name %s already exists.", tileName));
            return;
        }
        currentProject.addObjectTileToCharacterData(tileName);
        updateView();
        selectObjectTile(tileName);
        listViewObjectTiles.requestFocus();
    }

    private void selectObjectTile() {
        selectObjectTile(null);
    }

    private void selectObjectTile(String tileName) {
        try {
            if (tileName != null) listViewObjectTiles.getSelectionModel().select(tileName);
            String selected = listViewObjectTiles.getSelectionModel().getSelectedItem();
            if (selected == null) {
                listViewObjectTiles.getSelectionModel().selectFirst();
            }
            Tile tile = currentProject.getObjectTile(selected);
            if (tile == null) {
                return;
            }
            if (MainSceneController.this.editTileSceneController == null) {
                MainSceneController.this.editTileSceneController = new EditTileSceneController(currentProject);
            }
            setCenter(editTileSceneController);
            editTileSceneController.setTile(tile, false);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException e) {
            System.out.println("Unable to load Edit Tile Scene.");
        }
    }

    private void focusObjectTilesListView() {
        listViewObjectTiles.requestFocus();
        TitledPane pane = accordion.getPanes().stream()
                .filter(p -> p.getText().toLowerCase().contains("tile"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Tile pane missing???"));
        pane.setExpanded(true);
        selectObjectTile();
    }

    @Override
    public void renameObjectTile(String oldName, String newName) {
        currentProject.renameObjectTile(oldName, newName);
        updateView();
        selectObjectTile(newName);
    }

    // endregion

    // region: 3.2 BACKGROUND TILES ------------------------------------------------------------------------------------

    // region 3.2.1 FXML LISTENERS -------------------------------------------------------------------------------------

    @FXML
    public void listViewBackgroundTilesMouseClicked() {
        selectBackgroundTile();
    }

    @FXML
    public void listViewBackgroundTilesKeyPressed(KeyEvent event) {
        int selectedBackground = choiceBoxBackgroundTiles.getSelectionModel().getSelectedIndex();
        if (event.getCode() == KeyCode.R) {
            String selectedTile = listViewBackgroundTiles.getSelectionModel().getSelectedItem();
            if (selectedTile == null || selectedTile.isBlank()) {
                altError("Select a tile to rename.");
                return;
            }
            try {
                new NewTileSceneController(this, selectedBackground, selectedTile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (event.isAltDown()) {
            String selectedTile = listViewBackgroundTiles.getSelectionModel().getSelectedItem();
            if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
                if (selectedTile == null || selectedTile.isBlank()) {
                    altError("Select a tile to move.");
                    return;
                }
            }
            if (event.getCode() == KeyCode.UP) {
                System.out.println("Moving up");
                currentProject.moveBackgroundTileUp(selectedBackground, selectedTile);
            } else if (event.getCode() == KeyCode.DOWN) {
                currentProject.moveBackgroundTileDown(selectedBackground, selectedTile);
            }
            updateView();
        }
    }

    @FXML
    public void btnAddBackgroundTileOnAction() {
        int selectedIndex = choiceBoxBackgroundTiles.getSelectionModel().getSelectedIndex();
        try {
            new NewTileSceneController(this, selectedIndex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void btnDeleteBackgroundTileOnAction() {
        if (currentProject == null) {
            altError("You can't delete a tile until you've opened or created a project");
            return;
        }
        String selectedTile = listViewBackgroundTiles.getSelectionModel().getSelectedItem();
        if (selectedTile == null || selectedTile.isBlank()) {
            altLog("Select a tile to delete.");
            return;
        }
        int selectedIndex = choiceBoxBackgroundTiles.getSelectionModel().getSelectedIndex();
        if (!currentProject.deleteBackgroundTileFromCharacterData(selectedIndex, selectedTile)) {
            altError(String.format("Unable to remove tile %s from project background %d character data.", selectedTile, selectedIndex));
        }
        updateView();
    }

    // endregion

    @Override
    public void addBackgroundTile(String tileName, int backgroundNumber) {
        System.out.printf("Adding background tile %s to background %d.%n", tileName, backgroundNumber);
        if (getBackgroundTileNames(backgroundNumber).contains(tileName)) {
            altError(String.format("Tile with name %s already exists on background %d.", tileName, backgroundNumber));
            return;
        }
        currentProject.addBackgroundTileToCharacterData(backgroundNumber, tileName);
        updateView();
        choiceBoxBackgroundTiles.getSelectionModel().select(backgroundNumber);
        selectBackgroundTile(tileName);
        listViewBackgroundTiles.requestFocus();
    }

    @Override
    public Collection<String> getBackgroundTileNames(int backgroundNumber) {
        return currentProject.getBackgroundTiles(backgroundNumber).stream()
                .map(Tile::getName)
                .collect(Collectors.toList());
    }

    private void selectBackgroundTile() {
        selectBackgroundTile(null);
    }

    private void selectBackgroundTile(String tileName) {
        try {
            if (tileName != null) listViewBackgroundTiles.getSelectionModel().select(tileName);
            String selected = listViewBackgroundTiles.getSelectionModel().getSelectedItem();
            if (selected == null) {
                listViewBackgroundTiles.getSelectionModel().selectFirst();
            }
            int backgroundIndex = choiceBoxBackgroundTiles.getSelectionModel().getSelectedIndex();
            Tile tile = currentProject.getBackgroundTile(backgroundIndex, selected);
            if (tile == null) {
                return;
            }
            if (MainSceneController.this.editTileSceneController == null) {
                MainSceneController.this.editTileSceneController = new EditTileSceneController(currentProject);
            }
            setCenter(editTileSceneController);
            editTileSceneController.setTile(tile, true);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException e) {
            System.out.println("Unable to load Edit Tile Scene.");
        }
    }

    private void focusBackgroundTilesListView() {
        listViewBackgroundTiles.requestFocus();
        TitledPane pane = accordion.getPanes().stream()
                .filter(p -> p.getText().toLowerCase().contains("tile"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Tile pane missing???"));
        pane.setExpanded(true);
        selectBackgroundTile();
    }

    @Override
    public void renameBackgroundTile(String oldName, String newName, int backgroundNumber) {
        currentProject.renameBackgroundTile(oldName, newName, backgroundNumber);
        updateView();
        selectBackgroundTile(newName);
    }

    // endregion

    // endregion

    // region: 4 MAPS --------------------------------------------------------------------------------------------------

    // region: 4.1 FXML LISTENERS --------------------------------------------------------------------------------------

    @FXML
    public void btnAddMapOnAction() {
        if (currentProject == null) {
            altError("You can't add a map until you've opened or created a project");
            return;
        }
        try {
            new NewMapSceneController(this);
        } catch (IOException ex) {
            Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void btnDeleteMapOnAction() {
        if (currentProject == null) {
            altError("You can't delete a map until you've opened or created a project");
            return;
        }
        String selectedMap = listViewMaps.getSelectionModel().getSelectedItem();
        if (selectedMap == null || selectedMap.isBlank()) {
            altLog("Select an object palette to delete.");
            return;
        }
        currentProject.removeBackgroundMap(selectedMap);
        updateView();
    }

    @FXML
    public void listViewMapsMouseClicked() {
        selectBackgroundMap();
    }

    // endregion

    public void createMap(String mapName) {
        currentProject.addBackgroundMap(mapName);
        updateView();
    }

    private void selectBackgroundMap() {
        try {
            String selected = listViewMaps.getSelectionModel().getSelectedItem();
            if (selected == null) listViewMaps.getSelectionModel().selectFirst();
            selected = listViewMaps.getSelectionModel().getSelectedItem();
            EditMapsSceneController editMapsSceneController = new EditMapsSceneController(currentProject);
            editMapsSceneController.selectMap(selected);
            setCenter(editMapsSceneController);
        } catch (IOException ex) {
            Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // endregion
}
