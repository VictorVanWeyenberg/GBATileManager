package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BackgroundTests {

    private Tile tile1, tile2;
    private CharacterData characterData;
    private ScreenEntry screenEntry;
    private ScreenData screenData;

    private Background background;

    @BeforeEach
    public void setup() {
        tile1 = new Tile(false, "Tile001");
        for (int i = 0; i < 8; i++) {
            tile1.setTileData(i, 0, 1);
            tile1.setTileData(0, i, 1);
        }

        tile2 = new Tile(false, "Tile002");
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
    }

    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Tile.class, new Tile.Serializer());
        module.addDeserializer(Tile.class, new Tile.Deserializer());
        module.addSerializer(CharacterData.class, new CharacterData.Serializer());
        module.addDeserializer(CharacterData.class, new CharacterData.Deserializer());
        module.addSerializer(ScreenEntry.class, new ScreenEntry.Serializer());
        module.addDeserializer(ScreenEntry.class, new ScreenEntry.Deserializer());
        module.addSerializer(ScreenData.class, new ScreenData.Serializer());
        module.addDeserializer(ScreenData.class, new ScreenData.Deserializer());
        module.addSerializer(Background.class, new Background.Serializer());
        module.addDeserializer(Background.class, new Background.Deserializer());
        mapper.registerModule(module);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(background);
        System.out.println(json);
        Background parsedBackground = mapper.readValue(json, Background.class);
        assertEquals(background, parsedBackground);
    }

}
