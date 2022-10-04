package dev.cnpuvache.gba.tile_manager;

import dev.cnpuvache.gba.tile_manager.gui.MainSceneController;
import java.io.IOException;
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
        new MainSceneController(primaryStage);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
