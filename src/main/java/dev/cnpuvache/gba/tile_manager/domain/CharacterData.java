package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Block of 16KBytes managing the Tile data of a background.
 * One tile is 32 bytes so one CharacterData can hold 16384 / 32 = 512 tiles.
 */
public class CharacterData {

    private final List<Tile> tiles;

    private final boolean colorsNotPalettes;

    public CharacterData(boolean colorsNotPalettes) {
        this.tiles = new ArrayList<>();
        this.colorsNotPalettes = colorsNotPalettes;
    }

    @JsonCreator
    private CharacterData(@JsonProperty("colorsNotPalettes") boolean colorsNotPalettes, @JsonProperty("tiles") List<Tile> tiles) {
        this.tiles = tiles;
        this.colorsNotPalettes = colorsNotPalettes;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void addTile(Tile tile) {
        if (tiles.size() == 512) {
            throw new IndexOutOfBoundsException("CharacterData is full. No more tiles can be added.");
        }
        tiles.add(tile);
    }

    public Tile getTile(int index) {
        return tiles.get(index);
    }

    public void setTile(int index, Tile tile) {
        if (index < 0 || index > 511) {
            throw new IllegalArgumentException("Index out of bounds for setting a tile in character data.");
        }
        tiles.set(index, tile);
    }

    public void addC(String c) {
        Tile tile = Tile.fromC(c, colorsNotPalettes);
        addTile(tile);
    }

    public static CharacterData fromC(String contents, boolean colorsNotPalettes) throws IOException {
        CharacterData characterData = new CharacterData(colorsNotPalettes);
        Pattern pattern = Pattern.compile("\\{\\s(0x[\\da-fA-F]{4},\\s){15}0x[\\da-fA-F]{4}\\s\\}");
        Matcher matcher = pattern.matcher(contents);
        matcher.results().forEach(c-> characterData.addC(c.group()));
        return characterData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CharacterData that = (CharacterData) o;
        return colorsNotPalettes == that.colorsNotPalettes && tiles.equals(that.tiles);
    }

    public ByteBuffer toC() {
        ByteBuffer buffer = ByteBuffer.allocate(tiles.size() * 32);
        for (Tile tile : tiles) {
            buffer.put(tile.toC().array());
        }
        return buffer;
    }

    public boolean removeTile(String tileName) {
        Tile tile = tiles.stream()
                .filter(Objects::nonNull)
                .filter(t -> Objects.nonNull(t.getName()))
                .filter(t -> t.getName().equals(tileName))
                .findFirst()
                .orElse(null);
        if (tile == null) return false;
        return tiles.remove(tile);
    }

    public void renameTile(String oldName, String newName) {
        Tile tile = tiles.stream()
                .filter(t -> t.getName().equals(oldName))
                .findFirst().orElse(null);
        if (tile == null) return;
        tile.setName(newName);
    }

    public void moveTileUp(String tileName) {
        Tile tile = tiles.stream()
                .filter(Objects::nonNull)
                .filter(t -> Objects.nonNull(t.getName()))
                .filter(t -> t.getName().equals(tileName))
                .findFirst()
                .orElse(null);
        if (tile == null) {
            System.out.printf("Tile with name %s not found in character data.", tileName);
            return;
        }
        System.out.println(tile);
        int index = tiles.indexOf(tile);
        if (index == 0) {
            System.out.println("Tile has index 0 so can't be moved up.");
            return;
        }
        System.out.println("Swapping...");
        Collections.swap(tiles, index, index - 1);
    }

    public void moveTileDown(String tileName) {
        Tile tile = tiles.stream()
                .filter(Objects::nonNull)
                .filter(t -> Objects.nonNull(t.getName()))
                .filter(t -> t.getName().equals(tileName))
                .findFirst()
                .orElse(null);
        if (tile == null) {
            System.out.printf("Tile with name %s not found in character data.", tileName);
            return;
        }
        int index = tiles.indexOf(tile);
        if (index + 1 == tiles.size()) return;
        Collections.swap(tiles, index, index + 1);
    }

    public static final class Serializer extends StdSerializer<CharacterData> {

        public Serializer() {
            super(CharacterData.class);
        }

        @Override
        public void serialize(CharacterData characterData, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeBooleanField("colorsNotPalettes", characterData.colorsNotPalettes);
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
            boolean colorsNotPalettes = node.get("colorsNotPalettes").asBoolean();
            List<Tile> tiles = new ArrayList<>();
            Iterator<JsonNode> it = node.get("tiles").elements();
            ObjectMapper mapper = new ObjectMapper();
            while (it.hasNext()) tiles.add(mapper.treeToValue(it.next(), Tile.class));
            CharacterData characterData = new CharacterData(colorsNotPalettes, tiles);
            return characterData;
        }
    }

}
