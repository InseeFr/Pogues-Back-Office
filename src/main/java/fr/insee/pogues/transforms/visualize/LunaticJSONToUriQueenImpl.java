package fr.insee.pogues.transforms.visualize;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.webservice.rest.PoguesException;

@Service
public class LunaticJSONToUriQueenImpl implements LunaticJSONToUriQueen{
	
	@Autowired
	private QuestionnairesService questionnaireService;
	
	@Value("${fr.insee.pogues.api.host}")
	private String apiHost;
	
	@Value("${fr.insee.pogues.api.name}")
	private String apiName;
	
	@Value("${fr.insee.pogues.api.scheme}")
	private String apiScheme;
	
	@Value("${fr.insee.pogues.api.remote.queen.host}")
	private String queenHost;
	
	@Value("${fr.insee.pogues.api.remote.queen.vis.path}")
	private String queenVisualizationPath;

	@Override
	public void transform(InputStream input, OutputStream output, Map<String, Object> params, String surveyName)
			throws Exception {
		throw new RuntimeException("Not Implemented");
		
	}

	@Override
	public String transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public String transform(String input, Map<String, Object> params, String surveyName) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject jsonContent = (JSONObject) parser.parse(input);
		String id  = (String) jsonContent.get("id");
		try {
			questionnaireService.createJsonLunatic(jsonContent);
		} catch (PoguesException e) {
			questionnaireService.updateJsonLunatic(id, jsonContent);
        } catch (Exception e) {
            throw new Exception(String.format("%s:%s", getClass().getName(), e.getMessage()));
        }
		String urlGetJsonLunatic = String.format("%s://%s%s/api/persistence/questionnaire/json-lunatic/%s",apiScheme,apiHost,apiName,id);
		return String.format("%s/%s%s", queenHost, queenVisualizationPath, URLEncoder.encode(urlGetJsonLunatic, "UTF-8"));
	}

}
