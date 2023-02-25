package dev.cnpuvache.gba.tile_manager.gui.dialog;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class NewProjectDialog extends Dialog<File> {

    private final TextField name;
    private final Button chooseFileButton;

    private File projectDirectory;

    public NewProjectDialog() {
        setTitle("New Project");
        setHeaderText("Enter the location and name of the new project.");

        getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(5, 5, 5, 5));

        name = new TextField();
        name.setPromptText("Project name");
        VBox locationVBox = new VBox();
        chooseFileButton = new Button();
        chooseFileButton.setText("Browse...");
        Label locationLabel = new Label();
        locationVBox.getChildren().addAll(chooseFileButton, locationLabel);

        grid.add(new Label("Name"), 0, 0);
        grid.add(new Label("Location"), 0, 1);
        grid.add(name, 1, 0);
        grid.add(locationVBox, 1, 1);

        getDialogPane().setContent(grid);

        Platform.runLater(name::requestFocus);

        chooseFileButton.setOnAction(e -> {
            Stage stage = new Stage();
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose project location");
            NewProjectDialog.this.projectDirectory = directoryChooser.showDialog(stage);
            locationLabel.setText(projectDirectory.getAbsolutePath());
        });

        setResultConverter(button -> {
            if (projectDirectory == null || name.getText() == null || name.getText().trim().isBlank()) {
                return null;
            }
            if (button == ButtonType.FINISH) {
                return new File(projectDirectory, name.getText());
            }
            return null;
        });
    }

}
