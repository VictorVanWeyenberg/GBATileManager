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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Reznov
 */
public class Palette {
    
    private final RGB15[] paletteData;
    private static final int PALETTE_SIZE = 16;
    private static final int NUMBER_OF_COLORS = 16 * 16;

    @JsonCreator
    private Palette(@JsonProperty("paletteData") RGB15[] paletteData) {
        if (paletteData.length != NUMBER_OF_COLORS) {
            throw new IllegalArgumentException("Palette data size must be 256.");
        }
        this.paletteData = paletteData;
    }

    public Palette() {
        paletteData = new RGB15[NUMBER_OF_COLORS];
        for (int colorIndex = 0; colorIndex < NUMBER_OF_COLORS; colorIndex++) {
            paletteData[colorIndex] = RGB15.DEFAULT;
        }
    }

    public RGB15[] getPalette(int index) {
        RGB15[] palette = new RGB15[16];
        System.arraycopy(paletteData, index * PALETTE_SIZE, palette, 0, PALETTE_SIZE);
        return palette;
    }

    public void setColor(int palette, int index, RGB15 color) {
        paletteData[palette * PALETTE_SIZE + index] = color;
    }

    public RGB15 getColor(int colorNumber) {
        return paletteData[colorNumber];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Palette palette16 = (Palette) o;
        return Arrays.equals(paletteData, palette16.paletteData);
    }

    public static final class Serializer extends StdSerializer<Palette> {

        public Serializer() {
            super(Palette.class);
        }

        @Override
        public void serialize(Palette palette16, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeArrayFieldStart("paletteData");
            for (RGB15 color : palette16.paletteData) {
                jsonGenerator.writeObject(color);
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<Palette> {

        public Deserializer() {
            super(Palette.class);
        }

        @Override
        public Palette deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            ObjectMapper mapper = new ObjectMapper();
            Iterator<JsonNode> paletteDataIterator = node.get("paletteData").elements();
            final List<RGB15> paletteDataList = new ArrayList<>();
            paletteDataIterator.forEachRemaining(pdn -> {
                try {
                    paletteDataList.add(mapper.treeToValue(pdn, RGB15.class));
                } catch (JsonProcessingException e) {
                    System.out.println("Could not deserialize RGB15 from json. " + pdn.toString());
                    paletteDataList.add(RGB15.DEFAULT);
                }
            });
            while (paletteDataList.size() > NUMBER_OF_COLORS) {
                paletteDataList.remove(paletteDataList.size() - 1);
            }
            while (paletteDataList.size() < NUMBER_OF_COLORS) {
                paletteDataList.add(RGB15.DEFAULT);
            }
            RGB15[] paletteData = new RGB15[NUMBER_OF_COLORS];
            paletteData = paletteDataList.toArray(paletteData);
            return new Palette(paletteData);
        }
    }
    
}
