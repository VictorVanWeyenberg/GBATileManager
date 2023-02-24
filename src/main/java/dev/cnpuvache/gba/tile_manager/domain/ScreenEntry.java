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

public class ScreenEntry {

    private int tileNumber;
    private boolean horizontalFlip;
    private boolean verticalFlip;
    private int paletteNumber;

    @JsonCreator
    public ScreenEntry(@JsonProperty("tileNumber") int tileNumber,
                       @JsonProperty("horizontalFlip") boolean horizontalFlip,
                       @JsonProperty("verticalFlip") boolean verticalFlip,
                       @JsonProperty("paletteNumber") int paletteNumber) {
        if (tileNumber < 0 || tileNumber > 1023) {
            throw new IllegalArgumentException("Tile number out of bounds for Screen data entry.");
        }
        if (paletteNumber < 0 || paletteNumber > 15) {
            throw new IllegalArgumentException("Palette number out of bounds for Screen data entry.");
        }
        this.tileNumber = tileNumber;
        this.horizontalFlip = horizontalFlip;
        this.verticalFlip = verticalFlip;
        this.paletteNumber = paletteNumber;
    }

    public int getTileNumber() {
        return tileNumber;
    }

    public boolean isHorizontalFlip() {
        return horizontalFlip;
    }

    public boolean isVerticalFlip() {
        return verticalFlip;
    }

    public int getPaletteNumber() {
        return paletteNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScreenEntry that = (ScreenEntry) o;
        return tileNumber == that.tileNumber && horizontalFlip == that.horizontalFlip && verticalFlip == that.verticalFlip && paletteNumber == that.paletteNumber;
    }

    public void setPaletteNumber(int paletteNumber) {
        this.paletteNumber = paletteNumber;
    }

    public void setHorizontalFlip(boolean horizontalFlip) {
        this.horizontalFlip = horizontalFlip;
    }

    public void setVerticalFlip(boolean verticalFlip) {
        this.verticalFlip = verticalFlip;
    }

    public void setTileNumber(int tileNumber) {
        this.tileNumber = tileNumber;
    }

    public static final class Serializer extends StdSerializer<ScreenEntry> {

        public Serializer() {
            super(ScreenEntry.class);
        }

        @Override
        public void serialize(ScreenEntry screenEntry, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("tileNumber", screenEntry.tileNumber);
            jsonGenerator.writeBooleanField("horizontalFlip", screenEntry.horizontalFlip);
            jsonGenerator.writeBooleanField("verticalFlip", screenEntry.verticalFlip);
            jsonGenerator.writeNumberField("paletteNumber", screenEntry.paletteNumber);
            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<ScreenEntry> {

        public Deserializer() {
            super(ScreenEntry.class);
        }

        @Override
        public ScreenEntry deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            int tileNumber = node.get("tileNumber").asInt();
            boolean horizontalFlip = node.get("horizontalFlip").asBoolean();
            boolean verticalFlip = node.get("verticalFlip").asBoolean();
            int paletteNumber = node.get("paletteNumber").asInt();
            return new ScreenEntry(tileNumber, horizontalFlip, verticalFlip, paletteNumber);
        }
    }

}
