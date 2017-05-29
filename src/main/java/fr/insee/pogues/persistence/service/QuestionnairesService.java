package fr.insee.pogues.persistence.service;

import java.util.Map;
import java.util.Map.Entry;

import fr.insee.pogues.persistence.query.QuestionnairesServiceQuery;
import fr.insee.pogues.persistence.query.QuestionnairesServiceQueryPostgresqlImpl;
import fr.insee.pogues.utils.json.JSONFunctions;


/**
 * Questionnaire Service to assume the persistance of Pogues UI in JSON
 * 
 * @author I6VWID
 * 
 * @see /Pogues-BO/src/main/java/fr/insee/pogues/webservice/rest/
 *      PoguesPersistenceQuestionnaireList.java
 *
 */
public class QuestionnairesService {

	private QuestionnairesServiceQuery questionnaireServiceQuery;

	/**
	 * Contructor for Questionnaires Service, init the transaction if needed
	 * 
	 */
	public QuestionnairesService() {
		// TODO externalisation of the implementation parameter
		questionnaireServiceQuery = new QuestionnairesServiceQueryPostgresqlImpl();
	}
	
	/**
	 * A method to close the transaction if needed.
	 * 
	 */
	public void close(){
		questionnaireServiceQuery.close();
	}

	/**
	 * A method to get the `QuestionnaireList` object in the database
	 * 
	 * @return the questionnaires list JSON description of the questionnaires
	 */
	public String getQuestionnaireList() {

		Map<String, String> questionnaires = questionnaireServiceQuery.getQuestionnaires();
		String questionnaireList = JSONFunctions.getJSONArray(questionnaires);
		return questionnaireList;

	}
	
	public String getQuestionnairesByOwner(String owner) {

		Map<String, String> questionnaires = questionnaireServiceQuery.getQuestionnairesByOwner(owner);
		String questionnaireList = JSONFunctions.getJSONArray(questionnaires);
		return questionnaireList;

	}
	

	public String getQuestionnaireByID(String id) {

		return questionnaireServiceQuery.getQuestionnaireByID(id);

	}

	public void createOrReplaceQuestionnaire(String id, String questionnaire) {

		questionnaireServiceQuery.createOrReplaceQuestionnaire(id, questionnaire);

	}
	
	public String createQuestionnaire(String questionnaire) {
		String id = JSONFunctions.getQuestionnaireIDinQuestionnaire(questionnaire);
		this.createOrReplaceQuestionnaire(id,questionnaire);
		return id;
	}

	public void createOrReplaceQuestionnaireList(String questionnaireList) {
		
		Map<String, String> questionnaires = JSONFunctions.getMap(questionnaireList);
		for(Entry<String, String> entry : questionnaires.entrySet()) {
			String id = entry.getKey();
			String questionnaire = entry.getValue();
			this.createOrReplaceQuestionnaire(id,questionnaire);
		}
	}
	
	

	
	
	
	
	
	

}
