/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import compiler.CCompiler;
import domain.IOManager;
import domain.Project;
import compiler.ProjectCAdapter;
import exceptions.FileTypeException;
import exceptions.ProjectStructureException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
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

    public MainSceneController(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        addListeners();
    }

    private void addListeners() {
        menuItemNewProject.setOnAction((ActionEvent event) -> {
            try {
                new NewProjectSceneController(this);
            } catch (IOException ex) {
                Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        listViewObjectPalettes.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                new EditPaletteSceneController(subScene, currentProject.getObjectPalette(newValue));
            } catch (IOException ex) {
                Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        listViewBackgroundPalettes.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            try {
                new EditPaletteSceneController(subScene, currentProject.getBackgroundPalette(newValue));
            } catch (IOException ex) {
                Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        menuItemSaveAs.setOnAction((ActionEvent event) -> {
            FileChooser chooser = new FileChooser();
            chooser.setInitialFileName(currentProject.getName());
            File dest = chooser.showSaveDialog(new Stage());
            try {
                IOManager.saveAs(currentProject, dest);
            } catch (IOException ex) {
                Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        menuItemOpen.setOnAction((ActionEvent event) -> {
            FileChooser chooser = new FileChooser();
            chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("GBA Project", "*.gbaproj"));
            File src = chooser.showOpenDialog(new Stage());
            try {
                Project project = IOManager.open(src);
                currentProject = project;
                updateView();
            } catch (FileTypeException ex) {
                Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ProjectStructureException ex) {
                Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(MainSceneController.class.getName()).log(Level.SEVERE, null, ex);
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
