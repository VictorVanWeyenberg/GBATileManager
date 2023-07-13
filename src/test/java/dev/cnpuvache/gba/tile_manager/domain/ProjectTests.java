package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectTests {

    private Project project;

    @BeforeEach
    public void setup() {
        ScreenEntry screenEntry1 = new ScreenEntry(49, false, false, 12);
        ScreenEntry screenEntry2 = new ScreenEntry(86, false, true, 0);
        ScreenEntry screenEntry3 = new ScreenEntry(61, true, false, 1);
        ScreenEntry screenEntry4 = new ScreenEntry(24, true, true, 2);
        Screen screen = new Screen("screen", new int[] {3, 2, 1, 0});
        screen.setEntry(0, 4,7, screenEntry1);
        screen.setEntry(1, 4,8, screenEntry2);
        screen.setEntry(2, 9,7, screenEntry3);
        screen.setEntry(3, 4,15, screenEntry4);

        int[] tile1Data = new int[64];
        for (int i = 0; i < 8; i++) {
            tile1Data[i] = 1;
        }
        Tile tile1 = new Tile("tile1", tile1Data);

        int[] tile2Data = new int[64];
        for (int i = 0; i < 8; i++) {
            tile2Data[i*7+i] = 1;
        }
        Tile tile2 = new Tile("tile2", tile2Data);

        int[] tile3Data = new int[64];
        for (int i = 0; i < 8; i++) {
            tile1Data[i*7] = 1;
        }
        Tile tile3 = new Tile("tile3", tile2Data);

        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile3);
        tiles.add(tile1);
        tiles.add(tile2);
        CharacterData characterData = new CharacterData(tiles);

        RGB15 color1 = new RGB15(1, 2, 3);
        RGB15 color2 = new RGB15(4, 5, 6);
        RGB15 color3 = new RGB15(7, 8, 9);

        Palette palette = new Palette();
        palette.setColor(1, 2, color1);
        palette.setColor(3, 4, color2);
        palette.setColor(5, 6, color3);

        ObjectAttributes objectAttributes1 = new ObjectAttributes(10, 20, 300, 4, 2);
        ObjectAttributes objectAttributes2 = new ObjectAttributes(20, 40, 600, 8, 3);

        List<Screen> screens = new ArrayList<>();
        screens.add(screen);

        List<CharacterData> characterBlocks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            characterBlocks.add(characterData);
        }

        List<Palette> palettes = new ArrayList<>();
        palettes.add(palette);
        palettes.add(palette);

        TreeMap<String, ObjectAttributes> objects = new TreeMap<>();
        objects.put("object1", objectAttributes1);
        objects.put("object2", objectAttributes2);

        TreeMap<String, List<Component>> components = new TreeMap<>();

        project = new Project("TestProject", new int[] {2,1,3,0}, screens, characterBlocks, palettes, objects, components);
    }

    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ScreenEntry.class, new ScreenEntry.Serializer());
        module.addDeserializer(ScreenEntry.class, new ScreenEntry.Deserializer());
        module.addSerializer(ScreenData.class, new ScreenData.Serializer());
        module.addDeserializer(ScreenData.class, new ScreenData.Deserializer());
        module.addSerializer(Screen.class, new Screen.Serializer());
        module.addDeserializer(Screen.class, new Screen.Deserializer());
        module.addSerializer(RGB15.class, new RGB15.Serializer());
        module.addDeserializer(RGB15.class, new RGB15.Deserializer());
        module.addSerializer(Palette.class, new Palette.Serializer());
        module.addDeserializer(Palette.class, new Palette.Deserializer());
        module.addSerializer(Tile.class, new Tile.Serializer());
        module.addDeserializer(Tile.class, new Tile.Deserializer());
        module.addSerializer(CharacterData.class, new CharacterData.Serializer());
        module.addDeserializer(CharacterData.class, new CharacterData.Deserializer());
        module.addSerializer(ObjectAttributes.class, new ObjectAttributes.Serializer());
        module.addDeserializer(ObjectAttributes.class, new ObjectAttributes.Deserializer());
        module.addSerializer(Component.class, new Component.Serializer());
        module.addDeserializer(Component.class, new Component.Deserializer());
        module.addSerializer(Project.class, new Project.Serializer());
        module.addDeserializer(Project.class, new Project.Deserializer());
        mapper.registerModule(module);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(project);
        System.out.println(json);

        Project parsedProject = mapper.readValue(json, Project.class);
        assertEquals(project, parsedProject);
    }

}
