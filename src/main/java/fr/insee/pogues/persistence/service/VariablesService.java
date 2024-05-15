package fr.insee.pogues.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;

public interface VariablesService {

	/**
	 * Used for pogues frontend
	 * @param id questionnaire id
	 * @return variables as json string with caveats from pogues-model (like format for datedatatype, ...)
	 */
	JsonNode getVariablesByQuestionnaire(String id) throws IOException;

	/**
	 * Used for public enemy, delivers
	 * @param id
	 * @return variables as json directly from DB
	 */
	ArrayNode getVariablesByQuestionnaireForPublicEnemy(String id);

}
