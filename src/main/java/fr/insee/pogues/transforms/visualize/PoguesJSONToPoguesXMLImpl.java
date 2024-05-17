package fr.insee.pogues.transforms.visualize;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.conversion.JSONToXMLTranslator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import static fr.insee.pogues.transforms.visualize.ModelMapper.inputStream2String;
import static fr.insee.pogues.transforms.visualize.ModelMapper.string2BOAS;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static fr.insee.pogues.utils.json.JSONFunctions.renameQuestionnairePlural;

@Service
public class PoguesJSONToPoguesXMLImpl implements PoguesJSONToPoguesXML {

	private JSONToXMLTranslator translator = new JSONToXMLTranslator(true);


	@Override
	public ByteArrayOutputStream transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {
		if (null == input) {
			throw new NullPointerException("Null input");
		}
		String inputAsString = inputStream2String(input);
		String outputAsString = transform(inputAsString);
		return string2BOAS(outputAsString);

	}

	private String transform(String input) throws Exception {
		if (null == input) {
			throw new NullPointerException("Null input");
		}
		try {
			JsonNode questionnaire = jsonStringtoJsonNode(input);
			questionnaire = renameQuestionnairePlural(questionnaire);
			return translator.translate(questionnaire.toString());
		} catch (Exception e) {
			throw new Exception(String.format("%s:%s", getClass().getName(), e.getMessage()));
		}

	}

}
