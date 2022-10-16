/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.Palette16;
import dev.cnpuvache.gba.tile_manager.domain.Project;

import java.io.*;
import java.nio.file.Files;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import dev.cnpuvache.gba.tile_manager.domain.Tile;
import dev.cnpuvache.gba.tile_manager.io.ProjectJsonConverter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Reznov
 */
public class MainSceneController extends BorderPane implements MainSceneInterface {
    
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
    
    private Project currentProject;
    @FXML
    private ListView<String> listViewObjectPalettes;
    @FXML
    private ListView<String> listViewBackgroundPalettes;
    @FXML
    private ListView<String> listViewObjectTiles;
    @FXML
    private ListView<String> listViewBackgroundTiles;
    @FXML
    private Button btnAddPalette;
    @FXML
    private Button btnDeletePalette;
    @FXML
    private Button btnMakeDefaultPalette;
    @FXML
    private Pane subScene;
    @FXML
    private Label altLabel;

    private EditPaletteSceneController editPaletteSceneController;
    private EditTileSceneController editTileSceneController;
    private File currentProjectLocation;

    public MainSceneController(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainScene.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

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
        this.currentProjectLocation = src;
        try {
            byte[] data = Files.readAllBytes(src.toPath());
            currentProject = ProjectJsonConverter.fromJson(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            updateView();
        }
    }

    @FXML
    public void menuItemSaveOnAction() {
        if (this.currentProject == null || this.currentProjectLocation == null) {
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(this.currentProjectLocation)) {
            byte[] data = ProjectJsonConverter.toJson(currentProject);
            int len = 1024;
            for (int off = 0; off < data.length; off += len) {
                if (off + len > data.length) {
                    len = data.length - off;
                }
                fos.write(data, off, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void menuItemSaveAsOnAction() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialFileName(currentProject.getName());
        File dest = chooser.showSaveDialog(new Stage());
        this.currentProjectLocation = dest;
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            byte[] data = ProjectJsonConverter.toJson(currentProject);
            int len = 1024;
            for (int off = 0; off < data.length; off += len) {
                if (off + len > data.length) {
                    len = data.length - off;
                }
                fos.write(data, off, len);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void menuItemCompileToCOnAction() {
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

    @FXML
    public void listViewBackgroundPalettesMouseClicked(MouseEvent event) {
        try {
            String selected = listViewBackgroundPalettes.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            if (MainSceneController.this.editPaletteSceneController == null) {
                MainSceneController.this.editPaletteSceneController = new EditPaletteSceneController();
            }
            setCenter(editPaletteSceneController);
            MainSceneController.this.editPaletteSceneController.setPalette(currentProject.getBackgroundPalette(selected));
        } catch (IOException ex) {
            Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void listViewObjectPalettesMouseClicked(MouseEvent event) {
        try {
            String selected = listViewObjectPalettes.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            if (MainSceneController.this.editPaletteSceneController == null) {
                MainSceneController.this.editPaletteSceneController = new EditPaletteSceneController();
            }
            setCenter(editPaletteSceneController);
            MainSceneController.this.editPaletteSceneController.setPalette(currentProject.getObjectPalette(selected));
        } catch (IOException ex) {
            Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void listViewBackgroundTilesMouseClicked() {
        try {
            String selected = listViewBackgroundTiles.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }
            Tile tile = currentProject.getBackgroundTile(1, selected);
            System.out.println(tile.toString());

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

    @FXML
    public void listViewObjectTilesMouseClicked() {

    }

    private void updateView() {
        if (currentProject != null) {
            listViewObjectPalettes.setItems(FXCollections.observableArrayList(this.currentProject.getObjectPalettes().keySet()));
            listViewBackgroundPalettes.setItems(FXCollections.observableArrayList(this.currentProject.getBackgroundPalettes().keySet()));
            listViewObjectTiles.setItems(FXCollections.observableArrayList(this.currentProject.getObjectTiles().stream().map(Tile::getName).collect(Collectors.toList())));
            listViewBackgroundTiles.setItems(FXCollections.observableArrayList(this.currentProject.getBackgroundTiles(1).stream().map(Tile::getName).collect(Collectors.toList())));
        }
        menuItemSave.setDisable(currentProject == null || currentProjectLocation == null);
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

    @Override
    public void addBackgroundPalette(String paletteName, boolean makeDefault) {
        currentProject.addBackgroundPalette(paletteName, new Palette16(paletteName), makeDefault);
        updateView();
        altLog("Added background palette " + paletteName);
    }

    @Override
    public void addObjectPalette(String paletteName, boolean makeDefault) {
        currentProject.addObjectPalette(paletteName, new Palette16(paletteName), makeDefault);
        updateView();
        altLog("Added object palette " + paletteName);
    }

    @Override
    public Collection<String> getBackgroundPaletteNames() {
        return listViewBackgroundPalettes.getItems();
    }

    @Override
    public Collection<String> getObjectPaletteNames() {
        return listViewObjectPalettes.getItems();
    }
}
