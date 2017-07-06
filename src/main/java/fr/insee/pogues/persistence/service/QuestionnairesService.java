package fr.insee.pogues.persistence.service;

import org.json.simple.JSONObject;

import java.util.Map;

/**
 * Created by acordier on 05/07/17.
 */
public interface QuestionnairesService {
    Map<String, JSONObject> getQuestionnaireList() throws Exception;
    Map<String, JSONObject> getQuestionnairesByOwner(String owner)throws Exception;
    JSONObject getQuestionnaireByID(String id) throws Exception;
    void deleteQuestionnaireByID(String id) throws Exception;
    void deleteAllQuestionnaires() throws Exception;
    void createQuestionnaire(JSONObject questionnaire) throws Exception;
    void updateQuestionnaire(JSONObject questionnaire) throws Exception;
}
