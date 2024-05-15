package fr.insee.pogues.persistence.service;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VariablesServiceImpl implements VariablesService {

	@Autowired QuestionnairesService questionnairesService;

	public VariablesServiceImpl(QuestionnairesService questionnairesService) {
		this.questionnairesService = questionnairesService;
	}

	public JSONArray getVariablesByQuestionnaireForPublicEnemy(String id) {
		try {
			JSONObject questionnaire = questionnairesService.getQuestionnaireByIDWithReferences(id);
			// We test the existence of the questionnaire in repository
			if (questionnaire != null) {
				JSONObject variables = (JSONObject) questionnaire.get("Variables");
				return (JSONArray) variables.get("Variable");
			}
		} catch (Exception e) {
			log.error("Exception occurred when trying to get variables from questionnaire with id={}", id, e);
		}
		return null;
	}

	public JSONObject getVariablesByQuestionnaire(String id) {
		try {
			JSONObject questionnaire = questionnairesService.getQuestionnaireByID(id);
			// We test the existence of the questionnaire in repository
			if (questionnaire != null) {
				JSONObject variables = (JSONObject) questionnaire.get("Variables");
				return variables;
			}
		} catch (Exception e) {
			log.error("Exception occurred when trying to get variables from questionnaire with id={}", id, e);
		}
		return null;
	}

}
