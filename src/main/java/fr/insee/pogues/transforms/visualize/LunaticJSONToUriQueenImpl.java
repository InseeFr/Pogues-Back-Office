package fr.insee.pogues.transforms.visualize;

import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.utils.suggester.SuggesterVisuTreatment;
import fr.insee.pogues.webservice.rest.PoguesException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Service
public class LunaticJSONToUriQueenImpl implements LunaticJSONToUriQueen{
	
	@Autowired
	private QuestionnairesService questionnaireService;

	@Value("${application.host}")
	private String apiHost;
	
	@Value("${application.name}")
	private String apiName;
	
	@Value("${application.scheme}")
	private String apiScheme;
	@Value("${application.queen.vis.host}")
	private String orchestratorHost;

	@Value("${application.queen.vis.path}")
	private String visualizePath;
	@Value("${application.queen.vis.queryparams.questionnaire}")
	private String queryParamsQuestionnaire;

	@Value("${application.queen.vis.queryparams.nomenclatures}")
	private String queryParamsNomenclatures;

	@Value("${application.api.nomenclatures}")
	private String apiNomenclatures;

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

		List<String> nomenclatureIds = (List<String>) params.get("nomenclatureIds");
		String jsonStringNomenclaturesForVisu = SuggesterVisuTreatment.createJsonNomenclaturesForVisu(nomenclatureIds, apiNomenclatures).toJSONString();
		String urlGetJsonLunatic = String.format("%s://%s%s/api/persistence/questionnaire/json-lunatic/%s", apiScheme, apiHost, apiName, id);

		return String.format(
				"%s%s?%s=%s&%s=%s",
				orchestratorHost,
				visualizePath,
				queryParamsQuestionnaire,
				URLEncoder.encode(urlGetJsonLunatic, "UTF-8"),
				queryParamsNomenclatures,
				URLEncoder.encode(jsonStringNomenclaturesForVisu, "UTF-8")
		);
	}

}
