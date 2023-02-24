package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class Tile implements Cloneable {

    public static final Tile DEFAULT = new Tile("Default", null);
    private String name;
    private int[] tileData;

    @JsonCreator
    public Tile(@JsonProperty("name") String name, @JsonProperty("tileData") int[] tileData) {
        this.name = name;
        if (tileData == null) {
            tileData = new int[64];
        }
        if (tileData.length != 64) {
            throw new IllegalArgumentException("Tile data length must be 64.");
        }
        this.tileData = tileData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTileData(int x, int y) {
        if (x > 7 || x < 0) {
            throw new IllegalArgumentException("Argument X must be in bound of [0:7].");
        }
        if (y > 7 || y < 0) {
            throw new IllegalArgumentException("Argument Y must be in bound of [0:7].");
        }
        return tileData[y * 8 + x];
    }

    public void setTileData(int x, int y, int selectedColor) {
        if (x > 7 || x < 0) {
            throw new IllegalArgumentException("Argument X must be in bound of [0:7].");
        }
        if (y > 7 || y < 0) {
            throw new IllegalArgumentException("Argument Y must be in bound of [0:7].");
        }
        tileData[y * 8 + x] = selectedColor;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int index = y * 8 + x;
                int palette = tileData[index];
                if (palette == 0) {
                    sb.append(" ");
                } else {
                    sb.append(String.format("%X", palette));
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return name.equals(tile.name) && Arrays.equals(tileData, tile.tileData);
    }

    @Override
    public Tile clone() {
        try {
            Tile clone = (Tile) super.clone();
            clone.setName(this.getName());
            clone.tileData = this.tileData.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static final class Serializer extends StdSerializer<Tile> {

        public Serializer() {
            super(Tile.class);
        }

        @Override
        public void serialize(Tile tile, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("name", tile.getName());
            jsonGenerator.writeFieldName("tileData");
            jsonGenerator.writeStartArray();
            for (int data : tile.tileData) {
                jsonGenerator.writeNumber(data);
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<Tile> {

        public Deserializer() {
            super(Tile.class);
        }

        @Override
        public Tile deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String name = node.get("name").asText();
            int[] tileData = new int[64];
            Iterator<JsonNode> it = node.get("tileData").elements();
            int index = 0;
            while (it.hasNext()) tileData[index++] = it.next().asInt();
            return new Tile(name, tileData);
        }
    }
}
