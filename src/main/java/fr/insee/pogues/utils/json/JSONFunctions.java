package fr.insee.pogues.utils.json;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JsonPointer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;


/**
 * This class contains JSON functions to convert Java collection on JSON string.
 * 
 * @author I6VWID
 *
 */
@Slf4j
public class JSONFunctions {

	public static JsonNode jsonStringtoJsonNode(String jsonString) {
		ObjectMapper objectMapper = JsonMapper.builder().build();
		return objectMapper.readTree(jsonString);
	}

	public static JsonNode renameQuestionnairePlural(JsonNode questionnaire) {
		questionnaire = renameKey(questionnaire, "children", "Child");
		questionnaire = renameKey(questionnaire, "controls", "Control");
		questionnaire = renameKey(questionnaire, "gotos", "GoTo");
		questionnaire = renameKey(questionnaire, "responses", "Response");
		questionnaire = renameKey(questionnaire, "declarations", "Declaration");
		return questionnaire;
	}

	private static JsonNode renameKey(JsonNode input, String key, String replacement){
		ObjectNode inputNode = (ObjectNode) input;
		if(null == inputNode.get(key)){
			return input;
		}
		inputNode.putPOJO(replacement, input.get(key));
		inputNode.remove(key);
		return inputNode;
	}

}
