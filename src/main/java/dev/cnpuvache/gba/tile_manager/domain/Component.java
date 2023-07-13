package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.Objects;
import java.util.UUID;

public class Component {

    private final UUID id;
    private final int beginX;
    private final int beginY;
    private int endX;
    private int endY;

    private transient Component north;
    private transient Component east;
    private transient Component south;
    private transient Component west;

    public Component(int beginX, int beginY, int endX, int endY) {
        this(UUID.randomUUID(), beginX, beginY, endX, endY);
    }

    @JsonCreator
    public Component(
            @JsonProperty("id") UUID id,
            @JsonProperty("beginX") int beginX,
            @JsonProperty("beginY") int beginY,
            @JsonProperty("endX") int endX,
            @JsonProperty("endY") int endY
    ) {
        this.id = UUID.randomUUID();
        this.beginX = beginX;
        this.beginY = beginY;
        this.endX = endX;
        this.endY = endY;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public UUID getId() {
        return id;
    }

    public int getBeginX() {
        return beginX;
    }

    public int getBeginY() {
        return beginY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public void reset() {
        this.north = null;
        this.east = null;
        this.south = null;
        this.west = null;
    }

    public Component getNorth() {
        return north;
    }

    public void setNorth(Component north) {
        this.north = north;
    }

    public Component getEast() {
        return east;
    }

    public void setEast(Component east) {
        this.east = east;
    }

    public Component getSouth() {
        return south;
    }

    public void setSouth(Component south) {
        this.south = south;
    }

    public Component getWest() {
        return west;
    }

    public void setWest(Component west) {
        this.west = west;
    }

    @Override
    public String toString() {
        return "Component{" +
                ", beginX=" + getBeginX() +
                ", beginY=" + getBeginY() +
                ", endX=" + getEndX() +
                ", endY=" + getEndY() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Objects.equals(id, component.id);
    }

    public static final class Serializer extends StdSerializer<Component> {

        public Serializer() {
            super(Component.class);
        }

        @Override
        public void serialize(Component component, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("id", component.id.toString());
            jsonGenerator.writeNumberField("beginX", component.beginX);
            jsonGenerator.writeNumberField("beginY", component.beginY);
            jsonGenerator.writeNumberField("endX", component.endX);
            jsonGenerator.writeNumberField("endY", component.endY);
            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<Component> {

        public Deserializer() {
            super(Component.class);
        }

        @Override
        public Component deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            UUID id = UUID.fromString(node.get("id").asText());
            int beginX = node.get("beginX").asInt();
            int beginY = node.get("beginY").asInt();
            int endX = node.get("endX").asInt();
            int endY = node.get("endY").asInt();
            return new Component(id, beginX, beginY, endX, endY);
        }
    }
}
