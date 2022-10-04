package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RGB15Tests {

    private RGB15 rgb15;

    @BeforeEach
    public void setup() {
        rgb15 = new RGB15(1, 2, 3);
    }

    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(RGB15.class, new RGB15.Serializer());
        module.addDeserializer(RGB15.class, new RGB15.Deserializer());
        mapper.registerModule(module);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rgb15);
        System.out.println(json);
        RGB15 parsedRGB15 = mapper.readValue(json, RGB15.class);
        assertEquals(rgb15, parsedRGB15);
    }

}
