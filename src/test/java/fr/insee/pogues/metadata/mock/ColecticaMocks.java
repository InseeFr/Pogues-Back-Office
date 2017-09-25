package fr.insee.pogues.metadata.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.pogues.metadata.model.ColecticaItem;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ColecticaMocks {

    private static final JSONObject db = createDb();

    private static final ObjectMapper mapper = new ObjectMapper();

    public static JSONObject createDb() {
        try {
            String filePath = ColecticaMocks.class
                    .getClassLoader()
                    .getResource("colectica.json")
                    .getPath();
            JSONParser parser = new JSONParser();
            JSONObject db = (JSONObject)
                    parser.parse(new FileReader(filePath));
            return db;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<ColecticaItem> getItems() throws IOException {
        JSONArray items = (JSONArray) db.get("items");
        return Arrays.asList(
                mapper.readValue(items.toJSONString(), ColecticaItem[].class)
        );
    }
}
