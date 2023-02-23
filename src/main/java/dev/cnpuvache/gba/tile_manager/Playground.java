package dev.cnpuvache.gba.tile_manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.cnpuvache.gba.tile_manager.domain.Project;
import dev.cnpuvache.gba.tile_manager.util.ProjectJsonConverter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Playground {

    public static void main(String[] args) throws JsonProcessingException {
        Project project = new Project("GrooveBoy", new int[] { 0, 0, 0, 0 });
        byte[] data = ProjectJsonConverter.toJson(project);
        try (FileOutputStream fos = new FileOutputStream("/home/cnpuvache/Desktop/GrooveBoy.gbaproj")) {
            fos.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
