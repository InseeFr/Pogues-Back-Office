package fr.insee.pogues.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.configuration.auth.security.restrictions.StampsRestrictionsService;
import fr.insee.pogues.exception.NullReferenceException;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.exceptions.EntityNotFoundException;
import fr.insee.pogues.persistence.exceptions.NonUniqueResultException;
import fr.insee.pogues.persistence.repository.JSONLunaticRepository;
import fr.insee.pogues.persistence.repository.QuestionnaireRepository;
import fr.insee.pogues.transforms.visualize.composition.QuestionnaireComposition;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

/**
 * Questionnaire Service to assume the persistence of Pogues UI in JSON
 *
 * @author I6VWID
 * @see /Pogues-BO/src/main/java/fr/insee/pogues/webservice/rest/
 *      PoguesPersistenceQuestionnaireList.java
 */
@Service
@Slf4j
public class JSONLunaticService {

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Autowired
    private JSONLunaticRepository jsonLunaticRepository;

    /**
     *
     * @param id Id of requested object
     * @return JSON Lunatic representation of a questionnaire
     * @throws Exception
     */
    public JsonNode getJsonLunaticByID(String id) throws Exception {
        JsonNode questionnaireLunatic = this.jsonLunaticRepository.getJsonLunaticByID(id);
        if (null == questionnaireLunatic) {
            throw new PoguesException(404, "Not found", "Pas de questionnaire pour cet identifiant");
        }
        return questionnaireLunatic;
    }

    /**
     *
     * @param id Id of the object we want to delete
     * @throws Exception
     */
    public void deleteJsonLunaticByID(String id) throws Exception {
        jsonLunaticRepository.deleteJsonLunaticByID(id);
    }

    /**
     * Save the JSON Lunatic representation of a questionnaire
     * @param questionnaireLunatic JSON Lunatic representation of a questionnaire
     * @throws Exception
     */
    public void createJsonLunatic(JsonNode dataLunatic) throws Exception {
        try {
            this.jsonLunaticRepository.createJsonLunatic(dataLunatic);
        } catch (NonUniqueResultException e) {
            throw new PoguesException(409, "Conflict", e.getMessage());
        }
    }

    /**
     * Update a questionnaire object
     * @param questionnaireLunatic JSON Lunatic representation of a questionnaire
     * @param id id of the questionnaire
     * @throws Exception
     */
    public void updateJsonLunatic(String id, JsonNode dataLunatic) throws Exception {
        try {
            this.jsonLunaticRepository.updateJsonLunatic(id, dataLunatic);
        } catch (EntityNotFoundException e) {
            throw new PoguesException(404, "Not found", e.getMessage());
        }
    }
}
