package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.Project;
import dev.cnpuvache.gba.tile_manager.util.ProjectJsonConverter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainSceneController {

    @FXML
    private MenuBarController menuBarController;

    @FXML
    private PalettesTabController palettesController;

    @FXML
    private TilesTabController tilesController;

    @FXML
    private ScreensTabController screensController;

    @FXML
    private ObjectsTabController objectsController;

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab palettesTab;
    @FXML
    private Tab tilesTab;
    @FXML
    private Tab screensTab;
    @FXML
    private Tab objectsTab;

    @FXML
    void initialize() {
        File file = new File("/home/cnpuvache/Desktop/GrooveBoy.gbaproj");
        try (FileInputStream fis = new FileInputStream(file)) {
            Project project = ProjectJsonConverter.fromJson(fis.readAllBytes());
            palettesController.setProject(project);
            tilesController.setProject(project);
            screensController.setProject(project);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tilesTab.selectedProperty().addListener((o, t0, t1) -> {
            if (t1) {
                tilesController.selected();
            }
        });
        screensTab.selectedProperty().addListener((o, t0, t1) -> {
            if (t1) {
                screensController.selected();
            }
        });
    }

}
