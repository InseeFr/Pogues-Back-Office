package fr.insee.pogues.metadata.mock;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class ColecticaMocks {

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
}
