package fr.insee.pogues.utils.json;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Test Class used to test JSON functions
 * 
 * @author I6VWID
 *
 */
public class TestJSONFunctions {

	final static Logger logger = LogManager.getLogger(TestJSONFunctions.class);

	private String json1 = "{\"id\":\"1\",\"Name\":\"FIRSTQUESTIONNAIRE\"}";
	private String json2 = "{\"id\":\"2\",\"Name\":\"FIRSTQUESTIONNAIRE2\"}";
	private String jsonArray = "[{\"id\":\"1\",\"Name\":\"FIRSTQUESTIONNAIRE\"},{\"id\":\"2\",\"Name\":\"FIRSTQUESTIONNAIRE2\"}]";
	
	private HashMap<String, String> data = new HashMap<String, String>();
	
	@Test
	public void getJSONArray() {
		data.put("2",json2);
		data.put("1",json1);
		String expected = jsonArray;
		Assert.assertEquals(expected, JSONFunctions.getJSONArray(data));
	}
	
	@Test
	public void getJSON() {
		data.put("id", "1");
		data.put("Name", "FIRSTQUESTIONNAIRE");
		String expected = json1;
		Assert.assertEquals(expected, JSONFunctions.getJSON(data));
	}
	
	@Test
	public void getMap() {
		
		HashMap<String, String> expected = new HashMap<String, String>();
		expected.put("1",json1);
		expected.put("2",json2);
		String data = jsonArray;
		Assert.assertEquals(expected, JSONFunctions.getMap(data));
		
	}
	
	@Test
	public void getIDinQuestionnaire() {
		
		String expected = "1";
		Assert.assertEquals(expected, JSONFunctions.getQuestionnaireIDinQuestionnaire(json1));
		
	}
	
	@Test
	public void getIDinQuestionnaireList() {
		
		
		ArrayList<String>  expected = new ArrayList<String>();
		expected.add("1");
		expected.add("2");
		Assert.assertEquals(expected, JSONFunctions.getQuestionnaireIDinQuestionnaireList(jsonArray));
		
	}


	@Test
	public void renamePluralTest() throws ParseException {
		JSONObject input = new JSONObject();
		input.put("Child", null);
		input.put("Control", null);
		input.put("GoTo", null);
		input.put("Response", null);
		input.put("Declaration", null);
		JSONObject output = JSONFunctions.renameQuestionnairePlural(input);
		Assert.assertTrue(output.containsKey("children"));
		Assert.assertTrue(output.containsKey("controls"));
		Assert.assertTrue(output.containsKey("gotos"));
		Assert.assertTrue(output.containsKey("responses"));
		Assert.assertTrue(output.containsKey("declarations"));
	}

}
