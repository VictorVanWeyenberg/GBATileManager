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
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class ScreenData {

    private final int width;
    private final int height;
    private final ScreenEntry[][] data;
    private int paletteNumber;

    private int screenSize;

    public ScreenData(int screenSize, int paletteNumber) {
        if (screenSize < 0 || screenSize > 3) {
            throw new IllegalArgumentException("Screen size out of bounds for screen data.");
        }

        this.screenSize = screenSize;
        this.width = screenSize % 2 == 0 ? 32 : 64;
        this.height = screenSize / 2 == 0 ? 32 : 64;
        this.data = new ScreenEntry[this.height][this.width];
        this.paletteNumber = paletteNumber;
    }

    @JsonCreator
    private ScreenData(
            @JsonProperty("screenSize") int screenSize,
            @JsonProperty("paletteNumber") int paletteNumber,
            @JsonProperty("data") ScreenEntry[][] data) {
        this(screenSize, paletteNumber);
        if (this.height != data.length || this.width != data[0].length) {
            throw new IllegalArgumentException("Screen size width or height do not match the screen data grid size.");
        }
        for (int y = 0; y < this.height; y++) {
            System.arraycopy(data[y], 0, this.data[y], 0, this.width);
        }
    }

    public ScreenEntry getEntry(int x, int y) {
        return data[y][x];
    }

    public void setEntry(int x, int y, int tileNumber) {
        this.setEntry(x, y, tileNumber, this.paletteNumber);
    }

    public void setEntry(int x, int y, int tileNumber, int paletteNumber) {
        ScreenEntry entry = new ScreenEntry(tileNumber, false, false, paletteNumber);
        setEntry(x, y, entry);
    }

    public void setEntry(int x, int y, ScreenEntry entry) {
        if (x < 0 || x >= this.width) {
            throw new IllegalArgumentException("X out of bounds for screen data width.");
        }
        if (y < 0 || y >= this.height) {
            throw new IllegalArgumentException("Y out of bounds for screen data height.");
        }
        data[y][x] = entry;
    }

    public int getPaletteNumber() {
        return paletteNumber;
    }

    public void setPaletteNumber(int paletteNumber) {
        this.paletteNumber = paletteNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenData that = (ScreenData) o;
        return paletteNumber == that.paletteNumber && screenSize == that.screenSize && Arrays.deepEquals(data, that.data);
    }

    public static final class Serializer extends StdSerializer<ScreenData> {

        public Serializer() {
            super(ScreenData.class);
        }

        @Override
        public void serialize(ScreenData screenData, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("screenSize", screenData.screenSize);
            jsonGenerator.writeNumberField("paletteNumber", screenData.paletteNumber);
            jsonGenerator.writeFieldName("data");
            jsonGenerator.writeStartArray();
            for (int y = 0; y < screenData.height; y++) {
                jsonGenerator.writeStartArray();
                for (int x = 0; x < screenData.width; x++) {
                    jsonGenerator.writeObject(screenData.data[y][x]);
                }
                jsonGenerator.writeEndArray();
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<ScreenData> {

        public Deserializer() {
            super(ScreenData.class);
        }

        @Override
        public ScreenData deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            int screenSize = node.get("screenSize").asInt();
            int paletteNumber = node.get("paletteNumber").asInt();
            Iterator<JsonNode> itY = node.get("data").elements();
            ObjectMapper mapper = new ObjectMapper();
            int yIndex = 0;
            int width = screenSize % 2 == 0 ? 32 : 64;
            int height = screenSize / 2 == 0 ? 32 : 64;
            ScreenEntry[][] data = new ScreenEntry[height][width];
            while (itY.hasNext()) {
                JsonNode rowJson = itY.next();
                Iterator<JsonNode> itX = rowJson.elements();
                int xIndex = 0;
                while (itX.hasNext()) {
                    JsonNode entryJson = itX.next();
                    ScreenEntry entry = mapper.treeToValue(entryJson, ScreenEntry.class);
                    data[yIndex][xIndex] = entry;
                    xIndex++;
                }
                yIndex++;
            }
            return new ScreenData(screenSize, paletteNumber, data);
        }
    }

}
