/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.domain;

import dev.cnpuvache.gba.tile_manager.exceptions.FileTypeException;
import dev.cnpuvache.gba.tile_manager.exceptions.ProjectStructureException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.util.Arrays.asList;
import java.util.List;

/**
 *
 * @author Reznov
 */
public class IOManager {
    
    private final static List<String> projectDirs = asList("src", "build");
    private static ObjectOutputStream output;
    private static ObjectInputStream input;
    
    public static void saveAs(Project project, File file) throws IOException {
        file.mkdir();
        String filePath = file.getAbsolutePath();
        for (String dir : projectDirs) {
            new File(filePath + File.separator + dir).mkdir();
        }
        output = new ObjectOutputStream(new FileOutputStream(filePath + File.separator + project.getName() + ".gbaproj"));
        output.writeObject(project);
        output.flush();
        output.close();
    }
    
    public static Project open(File file) throws FileTypeException, ProjectStructureException, FileNotFoundException, IOException, ClassNotFoundException {
        if (!file.getAbsolutePath().endsWith(".gbaproj")) {
            throw new FileTypeException("Project file has a \".gbaproj\" extension.");
        }
        File parent = file.getParentFile();
        for (String dir : projectDirs) {
            if (!new File(parent, dir).exists()) {
                throw new ProjectStructureException(String.format("Folder %s is missing in project folder.", dir));
            }
        }
        input = new ObjectInputStream(new FileInputStream(file));
        return (Project) input.readObject();
    }
    
}
