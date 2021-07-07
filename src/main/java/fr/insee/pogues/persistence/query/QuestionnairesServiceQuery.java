package fr.insee.pogues.persistence.query;

import org.json.simple.JSONObject;

import java.util.List;

/**
 * Questionnaire Service Query interface to assume the persistance of Pogues UI in JSON
 *
 * @author I6VWID
 */

public interface QuestionnairesServiceQuery {

    List<JSONObject> getQuestionnaires() throws Exception;

    JSONObject getQuestionnaireByID(String id) throws Exception;
    
    JSONObject getJsonLunaticByID(String id) throws Exception;

    void deleteQuestionnaireByID(String id) throws Exception;
    
    void deleteJsonLunaticByID(String id) throws Exception;

    List<JSONObject> getQuestionnairesByOwner(String owner) throws Exception;

    void createQuestionnaire(JSONObject questionnaire) throws Exception;
    
    void createJsonLunatic(JSONObject dataLunatic) throws Exception;
    
    void updateJsonLunatic(String id, JSONObject dataLunatic) throws Exception;

    void updateQuestionnaire(String id, JSONObject questionnaire) throws Exception;

    List<JSONObject> getMetaQuestionnaire(String owner) throws Exception;

}
