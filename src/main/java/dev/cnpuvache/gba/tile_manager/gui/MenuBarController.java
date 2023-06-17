    package dev.cnpuvache.gba.tile_manager.gui;

    import dev.cnpuvache.gba.tile_manager.domain.Project;
    import dev.cnpuvache.gba.tile_manager.gui.dialog.NewProjectDialog;
    import dev.cnpuvache.gba.tile_manager.persistence.FileManager;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.stage.DirectoryChooser;
    import javafx.stage.Stage;

    import java.io.File;
    import java.util.Optional;
    import java.util.function.Consumer;

    public class MenuBarController {

        private Consumer<Project> newProjectConsumer;

        public void setNewProjectConsumer(Consumer<Project> newProjectConsumer) {
            this.newProjectConsumer = newProjectConsumer;
        }

        @FXML
        void newProjectMenuItemOnAction(ActionEvent event) {
            NewProjectDialog newProjectDialog = new NewProjectDialog();
            newProjectDialog.showAndWait()
                    .flatMap(FileManager.getInstance()::createProject)
                    .ifPresent(p -> {
                if (newProjectConsumer != null) {
                    newProjectConsumer.accept(p);
                }
            });
        }

        @FXML
        void openProjectMenuItemOnAction(ActionEvent event) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Open Project");
            File file = directoryChooser.showDialog(new Stage());
            FileManager.getInstance().openProject(file).ifPresent(p -> {
                if (newProjectConsumer != null) {
                    newProjectConsumer.accept(p);
                }
            });
        }

        @FXML
        void saveProjectMenuItemOnAction(ActionEvent event) {
            FileManager.getInstance().saveProject();
        }

        @FXML
        void buildMenuItemOnAction(ActionEvent event) {
            FileManager.getInstance().buildProject();
        }

    }
