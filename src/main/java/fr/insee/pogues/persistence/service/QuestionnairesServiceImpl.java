package fr.insee.pogues.persistence.service;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pogues.persistence.query.EntityNotFoundException;
import fr.insee.pogues.persistence.query.NonUniqueResultException;
import fr.insee.pogues.persistence.query.QuestionnairesServiceQuery;
import fr.insee.pogues.webservice.rest.PoguesException;

/**
 * Questionnaire Service to assume the persistence of Pogues UI in JSON
 *
 * @author I6VWID
 * @see /Pogues-BO/src/main/java/fr/insee/pogues/webservice/rest/
 *      PoguesPersistenceQuestionnaireList.java
 */
@Service
public class QuestionnairesServiceImpl implements QuestionnairesService {

	@Autowired
	private QuestionnairesServiceQuery questionnaireServiceQuery;

	public List<JSONObject> getQuestionnaireList() throws Exception {
		List<JSONObject> questionnaires = questionnaireServiceQuery.getQuestionnaires();
		if (questionnaires.isEmpty()) {
			throw new PoguesException(404, "Not found", "Aucun questionnaire enregistré");
		}
		return questionnaires;
	}

	public List<JSONObject> getQuestionnairesMetadata(String owner) throws Exception {
		if (null == owner || owner.isEmpty()) {
			throw new PoguesException(400, "Bad Request", "Missing parameter: owner");
		}
		return questionnaireServiceQuery.getMetaQuestionnaire(owner);
	}
	
	public List<JSONObject> getQuestionnairesStamps() throws Exception {
		List<JSONObject> stamps = questionnaireServiceQuery.getStamps();
		if (stamps.isEmpty()) {
			throw new PoguesException(404, "Not found", "Aucun timbre enregistré");
		}
		return stamps;
	}

	public List<JSONObject> getQuestionnairesByOwner(String owner) throws Exception {
		if (null == owner || owner.isEmpty()) {
			throw new PoguesException(400, "Bad Request", "Missing parameter: owner");
		}
		return questionnaireServiceQuery.getQuestionnairesByOwner(owner);
	}

	public JSONObject getQuestionnaireByID(String id) throws Exception {
		JSONObject questionnaire = this.questionnaireServiceQuery.getQuestionnaireByID(id);
		if (null == questionnaire) {
			throw new PoguesException(404, "Not found", "Pas de questionnaire pour cet identifiant");
		}
		return questionnaire;
	}
	
	public JSONObject getJsonLunaticByID(String id) throws Exception {
        JSONObject questionnaireLunatic = this.questionnaireServiceQuery.getJsonLunaticByID(id);
        if (null == questionnaireLunatic) {
            throw new PoguesException(404, "Not found", "Pas de questionnaire pour cet identifiant");
        }
        return questionnaireLunatic;
    }

	public void deleteQuestionnaireByID(String id) throws Exception {
		questionnaireServiceQuery.deleteQuestionnaireByID(id);
	}
	
	public void deleteJsonLunaticByID(String id) throws Exception {
		questionnaireServiceQuery.deleteJsonLunaticByID(id);		
	}

	public void createQuestionnaire(JSONObject questionnaire) throws Exception {
		try {
			this.questionnaireServiceQuery.createQuestionnaire(questionnaire);
		} catch (NonUniqueResultException e) {
			throw new PoguesException(409, "Conflict", e.getMessage());
		}
	}
	
	public void createJsonLunatic(JSONObject dataLunatic) throws Exception {
        try {
            this.questionnaireServiceQuery.createJsonLunatic(dataLunatic);
        } catch (NonUniqueResultException e) {
            throw new PoguesException(409, "Conflict", e.getMessage());
        }
    }

	public void updateQuestionnaire(String id, JSONObject questionnaire) throws Exception {
		try {
			this.questionnaireServiceQuery.updateQuestionnaire(id, questionnaire);
		} catch (EntityNotFoundException e) {
			throw new PoguesException(404, "Not found", e.getMessage());
		}
	}
	
	public void updateJsonLunatic(String id, JSONObject dataLunatic) throws Exception {
	    try {
	        this.questionnaireServiceQuery.updateJsonLunatic(id, dataLunatic);
	    } catch (EntityNotFoundException e) {
	        throw new PoguesException(404, "Not found", e.getMessage());
	    }
	}
}
