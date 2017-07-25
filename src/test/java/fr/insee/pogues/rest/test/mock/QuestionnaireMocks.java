package fr.insee.pogues.rest.test.mock;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

/**
 * Created by acordier on 06/07/17.
 */

public class QuestionnaireMocks {


    public static JSONObject createMockQuestionnaire(){
        try {

            String filePath = QuestionnaireMocks.class
                    .getClassLoader()
                    .getResource("questionnaire-json.json")
                    .getPath();
            JSONParser parser = new JSONParser();
            Object questionnaire = parser.parse(new FileReader(filePath));
            return (JSONObject)questionnaire;
        } catch(Exception e){
            return null;
        }
    }
}
