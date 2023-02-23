package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Screen {

    private final List<ScreenData> screenBlocks;

    public Screen(int[] screenSizes) {
        if (screenSizes.length != 4) {
            throw new IllegalArgumentException("Four screen sizes must be passed.");
        }
        screenBlocks = new ArrayList<>();
        for (int index = 0; index < 4; index++) {
            screenBlocks.add(new ScreenData(screenSizes[index], index));
        }
    }

    @JsonCreator
    public Screen(@JsonProperty("screenBlocks") List<ScreenData> screenBlocks) {
        if (screenBlocks == null) {
            throw new IllegalArgumentException("Screen blocks is null.");
        }
        if (screenBlocks.size() != 4) {
            throw new IllegalArgumentException("Screen blocks must have size of 4.");
        }
        this.screenBlocks = screenBlocks;
    }

    public void setEntry(int backgroundNumber, int x, int y, ScreenEntry entry) {
        ScreenData screenData = screenBlocks.get(backgroundNumber);
        if (screenData == null) {
            return;
        }
        screenData.setEntry(x, y, entry);
    }

    public void setCharacterDataIndex(int screenDataIndex, int characterDataIndex) {
        screenBlocks.get(screenDataIndex).setCharacterDataIndex(characterDataIndex);
    }

    public void moveTileUp(int backgroundNumber, int tileNumber) {
        for (ScreenData screenData : screenBlocks) {
            if (screenData.getCharacterDataIndex() == backgroundNumber) {
                screenData.moveTileUp(tileNumber);
            }
        }
    }

    public void moveTileDown(int backgroundNumber, int tileNumber) {
        for (ScreenData screenData : screenBlocks) {
            if (screenData.getCharacterDataIndex() == backgroundNumber) {
                screenData.moveTileDown(tileNumber);
            }
        }
    }

    public void removeTile(int backgroundNumber, int tileNumber) {
        for (ScreenData screenData : screenBlocks) {
            if (screenData.getCharacterDataIndex() == backgroundNumber) {
                screenData.removeTile(tileNumber);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Screen screen = (Screen) o;
        return screenBlocks.equals(screen.screenBlocks);
    }

    public static final class Serializer extends StdSerializer<Screen> {

        public Serializer() {
            super(Screen.class);
        }

        @Override
        public void serialize(Screen screen, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeArrayFieldStart("screenBlocks");
            for (ScreenData screenData : screen.screenBlocks) {
                jsonGenerator.writeObject(screenData);
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<Screen> {

        public Deserializer() {
            super(Screen.class);
        }

        @Override
        public Screen deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            ObjectMapper mapper = new ObjectMapper();
            Iterator<JsonNode> screenBlocksIterator = node.get("screenBlocks").elements();
            List<ScreenData> screenBlocks = new ArrayList<>();
            screenBlocksIterator.forEachRemaining(sbn -> {
                try {
                    screenBlocks.add(mapper.treeToValue(sbn, ScreenData.class));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
            return new Screen(screenBlocks);
        }
    }

}
