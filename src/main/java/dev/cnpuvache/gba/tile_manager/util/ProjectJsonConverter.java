package dev.cnpuvache.gba.tile_manager.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.cnpuvache.gba.tile_manager.domain.*;

import java.nio.charset.StandardCharsets;

public class ProjectJsonConverter {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        SimpleModule module = new SimpleModule();
        module.addSerializer(ScreenEntry.class, new ScreenEntry.Serializer());
        module.addDeserializer(ScreenEntry.class, new ScreenEntry.Deserializer());
        module.addSerializer(ScreenData.class, new ScreenData.Serializer());
        module.addDeserializer(ScreenData.class, new ScreenData.Deserializer());
        module.addSerializer(Screen.class, new Screen.Serializer());
        module.addDeserializer(Screen.class, new Screen.Deserializer());
        module.addSerializer(RGB15.class, new RGB15.Serializer());
        module.addDeserializer(RGB15.class, new RGB15.Deserializer());
        module.addSerializer(Palette.class, new Palette.Serializer());
        module.addDeserializer(Palette.class, new Palette.Deserializer());
        module.addSerializer(Tile.class, new Tile.Serializer());
        module.addDeserializer(Tile.class, new Tile.Deserializer());
        module.addSerializer(CharacterData.class, new CharacterData.Serializer());
        module.addDeserializer(CharacterData.class, new CharacterData.Deserializer());
        module.addSerializer(ObjectAttributes.class, new ObjectAttributes.Serializer());
        module.addDeserializer(ObjectAttributes.class, new ObjectAttributes.Deserializer());
        module.addSerializer(Project.class, new Project.Serializer());
        module.addDeserializer(Project.class, new Project.Deserializer());
        mapper.registerModule(module);
    }

    public static byte[] toJson(Project project) throws JsonProcessingException {
        return mapper.writeValueAsString(project).getBytes(StandardCharsets.UTF_8);
    }

    public static Project fromJson(byte[] json) throws JsonProcessingException {
        return mapper.readValue(new String(json), Project.class);
    }

}
