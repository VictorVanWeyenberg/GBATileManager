package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TileTests {

    private static final String NAME = "Storm";

    private Tile tile;

    @BeforeEach
    public void setup() {
        int[] tileData = new int[64];
        for (int i = 0; i < 8; i++) {
            tileData[i] = 1;
            tileData[i * 8 + i] = 1;
            tileData[i * 8] = 1;
        }
        tile = new Tile(NAME, tileData);
    }

    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Tile.class, new Tile.Serializer());
        module.addDeserializer(Tile.class, new Tile.Deserializer());
        mapper.registerModule(module);
        String json = mapper.writeValueAsString(tile);
        System.out.println(json);
        Tile parsedTile = mapper.readValue(json, Tile.class);
        assertEquals(tile, parsedTile);
    }

}
