package dev.cnpuvache.gba.tile_manager.domain.serializers;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.cnpuvache.gba.tile_manager.domain.ScreenEntry;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class ScreenDataMapDeserializer extends JsonDeserializer<Map<Integer, Map<Integer, ScreenEntry>>> {
    @Override
    public Map<Integer, Map<Integer, ScreenEntry>> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        ObjectMapper mapper = new ObjectMapper();
        Map<Integer, Map<Integer, ScreenEntry>> data = new TreeMap<>();
        for (JsonNode rowJson : node) {
            int y = rowJson.get("y").asInt();
            for (JsonNode cellJson : rowJson.get("cells")) {
                int x = cellJson.get("x").asInt();
                System.out.println(x);
                ScreenEntry entry = mapper.treeToValue(cellJson.get("entry"), ScreenEntry.class);
                if (!data.containsKey(y)) {
                    data.put(y, new TreeMap<>());
                }
                data.get(y).put(x, entry);
            }
        }
        return data;
    }
}
