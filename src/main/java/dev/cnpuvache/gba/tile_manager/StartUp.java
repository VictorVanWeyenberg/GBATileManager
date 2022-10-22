package dev.cnpuvache.gba.tile_manager;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import dev.cnpuvache.gba.tile_manager.domain.Project;
import dev.cnpuvache.gba.tile_manager.gui.MainSceneController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import dev.cnpuvache.gba.tile_manager.io.ProjectJsonConverter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

/**
 *
 * @author Reznov
 */
public class StartUp extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        MainSceneController mainSceneController = new MainSceneController(primaryStage);
        File latestOpenedProjectFile = CachingManager.getInstance().getLatestOpenedProject();
        if (latestOpenedProjectFile != null && latestOpenedProjectFile.exists() && latestOpenedProjectFile.canRead()) {
            byte[] contents = Files.readAllBytes(latestOpenedProjectFile.toPath());
            Project project = ProjectJsonConverter.fromJson(contents);
            project.setLocation(latestOpenedProjectFile);
            mainSceneController.setProject(project);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
