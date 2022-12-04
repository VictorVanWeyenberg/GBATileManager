package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BackgroundMapTests {

    private BackgroundMap backgroundMap;

    @BeforeEach
    public void setup() {
        backgroundMap = new BackgroundMap(true, true, false, false, 0, 1, 2, 3);
        backgroundMap.setScreenData(0, 1, 2, 3, 4);
        backgroundMap.setScreenData(1, 2, 3, 4, 5);
        backgroundMap.setScreenData(0, 0, 0, 0);
        backgroundMap.setScreenData(1, 1, 1, 1);
    }

    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(BackgroundMap.class, new BackgroundMap.Serializer());
        module.addDeserializer(BackgroundMap.class, new BackgroundMap.Deserializer());
        module.addSerializer(ScreenData.class, new ScreenData.Serializer());
        module.addDeserializer(ScreenData.class, new ScreenData.Deserializer());
        module.addSerializer(ScreenEntry.class, new ScreenEntry.Serializer());
        module.addDeserializer(ScreenEntry.class, new ScreenEntry.Deserializer());
        mapper.registerModule(module);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(backgroundMap);
        System.out.println(json);
        System.out.println();
        BackgroundMap parsedBackgroundMap = mapper.readValue(json, BackgroundMap.class);
        json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedBackgroundMap);
        System.out.println(json);
        assertEquals(backgroundMap, parsedBackgroundMap);
    }

}
