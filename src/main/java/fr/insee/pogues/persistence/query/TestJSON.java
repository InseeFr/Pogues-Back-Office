package fr.insee.pogues.persistence.query;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TestJSON {

	public static void main(String[] args) {


	String json = "{\"Questionnaire\": {\"id\": \"1\",\"Name\": \"FIRSTQUESTIONNAIRE\",\"Child\": [{\"id\": \"iytt3i83\",\"Name\": \"SEQ1\"},{\"id\": \"iyttf4qh\",\"Name\": \"SEQ2\"}]}}";	
	String jsonTab = "{\"QuestionnairesList\" : [{\"Questionnaire\": {\"id\": \"1\",\"Name\": \"FIRSTQUESTIONNAIRE\"}},{\"Questionnaire\": {\"id\": \"2\",\"Name\": \"FIRSTQUESTIONNAIRE\"}}]}";
		   
	JSONObject jsonResults = new JSONObject();
	JSONArray jsonTabResults = new JSONArray();

	JSONParser parser = new JSONParser();

	try {
		jsonResults = (JSONObject) parser.parse(json);
	} catch (ParseException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	JSONObject questionnaire = (JSONObject)jsonResults.get("Questionnaire");
	System.out.println(questionnaire.get("id"));
	
	
	try {
		jsonResults = (JSONObject) parser.parse(jsonTab);
		jsonTabResults = (JSONArray) jsonResults.get("QuestionnairesList");
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}


	for(JSONObject js : (ArrayList<JSONObject>) jsonTabResults ){
		 questionnaire = (JSONObject) js.get("Questionnaire");
	     System.out.println(questionnaire.get("id"));
	}
	
	Iterator i = jsonTabResults.iterator();
	while (i.hasNext()) {
		jsonResults = (JSONObject) i.next();
        questionnaire = (JSONObject) jsonResults.get("Questionnaire");
        System.out.println(questionnaire.get("id"));
    }
	


}

}
