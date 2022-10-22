package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class Tile {
    public static int INCREMENTER = 0;
    private final int[] tileData;
    private final boolean colorsNoPalettes;

    private final int id;
    private String name;

    /**
     * MUST STAY PRIVATE.
     * @param colorsNoPalettes
     */
    private Tile(boolean colorsNoPalettes) {
        this.colorsNoPalettes = colorsNoPalettes;
        this.tileData = new int[64];
        this.id = ++INCREMENTER;
        this.name = null;
    }

    public Tile(boolean colorsNoPalettes, String name) {
        this.colorsNoPalettes = colorsNoPalettes;
        this.tileData = new int[64];
        this.id = ++INCREMENTER;
        this.name = name;
    }

    @JsonCreator
    public Tile(@JsonProperty("colorsNotPalettes") boolean colorsNoPalettes, @JsonProperty("name") String name, @JsonProperty("tileData") int[] tileData) {
        if (tileData.length != 64) {
            throw new IllegalArgumentException("Tile data length must be 64.");
        }
        this.colorsNoPalettes = colorsNoPalettes;
        this.tileData = tileData;
        this.name = name;
        this.id = ++INCREMENTER;
    }

    public String getName() {
        if (this.name == null) {
            return String.format("TIL%03d", id);
        } else {
            return this.name;
        }
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

    public void setTileData(int x, int y, int color) {
        if (x > 7 || x < 0) {
            throw new IllegalArgumentException("Argument X must be in bound of [0:7].");
        }
        if (y > 7 || y < 0) {
            throw new IllegalArgumentException("Argument Y must be in bound of [0:7].");
        }
        if ( color < 0 || color > (colorsNoPalettes ? 255 : 15)) {
            throw new IllegalArgumentException("Argument color must be in bounds for background palette mode.");
        }
        tileData[y * 8 + x] = color;
    }

    public String toCCode() {
        String[] halfRows = new String[this.tileData.length / 4];
        for (int index = 0; index < this.tileData.length / 4; index++) {
            StringBuilder halfRowBuilder = new StringBuilder("0x");
            for (int i = 3; i >= 0; i--) {
                halfRowBuilder.append(Integer.toHexString(tileData[index * 4 + i]));
            }
            halfRows[index] = halfRowBuilder.toString();
        }
        return "{ " + Arrays.stream(halfRows).reduce((s1, s2) -> s1 + ", " + s2).orElse("") + " }";
    }

    public static Tile fromC(String c, boolean colorsNoPalettes) {
        Tile tile = new Tile(colorsNoPalettes);
        String[] ctjes = c.replaceAll("\\{ ", "").replaceAll(" }", "").replaceAll("0x", "").split(", ");
        for (int index = 0; index < ctjes.length; index++) {
            int y = index / 2;
            for (int x = index % 2 == 0 ? 0 : 4; x < (index % 2 == 0 ? 4 : 8); x++) {
                tile.setTileData(x, y, (int) Long.parseLong(String.valueOf(ctjes[index].charAt(3 - (x % 4))), 16));
            }
        }
        return tile;
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
        return colorsNoPalettes == tile.colorsNoPalettes && Arrays.equals(tileData, tile.tileData) && name.equals(tile.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(colorsNoPalettes, name);
        result = 31 * result + Arrays.hashCode(tileData);
        return result;
    }

    public ByteBuffer toC() {
        ByteBuffer buffer = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN);
        for (int index = 0; index < this.tileData.length; index+=2) {
            int rightDot = tileData[index + 1];
            int leftDot = tileData[index];
            byte dots = (byte) (leftDot | (rightDot << 4));
            buffer.put(dots);
        }
        return buffer;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public static final class Serializer extends StdSerializer<Tile> {

        public Serializer() {
            super(Tile.class);
        }

        @Override
        public void serialize(Tile tile, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeBooleanField("colorsNotPalettes", tile.colorsNoPalettes);
            if (tile.name != null) {
                jsonGenerator.writeStringField("name", tile.name);
            }
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
        public Tile deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            boolean colorsNotPalettes = node.get("colorsNotPalettes").asBoolean();
            JsonNode nameNode = node.get("name");
            String name = null;
            if (nameNode != null) {
                name = nameNode.asText();
            }
            int[] tileData = new int[64];
            Iterator<JsonNode> it = node.get("tileData").elements();
            int index = 0;
            while (it.hasNext()) tileData[index++] = it.next().asInt();
            return new Tile(colorsNotPalettes, name, tileData);
        }
    }
}
