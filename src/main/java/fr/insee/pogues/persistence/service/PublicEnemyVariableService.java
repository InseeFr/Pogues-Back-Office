package fr.insee.pogues.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PublicEnemyVariableService {

	@Autowired
	QuestionnaireService questionnaireService;

	public PublicEnemyVariableService(QuestionnaireService questionnaireService) {
		this.questionnaireService = questionnaireService;
	}

	/**
	 * Used for public enemy, delivers
	 * @param id
	 * @return variables as json directly from DB
	 */
	public ArrayNode getVariablesByQuestionnaireForPublicEnemy(String id) {
		try {
			JsonNode questionnaire = questionnaireService.getQuestionnaireByIDWithReferences(id);
			// We test the existence of the questionnaire in repository
			if (questionnaire != null) {
				JsonNode variables = questionnaire.get("Variables");
				return (ArrayNode) variables.get("Variable");
			}
		} catch (Exception e) {
			log.error("Exception occurred when trying to get variables from questionnaire with id={}", id, e);
		}
		return null;
	}

	/**
	 * Used for pogues frontend
	 * @param id questionnaire id
	 * @return variables as json string with caveats from pogues-model (like format for datedatatype, ...)
	 */
	public JsonNode getVariablesByQuestionnaire(String id) {
		try {
			JsonNode questionnaire = questionnaireService.getQuestionnaireByID(id);
			// We test the existence of the questionnaire in repository
			if (questionnaire != null) {
				return questionnaire.get("Variables");
			}
		} catch (Exception e) {
			log.error("Exception occurred when trying to get variables from questionnaire with id={}", id, e);
		}
		return null;
	}

}
