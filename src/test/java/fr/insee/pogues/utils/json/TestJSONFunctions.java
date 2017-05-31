package fr.insee.pogues.utils.json;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Test Class used to test JSON functions
 * 
 * @author I6VWID
 *
 */
public class TestJSONFunctions {

	final static Logger logger = Logger.getLogger(TestJSONFunctions.class);

	private String json1 = "{\"id\":\"1\",\"Name\":\"FIRSTQUESTIONNAIRE\"}";
	private String json2 = "{\"id\":\"2\",\"Name\":\"FIRSTQUESTIONNAIRE2\"}";
	private String jsonArray = "[{\"id\":\"1\",\"Name\":\"FIRSTQUESTIONNAIRE\"},{\"id\":\"2\",\"Name\":\"FIRSTQUESTIONNAIRE2\"}]";
	
	private HashMap<String, String> data = new HashMap<String, String>();
	
	@Test
	public void getJSONArray() {
		data.put("2",json2);
		data.put("1",json1);
		String expected = jsonArray;
		assertEquals(expected,fr.insee.pogues.utils.json.JSONFunctions.getJSONArray(data));
	}
	
	@Test
	public void getJSON() {
		data.put("id", "1");
		data.put("Name", "FIRSTQUESTIONNAIRE");
		String expected = json1;
		assertEquals(expected,fr.insee.pogues.utils.json.JSONFunctions.getJSON(data));
	}
	
	@Test
	public void getMap() {
		
		HashMap<String, String> expected = new HashMap<String, String>();
		expected.put("1",json1);
		expected.put("2",json2);
		String data = jsonArray;
		assertEquals(expected,fr.insee.pogues.utils.json.JSONFunctions.getMap(data));
		
	}
	
	@Test
	public void getIDinQuestionnaire() {
		
		String expected = "1";
		assertEquals(expected,fr.insee.pogues.utils.json.JSONFunctions.getQuestionnaireIDinQuestionnaire(json1));
		
	}
	
	@Test
	public void getIDinQuestionnaireList() {
		
		
		ArrayList<String>  expected = new ArrayList<String>();
		expected.add("1");
		expected.add("2");
		assertEquals(expected,fr.insee.pogues.utils.json.JSONFunctions.getQuestionnaireIDinQuestionnaireList(jsonArray));
		
	}


}
