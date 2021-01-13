package fr.insee.pogues.transforms;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pogues.persistence.query.NonUniqueResultException;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.webservice.rest.PoguesException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Service
public class LunaticJSONToUriQueenImpl implements LunaticJSONToUriQueen{
	
	@Autowired
	private QuestionnairesService questionnaireService;

	@Override
	public void transform(InputStream input, OutputStream output, Map<String, Object> params, String surveyName)
			throws Exception {
		throw new NotImplementedException();
		
	}

	@Override
	public String transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	public String transform(String input, Map<String, Object> params, String surveyName) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject jsonContent = (JSONObject) parser.parse(input);
		try {
			questionnaireService.createJsonLunatic(jsonContent);
		} catch (PoguesException eUnique) {
			String id  = (String) jsonContent.get("id");
			questionnaireService.updateJsonLunatic(id, jsonContent);
        } catch (Exception e) {
            throw new Exception(String.format("%s:%s", getClass().getName(), e.getMessage()));
        }
		return null;
	}





}
