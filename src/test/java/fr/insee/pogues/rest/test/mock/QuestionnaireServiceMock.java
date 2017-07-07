package fr.insee.pogues.rest.test.mock;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by acordier on 06/07/17.
 */

public class QuestionnaireServiceMock implements QuestionnairesService {

    private List<JSONObject> db;

    public QuestionnaireServiceMock(){
        db = new ArrayList<>();
        JSONObject questionnaire = createMockQuestionnaire();
        db.add(questionnaire);
    }

    @Override
    public List<JSONObject> getQuestionnaireList() throws Exception {
        return db;
    }

    @Override
    public List<JSONObject> getQuestionnairesByOwner(String owner) throws Exception {
        return db
                .stream()
                .filter(entry-> entry.get("owner") == owner)
                .collect(Collectors.toList());
    }

    @Override
    public JSONObject getQuestionnaireByID(String id) throws Exception {
        return null;
    }

    @Override
    public void deleteQuestionnaireByID(String id) throws Exception {
    }

    @Override
    public void deleteAllQuestionnaires() throws Exception {
        // no test needed
    }

    @Override
    public void createQuestionnaire(JSONObject questionnaire) throws Exception {
        db.add(questionnaire);
    }

    @Override
    public void updateQuestionnaire(String id, JSONObject questionnaire) throws Exception {
    }

    public JSONObject createMockQuestionnaire(){
        JSONObject questionnaire = new JSONObject();
        questionnaire.put("id", "mockid");
        questionnaire.put("owner", "mockowner");
        questionnaire.put("name", "mockname");
        return questionnaire;
    }
}
