package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class BackgroundMap {
    private final boolean[] enabledBackgrounds;
    private final int[] screenSizes;
    private final ScreenData[] screenData;

    public BackgroundMap(boolean bg0Enabled, boolean bg1Enabled, boolean bg2Enabled, boolean bg3Enabled,
                        int screenSizeBG0, int screenSizeBG1, int screenSizeBG2, int screenSizeBG3) {
        if (screenSizeBG0 < 0 || screenSizeBG0 > 3 ||
                screenSizeBG1 < 0 || screenSizeBG1 > 3 ||
                screenSizeBG2 < 0 || screenSizeBG2 > 3 ||
                screenSizeBG3 < 0 || screenSizeBG3 > 3) {
            throw new IllegalArgumentException("Screen size must be within bounds.");
        }
        enabledBackgrounds = new boolean[] {bg0Enabled, bg1Enabled, bg2Enabled, bg3Enabled};
        screenSizes = new int[] {screenSizeBG0, screenSizeBG1, screenSizeBG2, screenSizeBG3};
        screenData = new ScreenData[4];
        for (int index = 0; index < 4; index++) {
            if (!enabledBackgrounds[index]) continue;
            screenData[index] = new ScreenData(screenSizes[index], null);
        }
    }

    @JsonCreator
    public BackgroundMap(@JsonProperty("enabledBackgrounds") boolean[] enabledBackgrounds,
                         @JsonProperty("screenSizes") int[] screenSizes,
                         @JsonProperty("screenData") ScreenData[] screenData) {
        if (Arrays.stream(screenSizes).filter(s -> s < 0 || s > 3).count() > 0) {
            throw new IllegalArgumentException("Screen size must be within bounds.");
        }
        this.enabledBackgrounds = enabledBackgrounds;
        this.screenSizes = screenSizes;
        this.screenData = screenData;
    }

    public ScreenData getScreenData(int backgroundBNumber) {
        return screenData[backgroundBNumber];
    }

    public void setScreenData(int backgroundNumber, int x, int y, int tileNumber) {
        setScreenData(backgroundNumber, x, y, tileNumber, 0);
    }

    public void setScreenData(int backgroundNumber, int x, int y, int tileNumber, int paletteNumber) {
        screenData[backgroundNumber].setEntry(x, y, tileNumber, paletteNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackgroundMap that = (BackgroundMap) o;
        return Arrays.equals(enabledBackgrounds, that.enabledBackgrounds) && Arrays.equals(screenSizes, that.screenSizes) && Arrays.equals(screenData, that.screenData);
    }

    public static final class Serializer extends StdSerializer<BackgroundMap> {

        Serializer() {
            super(BackgroundMap.class);
        }

        @Override
        public void serialize(BackgroundMap backgroundMap, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeArrayFieldStart("enabledBackgrounds");
            for (boolean enabledBackground : backgroundMap.enabledBackgrounds) {
                jsonGenerator.writeBoolean(enabledBackground);
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeArrayFieldStart("screenSizes");
            for (int screenSize : backgroundMap.screenSizes) {
                jsonGenerator.writeNumber(screenSize);
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeArrayFieldStart("screenData");
            for (ScreenData screenData : backgroundMap.screenData) {
                jsonGenerator.writeObject(screenData);
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<BackgroundMap> {

        Deserializer() {
            super(BackgroundMap.class);
        }

        @Override
        public BackgroundMap deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            Iterator<JsonNode> enabledBackgroundNodes = node.get("enabledBackgrounds").elements();
            boolean[] enabledBackgrounds = new boolean[4];
            int index = 0;
            while (enabledBackgroundNodes.hasNext()) {
                enabledBackgrounds[index++] = enabledBackgroundNodes.next().asBoolean();
            }
            Iterator<JsonNode> screenSizeNodes = node.get("screenSizes").elements();
            int[] screenSizes = new int[4];
            index = 0;
            while (screenSizeNodes.hasNext()) {
                screenSizes[index++] = screenSizeNodes.next().asInt();
            }
            Iterator<JsonNode> screenDataNodes = node.get("screenData").elements();
            ScreenData[] screenData = new ScreenData[4];
            index = 0;
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            while (screenDataNodes.hasNext()) {
                JsonNode screenDataNode = screenDataNodes.next();
                if (screenDataNode != null) {
                    screenData[index] = mapper.treeToValue(screenDataNode, ScreenData.class);
                }
                index++;
            }
            return new BackgroundMap(enabledBackgrounds, screenSizes, screenData);
        }
    }
}
