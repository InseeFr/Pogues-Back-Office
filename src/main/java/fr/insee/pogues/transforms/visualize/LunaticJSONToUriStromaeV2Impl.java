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

import fr.insee.pogues.exceptions.PoguesException;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Service
public class LunaticJSONToUriStromaeV2Impl implements LunaticJSONToUriStromaeV2{

	@Autowired
	private QuestionnairesService questionnaireService;

	@Value("${fr.insee.pogues.api.host}")
	private String apiHost;

	@Value("${fr.insee.pogues.api.name}")
	private String apiName;

	@Value("${fr.insee.pogues.api.scheme}")
	private String apiScheme;

	@Value("${fr.insee.pogues.api.remote.stromaev2.vis.url}")
	private String uriStromaeV2;

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
		String id  = (String) jsonContent.get("id");
		try {
			questionnaireService.createJsonLunatic(jsonContent);
		} catch (PoguesException e) {
			questionnaireService.updateJsonLunatic(id, jsonContent);
        } catch (Exception e) {
            throw new Exception(String.format("%s:%s", getClass().getName(), e.getMessage()));
        }
		String urlGetJsonLunatic = String.format("%s://%s%s/persistence/questionnaire/json-lunatic/%s",apiScheme,apiHost,apiName,id);
		String uriVisuStromaeV2 = String.format("%s%s", uriStromaeV2, URLEncoder.encode(urlGetJsonLunatic, "UTF-8"));
		return uriVisuStromaeV2;
	}
	
}
