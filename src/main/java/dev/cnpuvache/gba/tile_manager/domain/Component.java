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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Component {

    private final UUID id;
    private final int beginX;
    private final int beginY;
    private int endX;
    private int endY;
    private int callbackIndex;
    private List<Integer> args;

    private transient Component north;
    private transient Component east;
    private transient Component south;
    private transient Component west;

    public Component(int beginX, int beginY, int endX, int endY) {
        this(UUID.randomUUID(), beginX, beginY, endX, endY, 0, new ArrayList<>());
    }

    @JsonCreator
    public Component(
            @JsonProperty("id") UUID id,
            @JsonProperty("beginX") int beginX,
            @JsonProperty("beginY") int beginY,
            @JsonProperty("endX") int endX,
            @JsonProperty("endY") int endY,
            @JsonProperty("callback_index") int callbackIndex,
            @JsonProperty("args") List<Integer> args
    ) {
        this.id = UUID.randomUUID();
        this.beginX = beginX;
        this.beginY = beginY;
        this.endX = endX;
        this.endY = endY;
        this.callbackIndex = callbackIndex;
        this.args = args;
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

    public int getCallbackIndex() {
        return callbackIndex;
    }

    public void setCallbackIndex(int callbackIndex) {
        this.callbackIndex = callbackIndex;
    }

    public List<Integer> getArgs() {
        return args;
    }

    public void setArgs(List<Integer> args) {
        this.args = args;
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
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("id", component.id.toString());
                jsonGenerator.writeNumberField("beginX", component.beginX);
                jsonGenerator.writeNumberField("beginY", component.beginY);
                jsonGenerator.writeNumberField("endX", component.endX);
                jsonGenerator.writeNumberField("endY", component.endY);
                jsonGenerator.writeNumberField("callback_index", component.callbackIndex);
                jsonGenerator.writeArrayFieldStart("args");
                component.args.forEach(a -> {
                    try {
                        jsonGenerator.writeNumber(a);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                jsonGenerator.writeEndArray();
                jsonGenerator.writeEndObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
            int callbackIndex = node.get("callback_index").asInt();
            List<Integer> argsList = new ArrayList<>();
            node.get("args").elements().forEachRemaining(a -> argsList.add(a.asInt()));
            return new Component(id, beginX, beginY, endX, endY, callbackIndex, argsList);
        }
    }
}
