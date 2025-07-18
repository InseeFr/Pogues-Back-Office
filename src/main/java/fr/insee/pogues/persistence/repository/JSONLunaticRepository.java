package fr.insee.pogues.persistence.repository;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Questionnaire Service Query interface to assume the persistance of questionnaires' JSON Lunatic representation.
 */
public interface JSONLunaticRepository {

    JsonNode getJsonLunaticByID(String id) throws Exception;

    void deleteJsonLunaticByID(String id) throws Exception;

    void createJsonLunatic(JsonNode questionnaireLunatic) throws Exception;

    void updateJsonLunatic(String id, JsonNode questionnaireLunatic) throws Exception;

}
