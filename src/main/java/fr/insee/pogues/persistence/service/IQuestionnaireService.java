package fr.insee.pogues.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface IQuestionnaireService {
    List<JsonNode> getQuestionnairesMetadata(String owner) throws Exception;

    List<JsonNode> getQuestionnairesStamps() throws Exception;

    List<JsonNode> getQuestionnairesByOwner(String id) throws Exception;

    JsonNode getQuestionnaireByID(String id) throws Exception;

    JsonNode getQuestionnaireByIDWithReferences(String id) throws Exception;

    JsonNode getQuestionnaireWithReferences(JsonNode jsonQuestionnaire) throws Exception;

    void deleteQuestionnaireByID(String id) throws Exception;

    void createQuestionnaire(JsonNode questionnaire) throws Exception;

    void updateQuestionnaire(String id, JsonNode questionnaire) throws Exception;

}
