package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TileTests {

    private Tile tile;

    @BeforeEach
    public void setup() {
        tile = new Tile(false, "Tile001");
        for (int i = 0; i < 8; i++) {
            tile.setTileData(i, 0, 1);
            tile.setTileData(i, i, 1);
            tile.setTileData(0, i, 1);
        }
    }

    @Test
    public void tileFromCToCDoesNotChange() {
        String c = "{ 0x0000, 0x0000, 0x3300, 0x0333, 0x0330, 0x0330, 0x0330, 0x0330, 0x3330, 0x0033, 0x0330, 0x0003, 0x0330, 0x0330, 0x0000, 0x0000 }";
        Tile tile = Tile.fromC(c, false);
        String compiledC = tile.toCCode();
        assertEquals(c, compiledC);
    }

    @Test
    public void tileToCFromCDoesNotChange() {
        String compiledC = tile.toCCode();
        Tile parsedTile = Tile.fromC(compiledC, false);
        assertEquals(tile, parsedTile);
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
