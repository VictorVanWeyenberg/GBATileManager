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
import java.util.List;

/**
 * GBA can display four backgrounds with each their own size, priority, palette type etc.
 */
public class Background {

    private final int priority;
    /**
     * Place in VRAM in increments of 16KB to store tile data.
     */
    private final int characterBaseBlock;
    private final boolean mosaicEnable = false;
    private final boolean colorsNotPalettes;
    /**
     * Place in VRAM in increments of 2KB to store map data.
     */
    private final int screenBaseBlock;
    private final int screenSize;
    private final CharacterData characterData;

    private Background(int priority, int characterBaseBlock, boolean colorsNotPalettes, int screenBaseBlock,
                       int screenSize) {
        this(priority, characterBaseBlock, colorsNotPalettes, screenBaseBlock, screenSize, null);
    }

    @JsonCreator
    private Background(
            @JsonProperty("priority") int priority,
            @JsonProperty("characterBaseBlock") int characterBaseBlock,
            @JsonProperty("colorsNotPalettes") boolean colorsNotPalettes,
            @JsonProperty("screenBaseBlock") int screenBaseBlock,
            @JsonProperty("screenSize") int screenSize,
            @JsonProperty("characterData") CharacterData characterData) {
        if (priority < 0 || priority > 3) {
            throw new IllegalArgumentException("Priority out of bounds.");
        }
        if (characterBaseBlock < 0 || characterBaseBlock > 3) {
            throw new IllegalArgumentException("Character base block out of bounds.");
        }
        if (screenBaseBlock < 0 || screenBaseBlock > 31) {
            throw new IllegalArgumentException("Screen base block out of bounds.");
        }
        if (screenSize < 0 || screenSize > 3) {
            throw new IllegalArgumentException("Screen size out of bounds.");
        }
        if (characterData == null) {
            characterData = new CharacterData(colorsNotPalettes);
        }
        this.priority = priority;
        this.characterBaseBlock = characterBaseBlock;
        this.colorsNotPalettes = colorsNotPalettes;
        this.screenBaseBlock = screenBaseBlock;
        this.screenSize = screenSize;

        this.characterData = characterData;
    }

    public void addTileToCharacterData(String tileName) {
        Tile tile = new Tile(colorsNotPalettes, tileName);
        characterData.addTile(tile);
    }

    public boolean deleteTileFromCharacterData(String tileName) {
        return this.characterData.removeTile(tileName);
    }

    public List<Tile> getTiles() {
        return characterData.getTiles();
    }

    public int getScreenSize() {
        return screenSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Background that = (Background) o;
        return priority == that.priority &&
                characterBaseBlock == that.characterBaseBlock &&
                mosaicEnable == that.mosaicEnable &&
                colorsNotPalettes == that.colorsNotPalettes &&
                screenBaseBlock == that.screenBaseBlock &&
                screenSize == that.screenSize &&
                characterData.equals(that.characterData);
    }

    public void renameTile(String oldName, String newName) {
        characterData.renameTile(oldName, newName);
    }

    public void moveTileUp(String tileName) {
        characterData.moveTileUp(tileName);
    }

    public void moveTileDown(String tileName) {
        characterData.moveTileDown(tileName);
    }

    public static class Builder {
        private final int priority;
        private final int characterBaseBlock;
        private final boolean colorsNotPalettes;
        private final int screenBaseBlock;
        private final int screenSize;

        private CharacterData characterData;

        public Builder(int priority, int characterBaseBlock, boolean colorsNotPalettes, int screenBaseBlock,
                           int screenSize) {
            this.priority = priority;
            this.characterBaseBlock = characterBaseBlock;
            this.colorsNotPalettes = colorsNotPalettes;
            this.screenBaseBlock = screenBaseBlock;
            this.screenSize = screenSize;
        }

        public Builder setCharacterData(CharacterData characterData) {
            this.characterData = characterData;
            return this;
        }

        public Background build() {
            return new Background(priority, characterBaseBlock, colorsNotPalettes, screenBaseBlock, screenSize,
                    characterData);
        }
    }

    public static final class Serializer extends StdSerializer<Background> {

        public Serializer() {
            super(Background.class);
        }

        @Override
        public void serialize(Background background, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("priority", background.priority);
            jsonGenerator.writeNumberField("characterBaseBlock", background.characterBaseBlock);
            jsonGenerator.writeBooleanField("colorsNotPalettes", background.colorsNotPalettes);
            jsonGenerator.writeNumberField("screenBaseBlock", background.screenBaseBlock);
            jsonGenerator.writeNumberField("screenSize", background.screenSize);
            jsonGenerator.writeFieldName("characterData");
            jsonGenerator.writeObject(background.characterData);
            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<Background> {

        public Deserializer() {
            super(Background.class);
        }

        @Override
        public Background deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            int priority = node.get("priority").asInt();
            int characterBaseBlock = node.get("characterBaseBlock").asInt();
            boolean colorsNotPalettes = node.get("colorsNotPalettes").asBoolean();
            int screenBaseBlock = node.get("screenBaseBlock").asInt();
            int screenSize = node.get("screenSize").asInt();

            ObjectMapper mapper = new ObjectMapper();
            CharacterData characterData = mapper.treeToValue(node.get("characterData"), CharacterData.class);

            return new Background(priority, characterBaseBlock, colorsNotPalettes, screenBaseBlock, screenSize,
                    characterData);
        }
    }

}
