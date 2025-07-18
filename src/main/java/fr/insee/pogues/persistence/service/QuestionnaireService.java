package fr.insee.pogues.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.configuration.auth.security.restrictions.StampsRestrictionsService;
import fr.insee.pogues.exception.NullReferenceException;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.exceptions.EntityNotFoundException;
import fr.insee.pogues.persistence.exceptions.NonUniqueResultException;
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
public class QuestionnaireService {

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Autowired
    private VersionService versionService;

    @Autowired
    protected StampsRestrictionsService stampsRestrictionsService;

    public List<JsonNode> getQuestionnairesMetadata(String owner) throws Exception {
        if (null == owner || owner.isEmpty()) {
            throw new PoguesException(400, "Bad Request", "Missing parameter: owner");
        }
        return questionnaireRepository.getMetaQuestionnaire(owner);
    }

    public List<JsonNode> getQuestionnairesStamps() throws Exception {
        List<JsonNode> stamps = questionnaireRepository.getStamps();
        if (stamps.isEmpty()) {
            throw new PoguesException(404, "Not found", "Aucun timbre enregistré");
        }
        return stamps;
    }

    /**
     *
     * @param owner Should be a known username
     * @return A collection of questionnaire objects mapped to their id
     * @throws Exception
     */
    public List<JsonNode> getQuestionnairesByOwner(String owner) throws Exception {
        if (null == owner || owner.isEmpty()) {
            throw new PoguesException(400, "Bad Request", "Missing parameter: owner");
        }
        return questionnaireRepository.getQuestionnairesByOwner(owner);
    }

    /**
     *
     * @param id Id of requested object
     * @return JSON representation of the questionnaire
     * @throws Exception
     */
    public JsonNode getQuestionnaireByID(String id) throws Exception {
        JsonNode questionnaire = this.questionnaireRepository.getQuestionnaireByID(id);
        if (null == questionnaire) {
            throw new PoguesException(404, "Not found", "Pas de questionnaire pour cet identifiant");
        }
        return questionnaire;
    }

    /**
     * A questionnaire can "contain" other questionnaires. These questionnaires appear as references.
     * This method makes it possible to obtain the complete questionnaire, by replacing the references with the complete questionnaires.
     * @param id Id of requested object
     *
     * @return JSON representation of the questionnaire with references
     * @throws Exception
     */
    public JsonNode getQuestionnaireByIDWithReferences(String id) throws Exception {
        JsonNode jsonQuestionnaire = this.getQuestionnaireByID(id);
        return getQuestionnaireWithReferences(jsonQuestionnaire);
    }

    /**
     * A questionnaire can "contain" other questionnaires. These questionnaires appear as references.
     * This method makes it possible to obtain the complete questionnaire, by replacing the references with the complete questionnaires.
     *
     * @param jsonQuestionnaire JSON representation of a questionnaire
     * @return JSON representation of the questionnaire with its references
     * @throws Exception
     */
    public JsonNode getQuestionnaireWithReferences(JsonNode jsonQuestionnaire) throws Exception {
        Questionnaire questionnaireWithReferences = this.deReference(jsonQuestionnaire);
        return jsonStringtoJsonNode(PoguesSerializer.questionnaireJavaToString(questionnaireWithReferences));
    }

    /**
     *
     * @param id Id of the object we want to delete
     * @throws Exception
     */
    public void deleteQuestionnaireByID(String id) throws Exception {
        versionService.deleteAllVersionsByQuestionnaireIdExceptLast(id);
        questionnaireRepository.deleteQuestionnaireByID(id);
    }

    /**
     * Save the JSON representation of a questionnaire
     * @param questionnaire JSON representation of a questionnaire
     * @throws Exception
     */
    public void createQuestionnaire(JsonNode questionnaire) throws Exception {
        try {
            String poguesId = questionnaire.get("id").asText();
            this.questionnaireRepository.createQuestionnaire(questionnaire);
            String author = stampsRestrictionsService.getUser().getUserId();
            this.versionService.createVersionOfQuestionnaire(poguesId, questionnaire, author);
        } catch (NonUniqueResultException e) {
            throw new PoguesException(409, "Conflict", e.getMessage());
        }
    }

    /**
     * Update a questionnaire object
     * @param questionnaire JSON representation of a questionnaire
     * @param id id of the questionnaire
     * @throws Exception
     */
    public void updateQuestionnaire(String id, JsonNode questionnaire) throws Exception {
        try {
            this.questionnaireRepository.updateQuestionnaire(id, questionnaire);
            String author = stampsRestrictionsService.getUser().getUserId();
            this.versionService.createVersionOfQuestionnaire(id, questionnaire, author);
        } catch (EntityNotFoundException e) {
            throw new PoguesException(404, "Not found", e.getMessage());
        }
    }

    public Questionnaire deReference(JsonNode jsonQuestionnaire) throws Exception {
        Questionnaire questionnaire = PoguesDeserializer.questionnaireToJavaObject(jsonQuestionnaire);
        List<String> references = questionnaire.getChildQuestionnaireRef();
        deReference(references, questionnaire);
        return questionnaire;
    }

    private void deReference(List<String> references, Questionnaire questionnaire) throws Exception {
        for (String reference : references) {
            JsonNode referencedJsonQuestionnaire = this.getQuestionnaireByID(reference);
            if (referencedJsonQuestionnaire == null) {
                throw new NullReferenceException(String.format(
                        "Null reference behind reference '%s' in questionnaire '%s'.",
                        reference, questionnaire.getId()));
            } else {
                Questionnaire referencedQuestionnaire = PoguesDeserializer.questionnaireToJavaObject(referencedJsonQuestionnaire);
                // Coherence check
                if (! reference.equals(referencedQuestionnaire.getId())) {
                    log.warn("Reference '{}' found in questionnaire '{}' mismatch referenced questionnaire's id '{}'",
                            reference, questionnaire.getId(), referencedQuestionnaire.getId());
                }
                //
                QuestionnaireComposition.insertReference(questionnaire, referencedQuestionnaire);
            }
        }
    }
}
