package dev.cnpuvache.gba.tile_manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import dev.cnpuvache.gba.tile_manager.domain.Project;
import dev.cnpuvache.gba.tile_manager.gui.MainSceneController;
import dev.cnpuvache.gba.tile_manager.util.ProjectJsonConverter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Reznov
 */
public class StartUp extends Application {
    
    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/MainScene.fxml"));
        VBox root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("GBA Tile Manager");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
