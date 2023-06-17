package dev.cnpuvache.gba.tile_manager.binary;

import dev.cnpuvache.gba.tile_manager.domain.CharacterData;
import dev.cnpuvache.gba.tile_manager.domain.Project;
import dev.cnpuvache.gba.tile_manager.domain.Screen;

import java.util.HashMap;
import java.util.Map;

public class ProjectToBinary {

    private static final String BACKGROUND_BINARY_FILE_NAME = "background_palette.bin";
    private static final String OBJECT_BINARY_FILE_NAME = "object_palette.bin";
    private static final String CHARACTER_DATA_FILE_NAME = "BG%02d_character_data.bin";
    private static final String SCREEN_DATA_FILE_NAME = "%s_BG%02d_screen_data.bin";

    public static Map<String, byte[]> convert(Project project) {
        Map<String, byte[]> binaries = new HashMap<>();
        binaries.put(BACKGROUND_BINARY_FILE_NAME, PaletteToBinary.convert(project.getBackgroundPalette()));
        binaries.put(OBJECT_BINARY_FILE_NAME, PaletteToBinary.convert(project.getObjectPalette()));
        for (int backgroundNumber = 0; backgroundNumber < 5; backgroundNumber++) {
            String fileName = String.format(CHARACTER_DATA_FILE_NAME, backgroundNumber);
            CharacterData data = project.getCharacterData(backgroundNumber);
            if (data.getTiles().size() == 0) {
                continue;
            }
            binaries.put(fileName, CharacterDataToBinary.convert(project.getCharacterData(backgroundNumber)));
        }
        for (Screen screen : project.getScreens()) {
            for (int backgroundNumber = 0; backgroundNumber < 4; backgroundNumber++) {
                String name = screen.getName();
                name = String.format(SCREEN_DATA_FILE_NAME, name.toLowerCase(), backgroundNumber);
                binaries.put(name, ScreenDataToBinary.convert(screen, backgroundNumber));
            }
        }
        return binaries;
    }

}
