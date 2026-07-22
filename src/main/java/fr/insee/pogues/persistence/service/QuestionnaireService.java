package fr.insee.pogues.persistence.service;

import fr.insee.pogues.configuration.auth.security.restrictions.StampsRestrictionsService;
import fr.insee.pogues.exception.questionnaire.composition.NullReferenceException;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.exception.questionnaire.QuestionnaireNotFoundException;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.exceptions.EntityNotFoundException;
import fr.insee.pogues.persistence.exceptions.NonUniqueResultException;
import fr.insee.pogues.persistence.repository.QuestionnaireRepository;
import fr.insee.pogues.service.modelcleaning.ModelCleaningService;
import fr.insee.pogues.transforms.visualize.composition.QuestionnaireComposition;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

/**
 * Questionnaire Service to assume the persistence of Pogues UI in JSON
 *
 * @author I6VWID
 * @see /Pogues-BO/src/main/java/fr/insee/pogues/webservice/rest/
 * PoguesPersistenceQuestionnaireList.java
 */
@Service
@AllArgsConstructor
@Slf4j
public class QuestionnaireService implements IQuestionnaireService{

    private final QuestionnaireRepository questionnaireRepository;
    private final VersionService versionService;
    private final StampsRestrictionsService stampsRestrictionsService;
    private final ModelCleaningService modelCleaningService;

    public List<JsonNode> getQuestionnairesMetadata(String owner) throws Exception {
        if (null == owner || owner.isEmpty()) {
            throw new PoguesException(400, "Bad Request", "Missing parameter: owner");
        }
        return questionnaireRepository.getMetaQuestionnaire(owner);
    }

    public List<JsonNode> getQuestionnairesStamps() throws Exception {
        return questionnaireRepository.getStamps();
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
     * @return Cleaned JSON representation of the questionnaire
     * @throws Exception
     */
    public JsonNode getQuestionnaireByID(String id) throws Exception {
        return modelCleaningService.cleanModel(getRawQuestionnaireByID(id));
    }

    public Map<String, JsonNode> getQuestionnairesByIds(List<String> ids) throws Exception {
        return questionnaireRepository.getQuestionnairesByIds(ids);
    }

    private JsonNode getRawQuestionnaireByID(String id) throws Exception {
        JsonNode questionnaire = this.questionnaireRepository.getQuestionnaireByID(id);
        if (null == questionnaire) {
            throw new QuestionnaireNotFoundException(String.format("Questionnaire with id %s does not exist", id));
        }
        return questionnaire;
    }

    @Override
    public Questionnaire getQuestionnaireModelByID(String id) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(getQuestionnaireByID(id));
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
        long start = System.currentTimeMillis();
        JsonNode jsonQuestionnaire = getRawQuestionnaireByID(id);
        log.debug("Time to get main questionnaire (id: {}) in DB: {} ms", id, System.currentTimeMillis() - start);
        JsonNode fullQuestionnaire = getQuestionnaireWithReferences(jsonQuestionnaire);
        log.debug("Time to get a complete questionnaire with ref (id: {}) in DB: {} ms", id, System.currentTimeMillis() - start);
        return fullQuestionnaire;
    }

    @Override
    public Questionnaire getQuestionnaireModelByIDWithReferences(String id) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(getQuestionnaireByIDWithReferences(id));
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
        if(modelCleaningService != null) modelCleaningService.cleanModel(questionnaireWithReferences);
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
            String poguesId = questionnaire.get("id").asString();
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
        if(modelCleaningService != null) modelCleaningService.cleanModel(questionnaire);
        List<String> references = new ArrayList<>(questionnaire.getChildQuestionnaireRef()); // make copy of references
        deReference(references, questionnaire);
        return questionnaire;
    }

    private void deReference(List<String> references, Questionnaire questionnaire) throws Exception {
        log.debug("--- START Deref of {} with {} refs---", questionnaire.getId(), references.size());
        Map<String, JsonNode> childrenMap = this.getQuestionnairesByIds(references);
        for (String reference : references) {
            JsonNode referencedJsonQuestionnaire = childrenMap.get(reference);
            if (referencedJsonQuestionnaire == null) {
                throw new NullReferenceException(String.format(
                        "Null reference behind reference '%s' in questionnaire '%s'.",
                        reference, questionnaire.getId()));
            } else {
                Questionnaire referencedQuestionnaire = PoguesDeserializer.questionnaireToJavaObject(referencedJsonQuestionnaire);
                if(modelCleaningService != null) modelCleaningService.cleanModel(referencedQuestionnaire);
                // Coherence check
                if (! reference.equals(referencedQuestionnaire.getId())) {
                    log.warn("Reference '{}' found in questionnaire '{}' mismatch referenced questionnaire's id '{}'",
                            reference, questionnaire.getId(), referencedQuestionnaire.getId());
                }
                //
                QuestionnaireComposition.insertReference(questionnaire, referencedQuestionnaire);
                // remove reference of child questionnaire
                questionnaire.getChildQuestionnaireRef().remove(reference);
            }
        }
        log.debug("--- END Deref of {}", questionnaire.getId());
    }
}
