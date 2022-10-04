/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.compiler.CCompiler;
import dev.cnpuvache.gba.tile_manager.domain.Project;

import java.io.*;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import dev.cnpuvache.gba.tile_manager.io.ProjectJsonConverter;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
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
    private Button btnAddPalette;
    @FXML
    private Button btnDeletePalette;
    @FXML
    private Button btnMakeDefaultPalette;
    @FXML
    private SubScene subScene;

    private Pane subScenePane = new Pane();

    private EditPaletteSceneController editPaletteSceneController;

    public MainSceneController(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainScene.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        addListeners();

        subScene.setRoot(subScenePane);
    }

    private void addListeners() {
        menuItemNewProject.setOnAction((ActionEvent event) -> {
            try {
                new NewProjectSceneController(this);
            } catch (IOException ex) {
                Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        listViewObjectPalettes.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    String selected = listViewObjectPalettes.getSelectionModel().getSelectedItem();
                    if (selected == null) return;
                    if (MainSceneController.this.editPaletteSceneController == null) {
                        MainSceneController.this.editPaletteSceneController = new EditPaletteSceneController();
                    }
                    subScenePane.getChildren().clear();
                    subScenePane.getChildren().add(editPaletteSceneController);
                    MainSceneController.this.editPaletteSceneController.setPalette(currentProject.getObjectPalette(selected));
                } catch (IOException ex) {
                    Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        listViewBackgroundPalettes.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    String selected = listViewBackgroundPalettes.getSelectionModel().getSelectedItem();
                    if (selected == null) return;
                    if (MainSceneController.this.editPaletteSceneController == null) {
                        MainSceneController.this.editPaletteSceneController = new EditPaletteSceneController();
                    }
                    subScenePane.getChildren().clear();
                    subScenePane.getChildren().add(editPaletteSceneController);
                    MainSceneController.this.editPaletteSceneController.setPalette(currentProject.getBackgroundPalette(selected));
                } catch (IOException ex) {
                    Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        menuItemSaveAs.setOnAction((ActionEvent event) -> {
            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName(currentProject.getName());
            File dest = chooser.showSaveDialog(new Stage());
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
        });
        menuItemOpen.setOnAction((ActionEvent event) -> {
            FileChooser chooser = new FileChooser();
            chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("GBA Project", "*.gbaproj"));
            File src = chooser.showOpenDialog(new Stage());
            try {
                byte[] data = Files.readAllBytes(src.toPath());
                currentProject = ProjectJsonConverter.fromJson(data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                updateView();
            }
        });
        menuItemCompileToC.setOnAction((ActionEvent event) -> {
            CCompiler compiler = new CCompiler(currentProject);
            System.out.println(compiler.toC());
        });
    }

    private void updateView() {
        if (currentProject != null) {
            listViewObjectPalettes.setItems(FXCollections.observableArrayList(this.currentProject.getObjectPalettes().keySet()));
            listViewBackgroundPalettes.setItems(FXCollections.observableArrayList(this.currentProject.getBackgroundPalettes().keySet()));
        }
        // menuItemSave.setDisable(currentProject == null);
        menuItemSaveAs.setDisable(currentProject == null);
        menuItemCompileToC.setDisable(currentProject == null);
    }

    @Override
    public void setProject(Project project) {
        this.currentProject = project;
        updateView();
    }
}
