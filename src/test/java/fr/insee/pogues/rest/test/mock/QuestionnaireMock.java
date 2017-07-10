package fr.insee.pogues.rest.test.mock;

import org.json.simple.JSONObject;

/**
 * Created by acordier on 06/07/17.
 */

public class QuestionnaireMock {

    public static JSONObject createMockQuestionnaire() {
        JSONObject questionnaire = new JSONObject();
        questionnaire.put("id", "1");
        questionnaire.put("owner", "MOCK-GROUP");
        questionnaire.put("name", "How do you mock ?");
        return questionnaire;
    }

}
