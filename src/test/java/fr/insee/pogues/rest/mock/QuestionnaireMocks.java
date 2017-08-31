package fr.insee.pogues.rest.mock;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

/**
 * Created by acordier on 06/07/17.
 */

public class QuestionnaireMocks {


    public static JSONObject createMockQuestionnaire() {
        try {
            String filePath = QuestionnairesService.class
                    .getClassLoader()
                    .getResource("questionnaire-json.json")
                    .getPath();
            JSONParser parser = new JSONParser();
            JSONObject questionnaire = (JSONObject)
                    parser.parse(new FileReader(filePath));
            questionnaire.put("owner", "DG75-L120");
            return questionnaire;
        } catch (Exception e) {
            return null;
        }
    }
}
