package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScreenTests {

    private Screen screen;

    @BeforeEach
    public void setup() {
        ScreenEntry screenEntry1 = new ScreenEntry(49, false, false, 12);
        ScreenEntry screenEntry2 = new ScreenEntry(86, false, true, 0);
        ScreenEntry screenEntry3 = new ScreenEntry(61, true, false, 1);
        ScreenEntry screenEntry4 = new ScreenEntry(24, true, true, 2);
        screen = new Screen("screen", new int[] {3, 2, 1, 0});
        screen.setEntry(0, 4,7, screenEntry1);
        screen.setEntry(1, 4,8, screenEntry2);
        screen.setEntry(2, 9,7, screenEntry3);
        screen.setEntry(3, 4,15, screenEntry4);
    }

    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ScreenEntry.class, new ScreenEntry.Serializer());
        module.addDeserializer(ScreenEntry.class, new ScreenEntry.Deserializer());
        module.addSerializer(ScreenData.class, new ScreenData.Serializer());
        module.addDeserializer(ScreenData.class, new ScreenData.Deserializer());
        module.addSerializer(Screen.class, new Screen.Serializer());
        module.addDeserializer(Screen.class, new Screen.Deserializer());
        mapper.registerModule(module);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(screen);
        System.out.println(json);

        Screen parsedScreen = mapper.readValue(json, Screen.class);
        assertEquals(screen, parsedScreen);
    }

}
