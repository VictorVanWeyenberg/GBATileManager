package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ObjectAttributeTests {

    private ObjectAttributes objectAttributes;

    @BeforeEach
    public void setup() {
        objectAttributes = new ObjectAttributes(10, 20, 300, 4, 2);
    }

    static Stream<Arguments> rightObjectAttributesParameters() {
        return Stream.of(
                Arguments.of(0, 0, 0, 0, 0),
                Arguments.of(63, 0, 0, 0, 0),
                Arguments.of(0, 0, 0, 0, 0),
                Arguments.of(0, 63, 0, 0, 0),
                Arguments.of(0, 0, 0, 0, 0),
                Arguments.of(0, 0, 1023, 0, 0),
                Arguments.of(0, 0, 0, 0, 0),
                Arguments.of(0, 0, 0, 15, 0),
                Arguments.of(0, 0, 0, 0, 0),
                Arguments.of(0, 0, 0, 0, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("rightObjectAttributesParameters")
    @DisplayName("Right parameters allow the construction of object attributes.")
    public void rightParametersAllowTheConstructionOfObjectAttributes(int x, int y, int tileNumber, int paletteNumber, int priority) {
        try {
            new ObjectAttributes(x, y, tileNumber, paletteNumber, priority);
        } catch (Exception ex) {
            fail(String.format("Unexpectedly caught exception when creating object attributes with parameter set (%d, %d, %d, %d, d)",
                    x, y, tileNumber, paletteNumber, priority));
        }
    }

    static Stream<Arguments> wrongObjectAttributesParameters() {
        return Stream.of(
                Arguments.of(-1, 0, 0, 0, 0),
                Arguments.of(64, 0, 0, 0, 0),
                Arguments.of(0, -1, 0, 0, 0),
                Arguments.of(0, 64, 0, 0, 0),
                Arguments.of(0, 0, -1, 0, 0),
                Arguments.of(0, 0, 1024, 0, 0),
                Arguments.of(0, 0, 0, -1, 0),
                Arguments.of(0, 0, 0, 16, 0),
                Arguments.of(0, 0, 0, 0, -1),
                Arguments.of(0, 0, 0, 0, 4)
        );
    }

    @ParameterizedTest
    @MethodSource("wrongObjectAttributesParameters")
    @DisplayName("Wrong parameters do not allow to create an object.")
    public void wrongParametersDoNotAllowToCreateAnObject(int x, int y, int tileNumber, int paletteNumber, int priority) {
        try {
            new ObjectAttributes(x, y, tileNumber, paletteNumber, priority);
            fail(String.format("Construction of object attributes with parameter set (%d, %d, %d, %d, %d) unexpectedly succeeded.",
                    x, y, tileNumber, paletteNumber, priority));
        } catch (IllegalArgumentException ex) {
            System.out.println("Expected IllegalArgumentException.");
        } catch (Exception ex) {
            fail("Unexpectedly caught another exception.");
            ex.printStackTrace();
        }
    }

    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(ObjectAttributes.class, new ObjectAttributes.Serializer());
        module.addDeserializer(ObjectAttributes.class, new ObjectAttributes.Deserializer());
        mapper.registerModule(module);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectAttributes);
        System.out.println(json);
        ObjectAttributes parsedAttributes = mapper.readValue(json, ObjectAttributes.class);
        assertEquals(objectAttributes, parsedAttributes);
    }

}
