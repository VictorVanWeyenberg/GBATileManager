package dev.cnpuvache.gba.tile_manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.cnpuvache.gba.tile_manager.domain.Project;
import dev.cnpuvache.gba.tile_manager.domain.Tile;
import dev.cnpuvache.gba.tile_manager.persistence.CachingManager;
import dev.cnpuvache.gba.tile_manager.persistence.FileManager;
import dev.cnpuvache.gba.tile_manager.util.CToTileConverter;
import dev.cnpuvache.gba.tile_manager.util.ProjectJsonConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Playground {

    public static void main(String[] args) throws JsonProcessingException {
        System.exit(0);
        List<Tile> tiles = CToTileConverter.fromC();
        for (Tile tile : tiles) {
            System.out.println(tile);
            System.out.println();
        }
        System.exit(0);
        File latestOpenedProject = CachingManager.getInstance().getLatestOpenedProject();
        FileManager fileManager = FileManager.getInstance();
        Optional<Project> project = fileManager.openProject(latestOpenedProject);
        project.ifPresent(p -> {
            for (Tile tile : tiles) {
                p.setTile(1, tile);
            }
        });
        fileManager.saveProject();
    }

}
