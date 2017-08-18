package fr.insee.pogues.utils.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class contains JSON functions to convert Java collection on JSON string.
 * 
 * @author I6VWID
 *
 */
public class JSONFunctions {

	final static Logger logger = Logger.getLogger(JSONFunctions.class);
	
	
	
	public static JSONObject renameQuestionnairePlural(JSONObject questionnaire) throws ParseException{
		
		String data = questionnaire.toString();
		data = data.replace("\"Child\":","\"children\":");
		data = data.replace("\"Control\":","\"controls\":");
		data = data.replace("\"GoTo\":","\"gotos\":");
		data = data.replace("\"Response\":","\"responses\":");
		data = data.replace("\"Declaration\":","\"declarations\":");
		JSONParser parser = new JSONParser();
        JSONObject questionnaireUpdate = null;
        questionnaireUpdate = (JSONObject) parser.parse(data);
		return questionnaireUpdate;
	}
	
	public static String getJSONArray(Map<String, String> data) {

		String jSONResult = "[";
		for(Entry<String, String> entry : data.entrySet()) {
			String valeur = entry.getValue();
			jSONResult = jSONResult + valeur + ",";
		}
		return jSONResult.substring(0, jSONResult.length()-1)+"]" ;

	}

	public static String getJSONArray(List<String> data){
		JSONArray json = new JSONArray();
		for(String permission: data){
			json.add(permission);
		}
		return json.toJSONString();
	}
	
	
	public static String getJSON(Map<String, String> data) {

		String jSONResult = "{";
		for(Entry<String, String> entry : data.entrySet()) {
			String valeur = entry.getValue();
			jSONResult = jSONResult + "\"" + entry.getKey() + "\":\"" + valeur + "\",";
		}
		return jSONResult.substring(0, jSONResult.length()-1)+"}" ;

	}
	
	
	public static Map<String, String> getMap(String questionnaireListJSON) {

		Map<String, String> mapResult = new HashMap<String,String>();
		
		JSONArray jsonTabResults = new JSONArray();
		JSONParser parser = new JSONParser();

		try {
			jsonTabResults = (JSONArray) parser.parse(questionnaireListJSON);
			for(JSONObject js : (ArrayList<JSONObject>) jsonTabResults ){
				String id = (String) js.get("id");
				String questionnaireString = js.toJSONString();
				mapResult.put(id,questionnaireString);
			}
		} catch (ParseException e) {
			logger.error("JSON malformed, parsing Exception");
			e.printStackTrace();
		}		
		
		return mapResult ;

	}
	
	

	
	
	public static String getQuestionnaireIDinQuestionnaire(String questionnaireJSON) {
		JSONObject jsonResults = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			jsonResults = (JSONObject) parser.parse(questionnaireJSON);
			return (String) jsonResults.get("id");
		} catch (ParseException e) {
			logger.error("JSON malformed, parsing Exception");
			e.printStackTrace();
		}
		return null;

	}
	
	
	public static List<String> getQuestionnaireIDinQuestionnaireList(String questionnaireList) {
		
		ArrayList<String> result = new ArrayList<String>();

		JSONArray jsonTabResults = new JSONArray();
		JSONParser parser = new JSONParser();

		try {
			jsonTabResults = (JSONArray) parser.parse(questionnaireList);
			for(JSONObject js : (ArrayList<JSONObject>) jsonTabResults ){
				result.add((String) js.get("id"));
			}
		} catch (ParseException e) {
			logger.error("JSON malformed, parsing Exception");
			e.printStackTrace();
		}
		
		return result;

	}
	
	
	
	
}
