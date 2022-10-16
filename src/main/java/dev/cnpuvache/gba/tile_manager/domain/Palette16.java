/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Reznov
 */
public class Palette16 extends Palette {
    
    private final List<List<RGB15>> paletteData;
    private static final int PALETTE_SIZE = 16;

    public Palette16(String name) {
        super(name);
        paletteData = new ArrayList<List<RGB15>>();
        for (int i = 0; i < PALETTE_SIZE; i++) {
            paletteData.add(new ArrayList<RGB15>());
            for (int j = 0; j < PALETTE_SIZE; j++) {
                paletteData.get(i).add(new RGB15(0, 0, 0));
            }
        }
    }

    @JsonCreator
    private Palette16(
            @JsonProperty("name") String name,
            @JsonProperty("paletteData") List<List<RGB15>> paletteData) {
        super(name);
        this.paletteData = paletteData;
    }

    public List<RGB15> getPalette(int index) {
        return this.paletteData.get(index);
    }
    
    public void setColor(int palette, int index, RGB15 color) {
        paletteData.get(palette).set(index, color);
    }

    @Override
    public ByteBuffer toC() {
        ByteBuffer buffer = ByteBuffer.allocate(PALETTE_SIZE * PALETTE_SIZE * 2);
        for (int y = 0; y < paletteData.size(); y++) {
            List<RGB15> palette = paletteData.get(y);
            for (int x = 0; x < palette.size(); x++) {
                RGB15 color = palette.get(x);
                buffer.put(color.toC().array());
            }
        }
        return buffer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Palette16 palette16 = (Palette16) o;
        return name.equals(palette16.name) && paletteData.equals(palette16.paletteData);
    }

    public static final class Serializer extends StdSerializer<Palette16> {

        public Serializer() {
            super(Palette16.class);
        }

        @Override
        public void serialize(Palette16 palette16, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("name", palette16.name);
            jsonGenerator.writeBooleanField("default", palette16.isDefault());
            jsonGenerator.writeFieldName("paletteData");
            jsonGenerator.writeStartArray();
            for (List<RGB15> data : palette16.paletteData) {
                jsonGenerator.writeStartArray();
                for(RGB15 color : data) {
                    jsonGenerator.writeObject(color);
                }
                jsonGenerator.writeEndArray();
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<Palette16> {

        public Deserializer() {
            super(Palette16.class);
        }

        @Override
        public Palette16 deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String name = node.get("name").asText();
            Palette16 palette16 = new Palette16(name);
            JsonNode isDefaultNode = node.get("default");
            boolean isDefault = isDefaultNode != null && isDefaultNode.asBoolean();
            palette16.setDefault(isDefault);
            Iterator<JsonNode> paletteDataNode = node.get("paletteData").elements();
            ObjectMapper mapper = new ObjectMapper();
            int paletteIndex = 0;
            while (paletteDataNode.hasNext()) {
                Iterator<JsonNode> paletteNode = paletteDataNode.next().elements();
                int colorIndex = 0;
                while (paletteNode.hasNext()) {
                    JsonNode colorNode = paletteNode.next();
                    RGB15 color = mapper.treeToValue(colorNode, RGB15.class);
                    palette16.setColor(paletteIndex, colorIndex++, color);
                }
                paletteIndex++;
            }
            return palette16;
        }
    }
    
}
