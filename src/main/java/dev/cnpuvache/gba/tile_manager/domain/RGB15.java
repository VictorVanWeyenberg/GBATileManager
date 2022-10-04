/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.domain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;

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
import javafx.scene.paint.Color;

/**
 *
 * @author Reznov
 */
public class RGB15 {
    
    private int r, g, b;
    @JsonIgnore
    private transient Color color;

    @JsonCreator
    public RGB15(
            @JsonProperty("r") int r,
            @JsonProperty("g") int g,
            @JsonProperty("b") int b) {
        setColor(r, g, b);
    }
    
    public Color getColor() {
        return color;
    }
    
    public int getR() {
        return r;
    }
    
    public int getG() {
        return g;
    }
    
    public int getB() {
        return b;
    }

    private void setColor() {
        double r = this.r / 31.0;
        double g = this.g / 31.0;
        double b = this.b / 31.0;
        this.color = new Color(r, g, b, 1);
    }
    
    public void setColor(int r, int g, int b) {
        setR(r);
        setG(g);
        setB(b);
        setColor();
    }
    
    private void setR(int r) {
        if (r < 0 || r > 31) {
            rangeException("red");
        } else {
            this.r = r;
        }
    }
    
    private void setG(int g) {
        if (g < 0 || g > 31) {
            rangeException("green");
        } else {
            this.g = g;
        }
    }
    
    private void setB(int b) {
        if (b < 0 || b > 31) {
            rangeException("blue");
        } else {
            this.b = b;
        }
    }
    
    private void rangeException(String component) {
        throw new IllegalArgumentException(String.format("The value of the %s component must be in range of (0, 31).", component));
    }
    
    @Override
    public String toString() {
        return String.format("RGB15(%d, %d, %d)", r, g, b);
    }
    
    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject();
        setColor();
    }

    public String toC() {
        int color = (b << 10) + (g << 5) + r;
        String hexString = String.format("0x%04x", color);
        return hexString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RGB15 rgb15 = (RGB15) o;
        return r == rgb15.r && g == rgb15.g && b == rgb15.b;
    }

    public static final class Serializer extends StdSerializer<RGB15> {

        public Serializer() {
            super(RGB15.class);
        }

        @Override
        public void serialize(RGB15 rgb15, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("r", rgb15.r);
            jsonGenerator.writeNumberField("g", rgb15.g);
            jsonGenerator.writeNumberField("b", rgb15.b);
            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<RGB15> {

        public Deserializer() {
            super(RGB15.class);
        }

        @Override
        public RGB15 deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            int r = node.get("r").asInt();
            int g = node.get("g").asInt();
            int b = node.get("b").asInt();
            return new RGB15(r, g, b);
        }
    }
    
}
