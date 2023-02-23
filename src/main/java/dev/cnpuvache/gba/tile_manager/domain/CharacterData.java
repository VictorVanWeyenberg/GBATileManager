package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;

/**
 * Block of 16KBytes managing the Tile data of a background.
 * One tile is 32 bytes so one CharacterData can hold 16384 / 32 = 512 tiles.
 */
public class CharacterData {

    @JsonDeserialize(using = CharacterData.CharacterDataTilesMapDeserializer.class)
    protected final List<Tile> tiles;

    public CharacterData(@JsonProperty("tiles") List<Tile> tiles) {
        this.tiles = Objects.requireNonNullElseGet(tiles, ArrayList::new);
    }

    public void setTile(Tile tile) {
        if (tiles.size() == 512) {
            throw new IndexOutOfBoundsException("CharacterData is full. No more tiles can be added.");
        }
        tiles.add(tile);
    }

    public Tile getTile(int index) {
        return tiles.get(index);
    }

    public int moveTileUp(String name) {
        int index = getTileIndex(name);
        if (index <= 0) {
            return -1;
        }
        Collections.swap(tiles, index, index - 1);
        return index;
    }

    public int moveTileDown(String name) {
        int index = getTileIndex(name);
        if (index == tiles.size() - 1 || index == -1) {
            return -1;
        }
        Collections.swap(tiles, index, index + 1);
        return index;
    }

    public Tile getTile(String name) {
        if (name == null) {
            return null;
        }
        return tiles.stream()
                .filter(t -> name.equals(t.getName()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterData that = (CharacterData) o;
        return tiles.equals(that.tiles);
    }

    public int getTileIndex(String name) {
        if (name == null) {
            return -1;
        }
        for (int index = 0; index < tiles.size(); index++) {
            if (name.equals(tiles.get(index).getName())) {
                return index;
            }
        }
        return -1;
    }

    public int removeTile(String tileName) {
        if (tileName == null) {
            return -1;
        }
        int index = getTileIndex(tileName);
        if (index == -1) {
            return -1;
        }
        tiles.remove(index);
        return index;
    }

    public void addTile(String name) {
        if (tiles.size() == 512) {
            throw new IndexOutOfBoundsException("CharacterData is full. No more tiles can be added.");
        }
        Tile tile = new Tile(name, null);
        tiles.add(tile);
    }

    public static final class Serializer extends StdSerializer<CharacterData> {

        public Serializer() {
            super(CharacterData.class);
        }

        @Override
        public void serialize(CharacterData characterData, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeFieldName("tiles");
            jsonGenerator.writeStartArray();
            for (Tile tile : characterData.tiles) {
                jsonGenerator.writeObject(tile);
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<CharacterData> {

        public Deserializer() {
            super(CharacterData.class);
        }

        @Override
        public CharacterData deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            Iterator<JsonNode> it = node.get("tiles").elements();
            ObjectMapper mapper = new ObjectMapper();

            List<Tile> tiles = new ArrayList<>();
            while (it.hasNext()) {
                JsonNode tileNode = it.next();
                Tile tile = mapper.treeToValue(tileNode, Tile.class);
                tiles.add(tile);
            }
            return new CharacterData(tiles);
        }
    }

    private static final class CharacterDataTilesMapDeserializer extends StdDeserializer<List<Tile>> {

        CharacterDataTilesMapDeserializer() {
            super(List.class);
        }

        @Override
        public List<Tile> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            List<Tile> ret = new ArrayList<>();
            TreeNode node = jsonParser.getCodec().readTree(jsonParser);
            ObjectMapper mapper = new ObjectMapper();

            if (node.isArray()) {
                for (JsonNode n : (ArrayNode)node) {
                    Tile tile = mapper.treeToValue(n, Tile.class);
                    ret.add(tile);
                }
            }

            return ret;
        }
    }

}
