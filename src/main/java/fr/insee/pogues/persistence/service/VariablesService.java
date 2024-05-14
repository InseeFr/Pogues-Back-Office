package fr.insee.pogues.persistence.service;

import org.json.simple.JSONArray;

import java.io.IOException;

public interface VariablesService {

	/**
	 * Used for pogues frontend
	 * @param id questionnaire id
	 * @return variables as json string with caveats from pogues-model (like format for datedatatype, ...)
	 */
	String getVariablesByQuestionnaire(String id) throws IOException;

	/**
	 * Used for public enemy, delivers
	 * @param id
	 * @return variables as json directly from DB
	 */
	JSONArray getVariablesByQuestionnaireForPublicEnemy(String id);

}
