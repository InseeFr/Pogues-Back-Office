package fr.insee.pogues.transforms.visualize.uri;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.utils.suggester.SuggesterVisuService;
import fr.insee.pogues.webservice.rest.PoguesException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

@Service
public class LunaticJSONToUriQueenImpl implements LunaticJSONToUriQueen{
	
	@Autowired
	private QuestionnairesService questionnaireService;

	@Autowired
	private SuggesterVisuService suggesterVisuService;

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

	@Override
	public URI transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {
		JsonNode jsonContent = jsonStringtoJsonNode(new String(input.readAllBytes(), StandardCharsets.UTF_8));
		String id  = jsonContent.get("id").asText();
		try {
			questionnaireService.createJsonLunatic(jsonContent);
		} catch (PoguesException e) {
			questionnaireService.updateJsonLunatic(id, jsonContent);
        } catch (Exception e) {
            throw new Exception(String.format("%s:%s", getClass().getName(), e.getMessage()));
        }

		List<String> nomenclatureIds = (List<String>) params.get("nomenclatureIds");
		String jsonStringNomenclaturesForVisu = suggesterVisuService.createJsonNomenclaturesForVisu(nomenclatureIds).toString();
		String urlGetJsonLunatic = String.format("%s://%s%s/api/persistence/questionnaire/json-lunatic/%s", apiScheme, apiHost, apiName, id);

		return URI.create(String.format(
				"%s%s?%s=%s&%s=%s",
				orchestratorHost,
				visualizePath,
				queryParamsQuestionnaire,
				URLEncoder.encode(urlGetJsonLunatic, "UTF-8"),
				queryParamsNomenclatures,
				URLEncoder.encode(jsonStringNomenclaturesForVisu, "UTF-8")
		));
	}

}
