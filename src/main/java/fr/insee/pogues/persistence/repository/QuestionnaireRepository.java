package fr.insee.pogues.persistence.repository;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Questionnaire Service Query interface to assume the persistance of Pogues UI in JSON
 *
 * @author I6VWID
 */

public interface QuestionnaireRepository {

    List<JsonNode> getQuestionnaires() throws Exception;

    JsonNode getQuestionnaireByID(String id) throws Exception;

    JsonNode getJsonLunaticByID(String id) throws Exception;

    void deleteQuestionnaireByID(String id) throws Exception;
    
    void deleteJsonLunaticByID(String id) throws Exception;

    List<JsonNode> getQuestionnairesByOwner(String owner) throws Exception;

    void createQuestionnaire(JsonNode questionnaire) throws Exception;
    
    void createJsonLunatic(JsonNode questionnaireLunatic) throws Exception;

    void updateQuestionnaire(String id, JsonNode questionnaire) throws Exception;
    
    void updateJsonLunatic(String id, JsonNode questionnaireLunatic) throws Exception;

    List<JsonNode> getMetaQuestionnaire(String owner) throws Exception;

    List<JsonNode> getStamps() throws Exception;
    
    String countQuestionnaires() throws Exception;
}
