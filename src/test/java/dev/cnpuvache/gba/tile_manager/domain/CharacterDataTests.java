package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class CharacterDataTests {

    private Tile tile1;
    private Tile tile2;
    private Tile tile3;

    private CharacterData characterData;

    @BeforeEach
    public void setup() {
        int[] tile1Data = new int[64];
        for (int i = 0; i < 8; i++) {
            tile1Data[i] = 1;
        }
        tile1 = new Tile("tile1", tile1Data);

        int[] tile2Data = new int[64];
        for (int i = 0; i < 8; i++) {
            tile2Data[i*7+i] = 1;
        }
        tile2 = new Tile("tile2", tile2Data);

        int[] tile3Data = new int[64];
        for (int i = 0; i < 8; i++) {
            tile1Data[i*7] = 1;
        }
        tile3 = new Tile("tile3", tile3Data);

        List<Tile> tiles = new ArrayList<>();
        tiles.add(tile3);
        tiles.add(tile1);
        tiles.add(tile2);
        characterData = new CharacterData(tiles);
    }

    @Test
    @DisplayName("Adding a tile to characterData increases the size of the tiles map.")
    public void addingATileToCharacterDataIncreasesTheSizeOfTheTilesMap() {
        assertEquals(3, characterData.tiles.size());
        characterData.setTile(mock(Tile.class));
        assertEquals(4, characterData.tiles.size());
    }

    @Test
    @DisplayName("Adding a tile to characterData add that actual tile with that name.")
    public void addingATileToCharacterDataAddThatActualTileWithThatName() {
        Tile tile = mock(Tile.class);
        doReturn("tile4").when(tile).getName();
        characterData.setTile(tile);
        assertEquals(tile, characterData.getTile("tile4"));
    }

    @Test
    @DisplayName("Get tile actually fetches the right tile.")
    public void getTileActuallyFetchesTheRightTile() {
        assertEquals(tile1, characterData.getTile("tile1"));
        assertEquals(tile2, characterData.getTile("tile2"));
        assertEquals(tile3, characterData.getTile("tile3"));
    }

    @Test
    @DisplayName("Removing a tile removes that one specific tile and decreases all higher tile indexes.")
    public void removingATileRemovesThatOneSpecificTileAndDecreasesAllHigherTileIndexes() {
        assertEquals(3, characterData.tiles.size());
        assertEquals(2, characterData.getTileIndex("tile2"));
        characterData.removeTile("tile1");
        assertEquals(1, characterData.getTileIndex("tile2"));
        assertNull(characterData.getTile("tile1"));
        assertEquals(2, characterData.tiles.size());
    }

    @Test
    @DisplayName("Removing a tile removes that specific tile and leaves the lower tile indexes untouched.")
    public void removingATileRemovesThatSpecificTileAndLeavesTheLowerTileIndexesUntouched() {
        assertEquals(3, characterData.tiles.size());
        assertEquals(0, characterData.getTileIndex("tile3"));
        characterData.removeTile("tile1");
        assertEquals(0, characterData.getTileIndex("tile3"));
        assertNull(characterData.getTile("tile1"));
        assertEquals(2, characterData.tiles.size());
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

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(characterData);
        System.out.println(json);

        CharacterData parsedCharacterData = mapper.readValue(json, CharacterData.class);

        assertEquals(characterData, parsedCharacterData);
    }

}
