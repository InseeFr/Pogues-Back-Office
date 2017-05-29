package fr.insee.pogues.persistence.query;

import java.util.Map;

/**
 * Questionnaire Service Query interface to assume the persistance of Pogues UI in JSON
 * 
 * @author I6VWID
 * 
 */

public interface QuestionnairesServiceQuery {
	
	public void close();

	public Map<String, String> getQuestionnaires();
	
	public String getQuestionnaireByID(String id);
	
	public Map<String, String> getQuestionnairesByOwner(String owner);
	
	public void createOrReplaceQuestionnaire(String id, String questionnaires);
	
}
