package fr.insee.pogues.persistence.query;

import org.json.simple.JSONObject;

import java.util.Map;

/**
 * Questionnaire Service Query interface to assume the persistance of Pogues UI in JSON
 * 
 * @author I6VWID
 * 
 */

public interface QuestionnairesServiceQuery {
	
	public void close();

	public Map<String, JSONObject> getQuestionnaires() throws Exception;
	
	public JSONObject getQuestionnaireByID(String id) throws Exception;
	
	public void deleteQuestionnaireByID(String id) throws Exception;
	
	public Map<String, JSONObject> getQuestionnairesByOwner(String owner) throws Exception;
	
	public void createOrReplaceQuestionnaire(String id, JSONObject questionnaire) throws Exception;

	public void createQuestionnaire(JSONObject questionnaire) throws Exception;

	public void updateQuestionnaire(JSONObject questionnaire) throws Exception;


}
