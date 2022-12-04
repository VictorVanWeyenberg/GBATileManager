package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScreenDataTests {

    private ScreenData screenData;

    @BeforeEach
    public void setup() {
        ScreenEntry screenEntry1 = new ScreenEntry(49, false, false, 12);
        ScreenEntry screenEntry2 = new ScreenEntry(86, false, true, 0);
        ScreenEntry screenEntry3 = new ScreenEntry(61, true, false, 1);
        ScreenEntry screenEntry4 = new ScreenEntry(24, true, true, 2);
        screenData = new ScreenData(0);
        screenData.setEntry(4, 7, screenEntry1);
        screenData.setEntry(4, 8, screenEntry2);
        screenData.setEntry(9, 7, screenEntry3);
        screenData.setEntry(4, 15, screenEntry4);
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

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(screenData);
        System.out.println(json);

        ScreenData parsedScreenData = mapper.readValue(json, ScreenData.class);
        assertEquals(screenData, parsedScreenData);
    }

}
