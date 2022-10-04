package dev.cnpuvache.gba.tile_manager.compiler;

import dev.cnpuvache.gba.tile_manager.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class CCompilerTests {

    private Tile tile1, tile2;
    private CharacterData characterData;
    private ScreenEntry screenEntry;
    private ScreenData screenData;

    private Background background;

    private Palette16 palette16;
    private RGB15 color1, color2, color3;

    private Project project;

    @BeforeEach
    public void setup() {
        color1 = new RGB15(1, 2, 3);
        color2 = new RGB15(4, 5, 6);
        color3 = new RGB15(7, 8, 9);

        palette16 = new Palette16("TEST");
        palette16.setColor(1, 2, color1);
        palette16.setColor(3, 4, color2);
        palette16.setColor(5, 6, color3);

        tile1 = new Tile(false);
        for (int i = 0; i < 8; i++) {
            tile1.setTileData(i, 0, 1);
            tile1.setTileData(0, i, 1);
        }

        tile2 = new Tile(false);
        for (int i = 0; i < 8; i++) {
            tile2.setTileData(i, i, 1);
        }

        characterData = new CharacterData(false);
        characterData.addTile(tile1);
        characterData.addTile(tile2);

        screenEntry = new ScreenEntry(49, false, true, 12);
        screenData = new ScreenData(0, 0);
        screenData.setEntry(4, 7, screenEntry);

        background = new Background.Builder(3, 0, true, 22, 0)
                .setCharacterData(characterData)
                .setScreenData(screenData)
                .build();

        List<Background> backgrounds = new ArrayList<>();
        backgrounds.add(background);
        backgrounds.add(background);
        backgrounds.add(null);
        backgrounds.add(null);

        Map<String, Palette> objectPalettes = new TreeMap<>();
        objectPalettes.put("Default Object Palette", palette16);
        objectPalettes.put("Other Object Palette", palette16);

        Map<String, Palette> backgroundPalettes = new TreeMap<>();
        backgroundPalettes.put("Default Background Palette", palette16);
        backgroundPalettes.put("Other Background Palette", palette16);

        project = new Project(
                "TEST", Project.OBJMapping.ONE_DIMENSIONAL, Project.PaletteType.PALETTE16,
                backgrounds, objectPalettes, backgroundPalettes,
                true, true, true, false, false);
    }

    @Test
    public void test() {
        System.out.println(new CCompiler(project).toC());
    }

}
