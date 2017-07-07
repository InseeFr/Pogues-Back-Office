package fr.insee.pogues.rest.test.mock;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by acordier on 06/07/17.
 */

public class QuestionnaireServiceMock implements QuestionnairesService {

    private Map<String, JSONObject> db;

    public QuestionnaireServiceMock(){
        db = new HashMap<>();
        JSONObject questionnaire = createMockQuestionnaire();
        db.put((String)questionnaire.get("id"), questionnaire);
    }

    @Override
    public Map<String, JSONObject> getQuestionnaireList() throws Exception {
        return db;
    }

    @Override
    public Map<String, JSONObject> getQuestionnairesByOwner(String owner) throws Exception {
        return db.entrySet()
                .stream()
                .filter(entry-> entry.getKey() == "owner")
                .filter(entry-> entry.getValue().get("owner") == owner)
                .collect(Collectors.toMap(entry-> entry.getKey(), entry -> entry.getValue()));
    }

    @Override
    public JSONObject getQuestionnaireByID(String id) throws Exception {
        return db.get(id);
    }

    @Override
    public void deleteQuestionnaireByID(String id) throws Exception {
        db.remove(id);

    }

    @Override
    public void deleteAllQuestionnaires() throws Exception {
        // no test needed
    }

    @Override
    public void createQuestionnaire(JSONObject questionnaire) throws Exception {
        db.put((String)questionnaire.get("id"), questionnaire);
    }

    @Override
    public void updateQuestionnaire(String id, JSONObject questionnaire) throws Exception {
        db.put(id, questionnaire);
    }

    public JSONObject createMockQuestionnaire(){
        JSONObject questionnaire = new JSONObject();
        questionnaire.put("id", "mockid");
        questionnaire.put("owner", "mockowner");
        questionnaire.put("name", "mockname");
        return questionnaire;
    }
}
