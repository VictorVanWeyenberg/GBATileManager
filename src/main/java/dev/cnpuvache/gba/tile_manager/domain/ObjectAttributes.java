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

public class ObjectAttributes {

    private final int x;
    private final int y;
    private final int tileNumber;
    private final int paletteNumber;
    private final int priority;

    @JsonCreator
    public ObjectAttributes(
            @JsonProperty("x") int x,
            @JsonProperty("y") int y,
            @JsonProperty("tileNumber") int tileNumber,
            @JsonProperty("paletteNumber") int paletteNumber,
            @JsonProperty("priority") int priority
    ) {
        if (x < 0 || x > 63) {
            throw new IllegalArgumentException("X must be in interval [0:63].");
        }
        if (y < 0 || y > 63) {
            throw new IllegalArgumentException("Y must be in interval [0:63].");
        }
        if (tileNumber < 0 || tileNumber > 1023) {
            throw new IllegalArgumentException("Tile number must be in interval [0:1023].");
        }
        if (paletteNumber < 0 || paletteNumber > 15) {
            throw new IllegalArgumentException("Palette number must be in interval [0:15].");
        }
        if (priority < 0 || priority > 3) {
            throw new IllegalArgumentException("Priority must be in interval [0:3].");
        }
        this.x = x;
        this.y = y;
        this.tileNumber = tileNumber;
        this.paletteNumber = paletteNumber;
        this.priority = priority;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTileNumber() {
        return tileNumber;
    }

    public int getPaletteNumber() {
        return paletteNumber;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectAttributes that = (ObjectAttributes) o;
        return x == that.x && y == that.y && tileNumber == that.tileNumber && paletteNumber == that.paletteNumber && priority == that.priority;
    }

    public static final class Serializer extends StdSerializer<ObjectAttributes> {

        public Serializer() {
            super(ObjectAttributes.class);
        }

        @Override
        public void serialize(ObjectAttributes objectAttributes, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("x", objectAttributes.x);
            jsonGenerator.writeNumberField("y", objectAttributes.y);
            jsonGenerator.writeNumberField("tileNumber", objectAttributes.tileNumber);
            jsonGenerator.writeNumberField("paletteNumber", objectAttributes.paletteNumber);
            jsonGenerator.writeNumberField("priority", objectAttributes.priority);
            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<ObjectAttributes> {

        public Deserializer() {
            super(ObjectAttributes.class);
        }

        @Override
        public ObjectAttributes deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            int x = node.get("x").asInt();
            int y = node.get("y").asInt();
            int tileNumber = node.get("tileNumber").asInt();
            int paletteNumber = node.get("paletteNumber").asInt();
            int priority = node.get("priority").asInt();
            return new ObjectAttributes(x, y, tileNumber, paletteNumber, priority);
        }
    }

}
