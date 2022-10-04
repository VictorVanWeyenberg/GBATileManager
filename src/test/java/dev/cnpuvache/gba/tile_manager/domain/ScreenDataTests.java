package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScreenDataTests {

    private ScreenData screenData;

    private ScreenEntry screenEntry;

    @BeforeEach
    public void setup() {
        screenEntry = new ScreenEntry(49, false, true, 12);
        screenData = new ScreenData(0, 0);
        screenData.setEntry(4, 7, screenEntry);
    }

    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ScreenEntry.class, new ScreenEntry.Serializer());
        module.addDeserializer(ScreenEntry.class, new ScreenEntry.Deserializer());
        module.addSerializer(ScreenData.class, new ScreenData.Serializer());
        module.addDeserializer(ScreenData.class, new ScreenData.Deserializer());
        mapper.registerModule(module);

        String json = mapper.writeValueAsString(screenData);
        System.out.println(json);

        ScreenData parsedScreenData = mapper.readValue(json, ScreenData.class);
        assertEquals(screenData, parsedScreenData);
    }

}
