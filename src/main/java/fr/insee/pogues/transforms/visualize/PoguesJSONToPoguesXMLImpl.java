package fr.insee.pogues.transforms.visualize;

import fr.insee.pogues.conversion.JSONToXMLTranslator;
import fr.insee.pogues.utils.json.JSONFunctions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import static fr.insee.pogues.transforms.visualize.ModelMapper.inputStream2String;
import static fr.insee.pogues.transforms.visualize.ModelMapper.string2BOAS;

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
			JSONParser parser = new JSONParser();
			JSONObject questionnaire = (JSONObject) parser.parse(input);
			questionnaire = JSONFunctions.renameQuestionnairePlural(questionnaire);
			return translator.translate(questionnaire.toJSONString());
		} catch (Exception e) {
			throw new Exception(String.format("%s:%s", getClass().getName(), e.getMessage()));
		}

	}

}
