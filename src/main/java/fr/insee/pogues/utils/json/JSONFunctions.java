package fr.insee.pogues.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class contains JSON functions to convert Java collection on JSON string.
 * 
 * @author I6VWID
 *
 */
@Slf4j
public class JSONFunctions {

	public static JsonNode jsonStringtoJsonNode(String jsonString) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
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
		inputNode.put(replacement, input.get(key));
		inputNode.remove(key);
		return inputNode;
	}

	public static String objectNodeToPrettyJsonString(JsonNode jsonNode) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
		} catch (JsonProcessingException e) {
			log.error("Error converting JsonNode to pretty JSON string", e);
			return jsonNode.toString();
		}
	}

}
