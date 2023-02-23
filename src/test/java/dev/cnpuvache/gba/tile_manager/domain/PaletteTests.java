package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PaletteTests {

    private Palette palette;
    private RGB15 color1, color2, color3;

    @BeforeEach
    public void setup() {
        color1 = new RGB15(1, 2, 3);
        color2 = new RGB15(4, 5, 6);
        color3 = new RGB15(7, 8, 9);

        palette = new Palette();
        palette.setColor(1, 2, color1);
        palette.setColor(3, 4, color2);
        palette.setColor(5, 6, color3);
    }

    @Test
    @DisplayName("Initialization sets all colors to black.")
    public void initializationSetAll256ColorsToBlack() {
        Palette palette = new Palette();
        try {
            for (int i = 0; i < 256; i++) {
                assertEquals(RGB15.DEFAULT, palette.getColor(i));
            }
        } catch (IndexOutOfBoundsException ex) {
            fail("There are not 256 colors in the palette.");
        }
    }

     @Test
     @DisplayName("Set color sets the color at the expected index.")
     public void setColorSetsTheColorAtTheExpectedIndex() {
         Palette palette = new Palette();
         RGB15 red = new RGB15(31, 0, 0);
         palette.setColor(4, 9, red);
         try {
             for (int i = 0; i < 256; i++) {
                 if (i == 4 * 16 + 9) {
                     assertEquals(red, palette.getColor(i));
                 } else {
                     assertEquals(RGB15.DEFAULT, palette.getColor(i));
                 }
             }
         } catch (IndexOutOfBoundsException ex) {
             fail("There are not 256 colors in the palette.");
         }
     }

    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(RGB15.class, new RGB15.Serializer());
        module.addDeserializer(RGB15.class, new RGB15.Deserializer());
        module.addSerializer(Palette.class, new Palette.Serializer());
        module.addDeserializer(Palette.class, new Palette.Deserializer());
        mapper.registerModule(module);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(palette);
        System.out.println(json);
        Palette parsedPalette16 = mapper.readValue(json, Palette.class);
        assertEquals(palette, parsedPalette16);
    }

}
