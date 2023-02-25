package dev.cnpuvache.gba.tile_manager.persistence;

import dev.cnpuvache.gba.tile_manager.domain.Project;
import dev.cnpuvache.gba.tile_manager.util.ProjectJsonConverter;

import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;

public class FileManager {

    private static final String STRUCTURE_JSON_FILE_NAME = "structure.json";
    private static final FileManager INSTANCE = new FileManager();

    private File managingFile;
    private Project managingProject;

    public static FileManager getInstance() {
        return INSTANCE;
    }

    public Optional<Project> createProject(File file) {
        if (file.exists() || !file.mkdirs()) {
            return Optional.empty();
        }
        managingFile = file;
        managingProject = new Project(Paths.get(file.toURI()).getFileName().toString(), new int[] { 0, 0, 0, 0 });
        if (!saveProject()) {
            file.delete();
            return Optional.empty();
        }
        CachingManager.getInstance().setLatestOpenedProject(managingFile);
        return Optional.of(managingProject);
    }

    public Optional<Project> openProject(File file) {
        try (FileInputStream fis = new FileInputStream(new File(file, STRUCTURE_JSON_FILE_NAME))) {
            Optional<Project> optionalProject = Optional.of(ProjectJsonConverter.fromJson(fis.readAllBytes()));
            managingFile = file;
            CachingManager.getInstance().setLatestOpenedProject(managingFile);
            optionalProject.ifPresent(p -> {
                FileManager.this.managingProject = p;
            });
            return optionalProject;
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public boolean saveProject() {
        if (managingFile == null || managingProject == null) {
            System.out.println(managingFile);
            System.out.println(managingProject);
            return false;
        }
        try(FileOutputStream fos = new FileOutputStream(new File(managingFile, STRUCTURE_JSON_FILE_NAME))) {
            fos.write(ProjectJsonConverter.toJson(managingProject));
            CachingManager.getInstance().setLatestOpenedProject(managingFile);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
