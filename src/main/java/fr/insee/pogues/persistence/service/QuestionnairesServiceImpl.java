package fr.insee.pogues.persistence.service;

import fr.insee.pogues.persistence.query.EntityNotFoundException;
import fr.insee.pogues.persistence.query.NonUniqueResultException;
import fr.insee.pogues.persistence.query.QuestionnairesServiceQuery;
import fr.insee.pogues.user.query.UserServiceQuery;
import fr.insee.pogues.utils.json.JSONFunctions;
import fr.insee.pogues.webservice.rest.PoguesException;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * Questionnaire Service to assume the persistance of Pogues UI in JSON
 *
 * @author I6VWID
 *
 * @see /Pogues-BO/src/main/java/fr/insee/pogues/webservice/rest/
 *      PoguesPersistenceQuestionnaireList.java
 *
 */
@Service
public class QuestionnairesServiceImpl implements QuestionnairesService {

	final static Logger logger = Logger.getLogger(QuestionnairesService.class);

	@Autowired
	private QuestionnairesServiceQuery questionnaireServiceQuery;

	@Autowired
	private UserServiceQuery userServiceQuery;


	/**
	 * A method to get the `QuestionnaireList` object in the database
	 *
	 * @return the questionnaires list JSON description of the questionnaires
	 */
	public List<JSONObject> getQuestionnaireList() throws Exception {
		try {
			List<JSONObject> questionnaires = questionnaireServiceQuery.getQuestionnaires();
			if(questionnaires.isEmpty()){
				throw new PoguesException(404, "Not found", "Aucun questionnaire enregistré");
			}
			return questionnaires;
		} catch(Exception e){
			throw e;
		}
	}

	public List<JSONObject> getQuestionnairesByOwner(String owner)throws Exception {
		try {
			if(null == owner || owner.isEmpty()){
				throw new PoguesException(400, "Bad Request", "Missing parameter: owner");
			}
			return questionnaireServiceQuery.getQuestionnairesByOwner(owner);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * A method to get a `Questionnaire` object in the database given its ID
	 *
	 * @return the questionnaire matching the id
	 */
	public JSONObject getQuestionnaireByID(String id) throws Exception {
		try {
			JSONObject questionnaire = this.questionnaireServiceQuery.getQuestionnaireByID(id);
			if(questionnaire.isEmpty()){
				throw new PoguesException(404, "Not found", "Pas de questionnaire pour cet identifiant");
			}
			return questionnaire;
		}  catch (NonUniqueResultException e) {
			logger.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}

	}

	public void deleteQuestionnaireByID(String id) throws Exception {
		try {
			questionnaireServiceQuery.deleteQuestionnaireByID(id);
		} catch(Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	public void deleteAllQuestionnaires() throws Exception {
		try {
			questionnaireServiceQuery.deleteAllQuestionnaires();
		} catch(Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	public void createQuestionnaire(JSONObject questionnaire) throws Exception {
		try {
			this.questionnaireServiceQuery.createQuestionnaire(questionnaire);
		} catch(NonUniqueResultException e) {
			logger.error(e.getMessage());
			throw new PoguesException(400, "Bad request", e.getMessage());
		} catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}

	public void updateQuestionnaire(String id, JSONObject questionnaire) throws Exception {
		try {
			this.questionnaireServiceQuery.updateQuestionnaire(id, questionnaire);
		} catch(EntityNotFoundException e) {
			logger.error(e.getMessage());
			throw new PoguesException(404, "Not found", e.getMessage());
		} catch(Exception e){
			logger.error(e.getMessage());
			throw e;
		}
	}

	public void createOrReplaceQuestionnaireList(String questionnaireList) {

		Map<String, String> questionnaires = JSONFunctions.getMap(questionnaireList);
//		for(Entry<String, String> entry : questionnaires.entrySet()) {
//			String id = entry.getKey();
//			String questionnaire = entry.getValue();
//			this.createOrReplaceQuestionnaire(id,questionnaire);
//		}
	}
}
