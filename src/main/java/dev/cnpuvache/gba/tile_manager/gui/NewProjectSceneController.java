/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.Project;
import java.io.IOException;
import java.net.URISyntaxException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Reznov
 */
public class NewProjectSceneController extends BorderPane {
    
    private Stage stage;
    private MainSceneInterface mainScene;

    @FXML
    private Label lblError;
    @FXML
    private TextField txfName;
    @FXML
    private RadioButton rdbtnPalette16;
    @FXML
    private ToggleGroup palette;
    @FXML
    private RadioButton rdbtnPalette256;
    @FXML
    private RadioButton rdbtnOneDimensional;
    @FXML
    private ToggleGroup objmap;
    @FXML
    private RadioButton rdbtnTwoDimensional;
    @FXML
    private CheckBox rdbtnDisplayOBJ;
    @FXML
    private CheckBox rdbtnDisplayBG0;
    @FXML
    private CheckBox rdbtnDisplayBG1;
    @FXML
    private CheckBox rdbtnDisplayBG2;
    @FXML
    private CheckBox rdbtnDisplayBG3;
    @FXML
    private Button btnCreate;
    @FXML
    private Button btnCancel;

    public NewProjectSceneController(MainSceneInterface mainScene) throws IOException {
        this.stage = new Stage();
        this.mainScene = mainScene;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/NewProjectScene.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
        addListeners();
    }  

    private void addListeners() {
        btnCancel.setOnAction((ActionEvent event) -> this.stage.close());
        btnCreate.setOnAction((ActionEvent event) -> {
           String name = this.txfName.getText();
           Project.PaletteType paletteType = null;
           if (rdbtnPalette16.isSelected()) {
               paletteType = Project.PaletteType.PALETTE16;
           } else if (rdbtnPalette256.isSelected()) {
               paletteType = Project.PaletteType.PALETTE256;
           } else {
               throw new IllegalStateException("Palette toggle group needs to be checked.");
           }
           Project.OBJMapping objMapping = null;
           if (rdbtnOneDimensional.isSelected()) {
               objMapping = Project.OBJMapping.ONE_DIMENSIONAL;
           } else if (rdbtnTwoDimensional.isSelected()) {
               objMapping = Project.OBJMapping.TWO_DIMENSIONAL;
           } else {
               throw new IllegalStateException("OBJ Mapping toggle group needs to be checked.");
           }
           boolean displayOBJ = rdbtnDisplayOBJ.isSelected();
           boolean displayBG0 = rdbtnDisplayBG0.isSelected();
           boolean displayBG1 = rdbtnDisplayBG1.isSelected();
           boolean displayBG2 = rdbtnDisplayBG2.isSelected();
           boolean displayBG3 = rdbtnDisplayBG3.isSelected();
           try {
                Project project = new Project(name, objMapping, paletteType, displayOBJ, displayBG0, displayBG1, displayBG2, displayBG3);
                this.mainScene.setProject(project);
                this.stage.close();
           } catch (IllegalArgumentException ex) {
               lblError.setText(ex.getMessage());
           }
        });
    }
    
}
