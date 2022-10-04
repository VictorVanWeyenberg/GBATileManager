package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScreenEntryTests {

    private ScreenEntry screenEntry;

    @BeforeEach
    public void setup() {
        screenEntry = new ScreenEntry(49, false, true, 12);
    }

    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ScreenEntry.class, new ScreenEntry.Serializer());
        module.addDeserializer(ScreenEntry.class, new ScreenEntry.Deserializer());
        mapper.registerModule(module);

        String json = mapper.writeValueAsString(screenEntry);
        System.out.println(json);

        ScreenEntry parsedScreenEntry = mapper.readValue(json, ScreenEntry.class);

        assertEquals(screenEntry, parsedScreenEntry);
    }

}
