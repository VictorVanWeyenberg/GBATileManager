package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.*;

public class ScreenData {

    private final int width;
    private final int height;
    @JsonDeserialize(using = ScreenDataDataDeserializer.class)
    private final TreeMap<Integer, Map<Integer, ScreenEntry>> data;
    // private final ScreenEntry[][] data;

    private final int screenSize;

    public ScreenData(int screenSize) {
        this(screenSize, null);
    }

    @JsonCreator
    public ScreenData(
            @JsonProperty("screenSize") int screenSize,
            @JsonProperty("data") TreeMap<Integer, Map<Integer, ScreenEntry>> data) {
        this.screenSize = screenSize;
        this.width = screenSize % 2 == 0 ? 32 : 64;
        this.height = screenSize / 2 == 0 ? 32 : 64;
        if (data == null) {
            this.data = new TreeMap<>();
        } else {
            this.data = data;
        }
    }

    public ScreenEntry getEntry(int x, int y) {
        Map<Integer, ScreenEntry> row = data.get(y);
        if (row == null) return null;
        return row.get(x);
    }

    public void setEntry(int x, int y, int tileNumber) {
        this.setEntry(x, y, tileNumber, 0);
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
        if (!data.containsKey(y)) {
            data.put(y, new TreeMap<>());
        }
        data.get(y).put(x, entry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenData that = (ScreenData) o;
        return width == that.width && height == that.height && screenSize == that.screenSize && data.equals(that.data);
    }

    public static final class Serializer extends StdSerializer<ScreenData> {

        public Serializer() {
            super(ScreenData.class);
        }

        @Override
        public void serialize(ScreenData screenData, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("screenSize", screenData.screenSize);
            jsonGenerator.writeArrayFieldStart("data");
            for (Map.Entry<Integer, Map<Integer, ScreenEntry>> yEntry : screenData.data.entrySet()) {
                int y = yEntry.getKey();
                for (Map.Entry<Integer, ScreenEntry> xEntry : yEntry.getValue().entrySet()) {
                    int x = xEntry.getKey();
                    ScreenEntry entry = xEntry.getValue();
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeNumberField("x", x);
                    jsonGenerator.writeNumberField("y", y);
                    jsonGenerator.writeObjectField("entry", entry);
                    jsonGenerator.writeEndObject();
                }
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
            ScreenData screenData = new ScreenData(screenSize);
            ObjectMapper mapper = new ObjectMapper();
            Iterator<JsonNode> dataIterator = node.get("data").elements();
            while (dataIterator.hasNext()) {
                JsonNode dataObject = dataIterator.next();
                int x = dataObject.get("x").asInt();
                int y = dataObject.get("y").asInt();
                ScreenEntry entry = mapper.treeToValue(dataObject.get("entry"), ScreenEntry.class);
                screenData.setEntry(x, y, entry);
            }
            return screenData;
        }
    }

    private static final class ScreenDataDataDeserializer extends StdDeserializer<TreeMap<Integer, Map<Integer, ScreenEntry>>> {

        ScreenDataDataDeserializer() {
            super(TreeMap.class);
        }

        @Override
        public TreeMap<Integer, Map<Integer, ScreenEntry>> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            TreeMap<Integer, Map<Integer, ScreenEntry>> ret = new TreeMap<>();
            TreeNode node = jsonParser.getCodec().readTree(jsonParser);
            ObjectMapper mapper = new ObjectMapper();

            if (node.isArray()) {
                for (JsonNode n : (ArrayNode)node) {
                    int x = n.get("x").asInt();
                    int y = n.get("y").asInt();
                    ScreenEntry entry = mapper.treeToValue(n.get("entry"), ScreenEntry.class);
                    if (!ret.containsKey(y)) {
                        ret.put(y, new TreeMap<>());
                    }
                    ret.get(y).put(x, entry);
                }
            }
            return ret;
        }
    }

}
