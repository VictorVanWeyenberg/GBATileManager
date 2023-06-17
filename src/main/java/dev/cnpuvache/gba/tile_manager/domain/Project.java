package dev.cnpuvache.gba.tile_manager.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Project {

    private final String name;
    private final int[] screenSizes;
    private final List<Screen> screens;
    private final List<CharacterData> characterBlocks;
    private final List<Palette> palettes;
    private final TreeMap<String, ObjectAttributes> objects;

    public Project(String name, int[] screenSizes) {
        this.name = name;
        this.screenSizes = screenSizes;
        this.screens = new ArrayList<>();

        this.characterBlocks = new ArrayList<>();
        for (int characterBlockIndex = 0; characterBlockIndex < 5; characterBlockIndex++) {
            characterBlocks.add(new CharacterData(null));
        }

        this.palettes = new ArrayList<>();
        for (int paletteIndex = 0; paletteIndex < 2; paletteIndex++) {
            palettes.add(new Palette());
        }

        this.objects = new TreeMap<>();
    }

    @JsonCreator
    Project(
            @JsonProperty("name") String name,
            @JsonProperty("screenSizes") int[] screenSizes,
            @JsonProperty("screens") List<Screen> screens,
            @JsonProperty("characterBlocks") List<CharacterData> characterBlocks,
            @JsonProperty("palettes") List<Palette> palettes,
            @JsonProperty("objects") TreeMap<String, ObjectAttributes> objects
    ) {
        this.name = name;
        this.screenSizes = screenSizes;
        this.screens = screens;
        this.characterBlocks = characterBlocks;
        this.palettes = palettes;
        this.objects = objects;
    }

    public static Project defaultInstance() {
        return new Project("Default", new int[] { 0, 0, 0, 0 });
    }

    public String getName() {
        return name;
    }

    public void setObjectAttributes(String name, ObjectAttributes objectAttributes) {
        objects.put(name, objectAttributes);
    }

    public Palette getBackgroundPalette() {
        return palettes.get(0);
    }

    public Palette getObjectPalette() {
        return palettes.get(1);
    }

    public void setColor(boolean backgroundNotObject, int paletteNumber, int index, RGB15 color) {
        palettes.get(backgroundNotObject ? 0 : 1).setColor(paletteNumber, index, color);
    }

    public void setTile(int backgroundNumber, Tile tile) {
        if (backgroundNumber < 0 || backgroundNumber > 4) {
            throw new IllegalArgumentException("Background number must be [0:3] or 4 for object tiles.");
        }
        characterBlocks.get(backgroundNumber).setTile(tile);
    }

    public void removeTile(int backgroundNumber, String tileName) {
        if (backgroundNumber < 0 || backgroundNumber > 4) {
            throw new IllegalArgumentException("Background number must be [0:3] or 4 for object tiles.");
        }
        int tileIndex = characterBlocks.get(backgroundNumber).removeTile(tileName);
        for (Screen screen : screens) {
            screen.removeTile(backgroundNumber, tileIndex);
        }
    }

    public void moveTileUp(int characterBlockIndex, String tileName) {
        if (characterBlockIndex < 0 || characterBlockIndex > 4) {
            throw new IllegalArgumentException("Background number must be [0:3] or 4 for object tiles.");
        }
        int tileIndex = characterBlocks.get(characterBlockIndex).moveTileUp(tileName);
        if (tileIndex < 0) {
            return;
        }
        for (Screen screen : screens) {
            screen.moveTileUp(characterBlockIndex, tileIndex);
        }
    }

    public void moveTileDown(int characterBlockIndex, String tileName) {
        if (characterBlockIndex < 0 || characterBlockIndex > 4) {
            throw new IllegalArgumentException("Background number must be [0:3] or 4 for object tiles.");
        }
        int tileIndex = characterBlocks.get(characterBlockIndex).moveTileDown(tileName);
        if (tileIndex < 0) {
            return;
        }
        for (Screen screen : screens) {
            screen.moveTileDown(characterBlockIndex, tileIndex);
        }
    }

    public void createScreen(String screenName) {
        Screen screen = new Screen(screenName, screenSizes);
        screens.add(screen);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Arrays.equals(screenSizes, project.screenSizes) && screens.equals(project.screens) && characterBlocks.equals(project.characterBlocks) && palettes.equals(project.palettes) && objects.equals(project.objects);
    }

    public CharacterData getCharacterData(int backgroundNumber) {
        if (backgroundNumber < 0 || backgroundNumber > characterBlocks.size() - 1) {
            return null;
        }
        return characterBlocks.get(backgroundNumber);
    }

    public List<Tile> getTiles(int backgroundNumber) {
        return characterBlocks.get(backgroundNumber).tiles;
    }

    public void addTile(int backgroundNumber, String name) {
        characterBlocks.get(backgroundNumber).addTile(name);
    }

    public List<Screen> getScreens() {
        return screens;
    }

    public void removeScreen(Screen screen) {
        screens.remove(screen);
    }

    public void moveScreenDown(Screen screen) {
        int index = screens.indexOf(screen);
        if (index == screens.size() - 1 || index == -1) {
            return;
        }
        Collections.swap(screens, index, index + 1);
    }

    public void moveScreenUp(Screen screen) {
        int index = screens.indexOf(screen);
        if (index <= 0) {
            return;
        }
        Collections.swap(screens, index, index - 1);
    }

    public static final class Serializer extends StdSerializer<Project> {

        public Serializer() {
            super(Project.class);
        }

        @Override
        public void serialize(Project project, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();

            jsonGenerator.writeStringField("name", project.name);

            jsonGenerator.writeArrayFieldStart("screenSizes");
            for (Integer screenSize : project.screenSizes) {
                jsonGenerator.writeNumber(screenSize);
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeArrayFieldStart("screens");
            for (Screen screen : project.screens) {
                jsonGenerator.writeObject(screen);
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeArrayFieldStart("characterBlocks");
            for (CharacterData characterData : project.characterBlocks) {
                jsonGenerator.writeObject(characterData);
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeArrayFieldStart("palettes");
            for (Palette palette : project.palettes) {
                jsonGenerator.writeObject(palette);
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeArrayFieldStart("objects");
            for (Map.Entry<String, ObjectAttributes> objectsMapEntry : project.objects.entrySet()) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("name", objectsMapEntry.getKey());
                jsonGenerator.writeObjectField("obj_attrs", objectsMapEntry.getValue());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeEndObject();
        }
    }

    public static final class Deserializer extends StdDeserializer<Project>  {

        public Deserializer() {
            super(Project.class);
        }

        @Override
        public Project deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            ObjectMapper mapper = new ObjectMapper();

            JsonNode nameNode = node.get("name");
            if (nameNode == null) {
                throw new JsonMappingException("Field \"name\" is required for project.");
            }
            String projectName = nameNode.asText();

            List<Integer> screenSizesList = new ArrayList<>();
            node.get("screenSizes").elements().forEachRemaining(ssn -> screenSizesList.add(ssn.asInt()));
            while (screenSizesList.size() > 4) {
                screenSizesList.remove(screenSizesList.size() - 1);
            }
            while (screenSizesList.size() < 4) {
                screenSizesList.add(0);
            }
            int[] screenSizes = screenSizesList.stream().mapToInt(i -> i).toArray();

            List<Screen> screens = new ArrayList<>();
            Iterator<JsonNode> screenEntries = node.get("screens").elements();
            while (screenEntries.hasNext()) {
                JsonNode screenEntry = screenEntries.next();
                Screen screen = mapper.treeToValue(screenEntry, Screen.class);
                screens.add(screen);
            }

            List<CharacterData> characterBlocks = new ArrayList<>();
            node.get("characterBlocks").elements().forEachRemaining(cbn -> {
                try {
                    characterBlocks.add(mapper.treeToValue(cbn, CharacterData.class));
                } catch (JsonProcessingException e) {
                    System.out.println("Unable to deserialize CharacterData from json. " + cbn.toString());
                    e.printStackTrace();
                }
            });

            List<Palette> palettes = new ArrayList<>();
            node.get("palettes").elements().forEachRemaining(pn -> {
                try {
                    palettes.add(mapper.treeToValue(pn, Palette.class));
                } catch (JsonProcessingException e) {
                    System.out.println("Unable to deserialize Palette form json. " + pn.toString());
                }
            });

            TreeMap<String, ObjectAttributes> objects = new TreeMap<>();
            Iterator<JsonNode> objectEntries = node.get("objects").elements();
            while (objectEntries.hasNext()) {
                JsonNode objectEntry = objectEntries.next();
                String name = objectEntry.get("name").asText();
                ObjectAttributes objectAttributes = mapper.treeToValue(objectEntry.get("obj_attrs"), ObjectAttributes.class);
                objects.put(name, objectAttributes);
            }

            return new Project(projectName, screenSizes, screens, characterBlocks, palettes, objects);
        }
    }

}
