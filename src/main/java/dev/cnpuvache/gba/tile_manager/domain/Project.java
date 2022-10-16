/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 *
 * @author Reznov
 */
public class Project {

    public enum OBJMapping {
        ONE_DIMENSIONAL,
        TWO_DIMENSIONAL
    }
    
    public enum PaletteType {
        PALETTE16,
        PALETTE256
    }
    
    private final String name;
    private final OBJMapping objMapping;
    private final PaletteType paletteType;
    
    private final int BG_MODE = 0;
    private final boolean displayOBJ;
    private final boolean displayBG0;
    private final boolean displayBG1;
    private final boolean displayBG2;
    private final boolean displayBG3;
    
    private final Map<String, Palette> objectPalettes;
    private final Map<String, Palette> backgroundPalettes;

    private final List<Background> backgrounds;

    private final CharacterData objectCharacterData;

    @JsonCreator
    public Project(
            @JsonProperty("name") String name,
            @JsonProperty("objMapping") OBJMapping objMapping,
            @JsonProperty("paletteType") PaletteType paletteType,
            @JsonProperty("backgrounds") List<Background> backgrounds,
            @JsonProperty("objectPalettes") Map<String, Palette> objectPalettes,
            @JsonProperty("backgroundPalettes") Map<String, Palette> backgroundPalettes,
            @JsonProperty("objectCharacterData") CharacterData objectCharacterData,
            @JsonProperty("displayOBJ") boolean displayOBJ,
            @JsonProperty("displayBG0") boolean displayBG0,
            @JsonProperty("displayBG1") boolean displayBG1,
            @JsonProperty("displayBG2") boolean displayBG2,
            @JsonProperty("displayBG3") boolean displayBG3) {
        this.name = name;
        this.objMapping = objMapping;
        this.paletteType = paletteType;
        this.backgrounds = backgrounds;
        this.objectPalettes = objectPalettes;
        this.backgroundPalettes = backgroundPalettes;
        if (objectCharacterData == null) {
            objectCharacterData = new CharacterData(paletteType == PaletteType.PALETTE256);
        }
        this.objectCharacterData = objectCharacterData;
        this.displayOBJ = displayOBJ;
        this.displayBG0 = displayBG0;
        this.displayBG1 = displayBG1;
        this.displayBG2 = displayBG2;
        this.displayBG3 = displayBG3;
    }
    
    public Project(String name, OBJMapping objMapping, PaletteType paletteType, boolean displayOBJ, boolean displayBG0, boolean displayBG1, boolean displayBG2, boolean displayBG3) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Project name is required.");
        } else if (name.length() < 4) {
            throw new IllegalArgumentException("Project name must at least be 4 characters long.");
        }
        this.name = name;
        this.objMapping = objMapping;
        this.paletteType = paletteType;
        this.displayOBJ = displayOBJ;
        this.displayBG0 = displayBG0;
        this.displayBG1 = displayBG1;
        this.displayBG2 = displayBG2;
        this.displayBG3 = displayBG3;
        
        objectPalettes = new TreeMap<>();
        backgroundPalettes = new TreeMap<>();
        switch (paletteType) {
            case PALETTE16:
                objectPalettes.put("Default Palette", new Palette16("default_obj_palette"));
                backgroundPalettes.put("Default Palette", new Palette16("default_bg_palette"));
                break;
        }

        final boolean colorNotPalettes = paletteType == PaletteType.PALETTE256;
        this.backgrounds = new ArrayList<>();
        boolean[] displayeds = new boolean[] { displayBG0, displayBG1, displayBG2, displayBG3 };
        for (int index = 0; index < displayeds.length; index++) {
            boolean displayed = displayeds[index];
            if (!displayed) {
                this.backgrounds.add(null);
                continue;
            }

            CharacterData characterData;
            if (index == 1) {
                try (InputStream is = getClass().getResourceAsStream("/alphabet.c")) {
                    byte[] buf = new byte[1024];
                    StringBuilder data = new StringBuilder();
                    while (is.read(buf) > 0) {
                        data.append(new String(buf));
                    }
                    characterData = CharacterData.fromC(data.toString(), colorNotPalettes);
                } catch (IOException e) {
                    System.out.println("!!! COULD NOT GENERATE CHARACTER DATA FROM C !!!");
                    characterData = new CharacterData(colorNotPalettes);
                }
            } else {
                characterData = new CharacterData(colorNotPalettes);
            }
            backgrounds.add(new Background.Builder(index, 0, colorNotPalettes, 16 + index * 2, 0)
                    .setCharacterData(characterData).build());
        }

        this.objectCharacterData = new CharacterData(colorNotPalettes);
    }
    
    public String getName() {
        return name;
    }

    public void addObjectPalette(String paletteName, Palette16 palette16, boolean makeDefault) {
        objectPalettes.put(paletteName, palette16);
        if (makeDefault) {
            setDefaultObjectPalette(paletteName);
        }
    }

    public void deleteObjectPalette(String paletteName) {
        objectPalettes.remove(paletteName);
    }

    public Map<String, Palette> getObjectPalettes() {
        return objectPalettes;
    }
    
    public Palette getObjectPalette(String paletteName) {
        return objectPalettes.get(paletteName);
    }

    public void addBackgroundPalette(String name, Palette palette, boolean makeDefault) {
        backgroundPalettes.put(name, palette);
        if (makeDefault) {
            setDefaultBackgroundPalette(name);
        }
    }

    public void deleteBackgroundPalette(String paletteName) {
        backgroundPalettes.remove(paletteName);
    }
    
    public Map<String, Palette> getBackgroundPalettes() {
        return backgroundPalettes;
    }
    
    public Palette getBackgroundPalette(String paletteName) {
        return backgroundPalettes.get(paletteName);
    }

    public Palette getDefaultBackgroundPalette() {
        return backgroundPalettes.values().stream()
                .filter(Palette::isDefault)
                .findFirst()
                .orElse(null);
    }

    public boolean setDefaultBackgroundPalette(String paletteName) {
        if (!backgroundPalettes.containsKey(paletteName)) return false;
        backgroundPalettes.values().stream()
                .forEach(p -> p.setDefault(false));
        backgroundPalettes.get(paletteName).setDefault(true);
        return true;
    }

    public boolean setDefaultObjectPalette(String paletteName) {
        if (!objectPalettes.containsKey(paletteName)) return false;
        backgroundPalettes.values().stream()
                .forEach(p -> p.setDefault(false));
        backgroundPalettes.get(paletteName).setDefault(true);
        return true;
    }

    public void verifyBackgroundNumber(int backgroundNumber) {
        if (backgroundNumber < 0 || backgroundNumber > 3) {
            throw new IllegalArgumentException("Background number out of bounds [0:3].");
        }
        if (backgroundNumber == 0 && !displayBG0) {
            throw new IllegalArgumentException("Background 0 is not displayed.");
        }
        if (backgroundNumber == 1 && !displayBG1) {
            throw new IllegalArgumentException("Background 1 is not displayed.");
        }
        if (backgroundNumber == 2 && !displayBG2) {
            throw new IllegalArgumentException("Background 2 is not displayed.");
        }
        if (backgroundNumber == 3 && !displayBG3) {
            throw new IllegalArgumentException("Background 3 is not displayed.");
        }
    }

    public List<Tile> getBackgroundTiles(int backgroundNumber) {
        verifyBackgroundNumber(backgroundNumber);
        return backgrounds.get(backgroundNumber).getTiles();
    }

    public Tile getBackgroundTile(int backgroundNumber, String name) {
        if (name == null) {
            throw new IllegalArgumentException("Tile name is undefined.");
        }
        verifyBackgroundNumber(backgroundNumber);
        Tile tile = backgrounds.get(backgroundNumber).getTiles().stream()
                .filter(t -> name.equals(t.getName()))
                .findFirst().orElse(null);
        if (tile == null) {
            throw new IllegalArgumentException(String.format("Could not find tile with name %s.", name));
        }
        return tile;
    }

    public List<Tile> getObjectTiles() {
        return objectCharacterData.getTiles();
    }
    
    @Override
    public String toString() {
        Iterator<String> titles = Arrays.asList(new String[] {"Project", "OBJ Mapping", "Palette Type", "Display OBJ", "Display BG0", "Display BG1", "Display BG2", "Display BG3"}).iterator();
        Iterator<String> values = Arrays.asList(new String[] {this.name, this.objMapping.toString(), this.paletteType.toString(), Boolean.toString(displayOBJ), Boolean.toString(displayBG0), Boolean.toString(displayBG1), Boolean.toString(displayBG2), Boolean.toString(displayBG3)}).iterator();
        StringBuilder sb = new StringBuilder();
        while(titles.hasNext() && values.hasNext()) {
            sb.append(String.format("%-20s:%20s%n", titles.next(), values.next()));
        }
        return sb.toString();
    }

    public OBJMapping getObjMapping() {
        return objMapping;
    }

    public PaletteType getPaletteType() {
        return paletteType;
    }

    public boolean isDisplayOBJ() {
        return displayOBJ;
    }

    public boolean isDisplayBG0() {
        return displayBG0;
    }

    public boolean isDisplayBG1() {
        return displayBG1;
    }

    public boolean isDisplayBG2() {
        return displayBG2;
    }

    public boolean isDisplayBG3() {
        return displayBG3;
    }

    public int getBgMode() {
        return BG_MODE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return BG_MODE == project.BG_MODE &&
                displayOBJ == project.displayOBJ &&
                displayBG0 == project.displayBG0 &&
                displayBG1 == project.displayBG1 &&
                displayBG2 == project.displayBG2 &&
                displayBG3 == project.displayBG3 &&
                name.equals(project.name) &&
                objMapping == project.objMapping &&
                paletteType == project.paletteType &&
                objectPalettes.equals(project.objectPalettes) &&
                backgroundPalettes.equals(project.backgroundPalettes) &&
                backgrounds.equals(project.backgrounds);
    }

    public static final class Builder {

        private final String name;
        private final OBJMapping objMapping;
        private final PaletteType paletteType;

        public Builder(String name, OBJMapping objMapping, PaletteType paletteType) {
            this.name = name;
            this.objMapping = objMapping;
            this.paletteType = paletteType;
        }

    }

    public static final class Serializer extends StdSerializer<Project> {

        public Serializer() {
            super(Project.class);
        }

        @Override
        public void serialize(Project project, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("name", project.name);
            jsonGenerator.writeNumberField("bgMode", project.BG_MODE);
            jsonGenerator.writeBooleanField("displayOBJ", project.displayOBJ);
            jsonGenerator.writeBooleanField("displayBG0", project.displayBG0);
            jsonGenerator.writeBooleanField("displayBG1", project.displayBG1);
            jsonGenerator.writeBooleanField("displayBG2", project.displayBG2);
            jsonGenerator.writeBooleanField("displayBG3", project.displayBG3);
            jsonGenerator.writeStringField("objMapping", project.objMapping.name());
            jsonGenerator.writeStringField("paletteType", project.paletteType.name());

            jsonGenerator.writeFieldName("objectPalettes");
            jsonGenerator.writeStartArray();
            for (Map.Entry<String, Palette> entry : project.objectPalettes.entrySet()) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("name", entry.getKey());
                jsonGenerator.writeFieldName("palette");
                jsonGenerator.writeObject(entry.getValue());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeFieldName("backgroundPalettes");
            jsonGenerator.writeStartArray();
            for (Map.Entry<String, Palette> entry : project.backgroundPalettes.entrySet()) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("name", entry.getKey());
                jsonGenerator.writeFieldName("palette");
                jsonGenerator.writeObject(entry.getValue());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeObjectField("objectCharacterData", project.objectCharacterData);

            jsonGenerator.writeFieldName("backgrounds");
            jsonGenerator.writeStartArray();
            for (Background background : project.backgrounds) {
                jsonGenerator.writeObject(background);
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<Project> {

        public Deserializer() {
            super(Project.class);
        }

        @Override
        public Project deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String name = node.get("name").asText();
            int bgMode = node.get("bgMode").asInt();
            boolean displayOBJ = node.get("displayOBJ").asBoolean();
            boolean displayBG0 = node.get("displayBG0").asBoolean();
            boolean displayBG1 = node.get("displayBG1").asBoolean();
            boolean displayBG2 = node.get("displayBG2").asBoolean();
            boolean displayBG3 = node.get("displayBG3").asBoolean();
            OBJMapping objMapping = OBJMapping.valueOf(node.get("objMapping").asText());
            PaletteType paletteType = PaletteType.valueOf(node.get("paletteType").asText());

            ObjectMapper mapper = new ObjectMapper();

            Map<String, Palette> objectPalettes = new TreeMap<>();
            for (JsonNode entryNode : node.get("objectPalettes")) {
                String paletteName = entryNode.get("name").asText();
                Palette16 palette16 = mapper.treeToValue(entryNode.get("palette"), Palette16.class);
                objectPalettes.put(paletteName, palette16);
            }

            Map<String, Palette> backgroundPalettes = new TreeMap<>();
            for (JsonNode entryNode : node.get("backgroundPalettes")) {
                String paletteName = entryNode.get("name").asText();
                Palette16 palette16 = mapper.treeToValue(entryNode.get("palette"), Palette16.class);
                backgroundPalettes.put(paletteName, palette16);
            }

            CharacterData objectCharacterData = null;
            JsonNode objectCharacterDataNode = node.get("objectCharacterData");
            if (objectCharacterDataNode != null) {
                objectCharacterData = mapper.treeToValue(objectCharacterDataNode, CharacterData.class);
            }

            List<Background> backgrounds = new ArrayList<>();
            for (JsonNode backgroundNode : node.get("backgrounds")) {
                Background background = mapper.treeToValue(backgroundNode, Background.class);
                backgrounds.add(background);
            }

            return new Project(name, objMapping, paletteType,
                    backgrounds, objectPalettes, backgroundPalettes, objectCharacterData,
                    displayOBJ, displayBG0, displayBG1, displayBG2, displayBG3);
        }
    }
    
}
