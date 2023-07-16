package dev.cnpuvache.gba.tile_manager.persistence;

import dev.cnpuvache.gba.tile_manager.binary.ProjectToBinary;
import dev.cnpuvache.gba.tile_manager.domain.Project;
import dev.cnpuvache.gba.tile_manager.util.ProjectJsonConverter;

import java.io.*;
import java.nio.file.Paths;
import java.util.Map;
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
            e.printStackTrace();
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

    public void buildProject() {
        if (managingProject == null || managingFile == null) {
            System.out.println("Cannot build null project.");
            return;
        }
        File binariesDirectory = new File(managingFile, "bin");
        if (!binariesDirectory.exists() && !binariesDirectory.mkdirs()) {
            System.out.println("Cannot create bin directory.");
            return;
        }
        if (binariesDirectory.exists() && binariesDirectory.listFiles().length > 0) {
            for (File contentFile : binariesDirectory.listFiles()) {
                contentFile.delete();
            }
        }
        for (Map.Entry<String, byte[]> binaryEntry : ProjectToBinary.convert(managingProject).entrySet()) {
            File outputFile = new File(binariesDirectory, binaryEntry.getKey());
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(binaryEntry.getValue());
                fos.flush();
                System.out.println("Written " + outputFile);
            } catch (IOException e) {
                System.out.println("Unable to write " + binaryEntry.getKey());
            }
        }
    }
}
