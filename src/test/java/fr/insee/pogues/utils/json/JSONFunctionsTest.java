package fr.insee.pogues.utils.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test Class used to test JSON functions
 */
class JSONFunctionsTest {

	@Test
	void renamePluralTest() {
		ObjectNode input = JsonNodeFactory.instance.objectNode();
		input.put("children", "some children");
		input.put("controls", "some controls");
		input.put("gotos", "some gotos");
		input.put("responses", "some responses");
		input.put("declarations", "some declarations");
		JsonNode output = JSONFunctions.renameQuestionnairePlural(input);
		assertTrue(output.has("Child"));
		assertTrue(output.has("Control"));
		assertTrue(output.has("GoTo"));
		assertTrue(output.has("Response"));
		assertTrue(output.has("Declaration"));
	}

}
