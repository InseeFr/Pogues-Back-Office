package fr.insee.pogues.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.persistence.exceptions.EntityNotFoundException;
import fr.insee.pogues.persistence.exceptions.NonUniqueResultException;
import fr.insee.pogues.persistence.repository.JSONLunaticRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Questionnaire Service to assume the persistance of questionnaires' JSON unatic representation.
 * @see /Pogues-BO/src/main/java/fr/insee/pogues/webservice/rest/
 *      PoguesPersistenceQuestionnaireList.java
 */
@Service
@Slf4j
public class JSONLunaticService {

    @Autowired
    private JSONLunaticRepository jsonLunaticRepository;

    /**
     * Fetch the JSON Lunatic representation of the questionnaire.
     * @param id ID of the questionnaire to fetch
     * @return JSON Lunatic representation of a questionnaire
     * @throws PoguesException Questionnaire not found
     */
    public JsonNode getJsonLunaticByID(String id) throws Exception {
        JsonNode questionnaireLunatic = this.jsonLunaticRepository.getJsonLunaticByID(id);
        if (null == questionnaireLunatic) {
            throw new PoguesException(404, "Not found", "Pas de questionnaire pour cet identifiant");
        }
        return questionnaireLunatic;
    }

    /**
     * Delete the JSON Lunatic representation of the questionnaire.
     * @param id ID of the questionnaire to delete
     * @throws Exception
     */
    public void deleteJsonLunaticByID(String id) throws Exception {
        jsonLunaticRepository.deleteJsonLunaticByID(id);
    }

    /**
     * Create a JSON Lunatic representation of a questionnaire.
     * @param dataLunatic JSON Lunatic description of a questionnaire to create
     * @throws PoguesException A JSON Lunatic already exists for this questionnaire ID
     */
    public void createJsonLunatic(JsonNode dataLunatic) throws Exception {
        try {
            this.jsonLunaticRepository.createJsonLunatic(dataLunatic);
        } catch (NonUniqueResultException e) {
            throw new PoguesException(409, "Conflict", e.getMessage());
        }
    }

    /**
     * Update the JSON Lunatic representation of the questionnaire.
     * @param id ID of the questionnaire to update
     * @param dataLunatic JSON Lunatic description of a questionnaire to update
     * @throws PoguesException Questionnaire not found
     */
    public void updateJsonLunatic(String id, JsonNode dataLunatic) throws Exception {
        try {
            this.jsonLunaticRepository.updateJsonLunatic(id, dataLunatic);
        } catch (EntityNotFoundException e) {
            throw new PoguesException(404, "Not found", e.getMessage());
        }
    }
}
