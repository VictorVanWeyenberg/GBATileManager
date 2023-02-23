package dev.cnpuvache.gba.tile_manager.persistence;

import dev.cnpuvache.gba.tile_manager.domain.Project;
import dev.cnpuvache.gba.tile_manager.util.ProjectJsonConverter;

import java.io.*;
import java.util.Optional;

public class FileManager {

    private static final FileManager INSTANCE = new FileManager();

    private File managingFile;

    public static FileManager getInstance() {
        return INSTANCE;
    }

    public Optional<Project> openProject(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            Optional<Project> optionalProject = Optional.of(ProjectJsonConverter.fromJson(fis.readAllBytes()));
            managingFile = file;
            return optionalProject;
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public boolean saveProject(Project project) {
        if (managingFile == null || project == null) {
            return false;
        }
        try(FileOutputStream fos = new FileOutputStream(managingFile)) {
            fos.write(ProjectJsonConverter.toJson(project));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
