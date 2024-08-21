package fr.insee.pogues.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.configuration.auth.security.restrictions.StampsRestrictionsService;
import fr.insee.pogues.exception.NullReferenceException;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.impl.EntityNotFoundException;
import fr.insee.pogues.persistence.impl.NonUniqueResultException;
import fr.insee.pogues.persistence.repository.QuestionnaireRepository;
import fr.insee.pogues.transforms.visualize.composition.QuestionnaireComposition;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.utils.json.JSONFunctions;
import fr.insee.pogues.webservice.rest.PoguesException;
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
public class QuestionnairesServiceImpl implements QuestionnairesService {

	@Autowired
	private QuestionnaireRepository questionnaireRepository;

	@Autowired
	private VersionService versionService;

	@Autowired
	protected StampsRestrictionsService stampsRestrictionsService;

	public List<JsonNode> getQuestionnaireList() throws Exception {
		List<JsonNode> questionnaires = questionnaireRepository.getQuestionnaires();
		if (questionnaires.isEmpty()) {
			throw new PoguesException(404, "Not found", "Aucun questionnaire enregistré");
		}
		return questionnaires;
	}
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

	public List<JsonNode> getQuestionnairesByOwner(String owner) throws Exception {
		if (null == owner || owner.isEmpty()) {
			throw new PoguesException(400, "Bad Request", "Missing parameter: owner");
		}
		return questionnaireRepository.getQuestionnairesByOwner(owner);
	}

	public JsonNode getQuestionnaireByID(String id) throws Exception {
		JsonNode questionnaire = this.questionnaireRepository.getQuestionnaireByID(id);
		if (null == questionnaire) {
			throw new PoguesException(404, "Not found", "Pas de questionnaire pour cet identifiant");
		}
		return questionnaire;
	}

	@Override
	public JsonNode getQuestionnaireByIDWithReferences(String id) throws Exception {
		JsonNode jsonQuestionnaire = this.getQuestionnaireByID(id);
		return getQuestionnaireWithReferences(jsonQuestionnaire);
	}

	@Override
	public JsonNode getQuestionnaireWithReferences(JsonNode jsonQuestionnaire) throws Exception {
		Questionnaire questionnaireWithReferences = this.deReference(jsonQuestionnaire);
		return jsonStringtoJsonNode(PoguesSerializer.questionnaireJavaToString(questionnaireWithReferences));
	}

	public JsonNode getJsonLunaticByID(String id) throws Exception {
		JsonNode questionnaireLunatic = this.questionnaireRepository.getJsonLunaticByID(id);
        if (null == questionnaireLunatic) {
            throw new PoguesException(404, "Not found", "Pas de questionnaire pour cet identifiant");
        }
        return questionnaireLunatic;
    }

	public void deleteQuestionnaireByID(String id) throws Exception {
		questionnaireRepository.deleteQuestionnaireByID(id);
		versionService.deleteVersionsByQuestionnaireId(id);
	}
	
	public void deleteJsonLunaticByID(String id) throws Exception {
		questionnaireRepository.deleteJsonLunaticByID(id);
	}

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
	
	public void createJsonLunatic(JsonNode dataLunatic) throws Exception {
        try {
            this.questionnaireRepository.createJsonLunatic(dataLunatic);
        } catch (NonUniqueResultException e) {
            throw new PoguesException(409, "Conflict", e.getMessage());
        }
    }

	public void updateQuestionnaire(String id, JsonNode questionnaire) throws Exception {
		try {
			this.questionnaireRepository.updateQuestionnaire(id, questionnaire);
			String author = stampsRestrictionsService.getUser().getUserId();
			this.versionService.createVersionOfQuestionnaire(id, questionnaire, author);
		} catch (EntityNotFoundException e) {
			throw new PoguesException(404, "Not found", e.getMessage());
		}
	}
	
	public void updateJsonLunatic(String id, JsonNode dataLunatic) throws Exception {
	    try {
	        this.questionnaireRepository.updateJsonLunatic(id, dataLunatic);
	    } catch (EntityNotFoundException e) {
	        throw new PoguesException(404, "Not found", e.getMessage());
	    }
	}

	public Questionnaire deReference(JsonNode jsonQuestionnaire) throws Exception {

		Questionnaire questionnaire = PoguesDeserializer.questionnaireToJavaObject(jsonQuestionnaire);
		List<String> references = JSONFunctions.getChildReferencesFromQuestionnaire(jsonQuestionnaire);
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
