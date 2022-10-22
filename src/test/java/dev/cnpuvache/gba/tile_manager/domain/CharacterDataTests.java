package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CharacterDataTests {

    private Tile tile1;
    private Tile tile2;

    private CharacterData characterData;

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
    }

    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(CharacterData.class, new CharacterData.Serializer());
        module.addDeserializer(CharacterData.class, new CharacterData.Deserializer());
        module.addSerializer(Tile.class, new Tile.Serializer());
        module.addDeserializer(Tile.class, new Tile.Deserializer());
        mapper.registerModule(module);

        String json = mapper.writeValueAsString(characterData);

        CharacterData parsedCharacterData = mapper.readValue(json, CharacterData.class);

        assertEquals(characterData, parsedCharacterData);
    }

}
