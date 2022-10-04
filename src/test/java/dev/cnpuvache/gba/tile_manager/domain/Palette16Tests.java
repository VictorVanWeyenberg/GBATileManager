package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Palette16Tests {

    private Palette16 palette16;
    private RGB15 color1, color2, color3;

    @BeforeEach
    public void setup() {
        color1 = new RGB15(1, 2, 3);
        color2 = new RGB15(4, 5, 6);
        color3 = new RGB15(7, 8, 9);

        palette16 = new Palette16("TEST");
        palette16.setColor(1, 2, color1);
        palette16.setColor(3, 4, color2);
        palette16.setColor(5, 6, color3);
    }

    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(RGB15.class, new RGB15.Serializer());
        module.addDeserializer(RGB15.class, new RGB15.Deserializer());
        module.addSerializer(Palette16.class, new Palette16.Serializer());
        module.addDeserializer(Palette16.class, new Palette16.Deserializer());
        mapper.registerModule(module);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(palette16);
        System.out.println(json);
        Palette16 parsedPalette16 = mapper.readValue(json, Palette16.class);
        assertEquals(palette16, parsedPalette16);
    }

}
