package fr.insee.pogues.persistence.repository;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Questionnaire Service Query interface to assume the persistance of Pogues UI in JSON.
 */
public interface QuestionnaireRepository {

    JsonNode getQuestionnaireByID(String id) throws Exception;

    void deleteQuestionnaireByID(String id) throws Exception;

    List<JsonNode> getQuestionnairesByOwner(String owner) throws Exception;

    void createQuestionnaire(JsonNode questionnaire) throws Exception;

    void updateQuestionnaire(String id, JsonNode questionnaire) throws Exception;

    List<JsonNode> getMetaQuestionnaire(String owner) throws Exception;

    List<JsonNode> getStamps() throws Exception;
    
    String countQuestionnaires() throws Exception;
}
