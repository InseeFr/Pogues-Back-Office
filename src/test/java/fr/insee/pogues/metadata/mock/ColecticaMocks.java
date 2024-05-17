package fr.insee.pogues.metadata.mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.pogues.metadata.model.ColecticaItem;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

public class ColecticaMocks {

    private static final JsonNode db = createDb();

    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode createDb() {
        try {
            URL filePath = ColecticaMocks.class
                    .getClassLoader()
                    .getResource("colectica.json");
            JsonNode db = jsonStringtoJsonNode(Files.readString(Path.of(filePath.toURI())));
            return db;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<ColecticaItem> getItems() throws IOException {
        ArrayNode items = (ArrayNode) db.get("items");
        return Arrays.asList(
                mapper.readValue(items.toString(), ColecticaItem[].class)
        );
    }
}
