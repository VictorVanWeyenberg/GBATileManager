package dev.cnpuvache.gba.tile_manager.gui;

import dev.cnpuvache.gba.tile_manager.domain.Project;
import dev.cnpuvache.gba.tile_manager.persistence.CachingManager;
import dev.cnpuvache.gba.tile_manager.persistence.FileManager;
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
import java.util.Optional;

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
    private ComponentsTabController componentsController;

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
    private Tab componentsTab;

    @FXML
    void initialize() {
        File file = CachingManager.getInstance().getLatestOpenedProject();
        FileManager.getInstance().openProject(file).ifPresent(this::setProject);
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
        componentsTab.selectedProperty().addListener((o, t0, t1) -> {
            if (t1) {
                componentsController.selected();
            }
        });
        menuBarController.setNewProjectConsumer(this::setProject);
    }

    private void setProject(Project project) {
        if (project == null) {
            palettesTab.setDisable(true);
            tilesTab.setDisable(true);
            screensTab.setDisable(true);
            objectsTab.setDisable(true);
            return;
        }
        palettesTab.setDisable(false);
        tilesTab.setDisable(false);
        screensTab.setDisable(false);
        objectsTab.setDisable(false);
        componentsController.setProject(project);
        palettesController.setProject(project);
        tilesController.setProject(project);
        screensController.setProject(project);
    }

}
