package fr.insee.pogues.transforms.visualize;

import fr.insee.pogues.conversion.JSONToXMLTranslator;
import fr.insee.pogues.utils.json.JSONFunctions;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class PoguesJSONToPoguesXMLImpl implements PoguesJSONToPoguesXML {

	private JSONToXMLTranslator translator = new JSONToXMLTranslator(true);

	@PostConstruct
	public void onInit() {
		System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
	}

	public void transform(InputStream input, OutputStream output, Map<String, Object> params, String surveyName)
			throws Exception {
		if (null == input) {
			throw new NullPointerException("Null input");
		}
		if (null == output) {
			throw new NullPointerException("Null output");
		}
		byte[] out = transform(input, params, surveyName).getBytes(Charset.forName("UTF-8"));
		output.write(out, 0, out.length);
	}

	public String transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {
		if (null == input) {
			throw new NullPointerException("Null input");
		}
		return transform(IOUtils.toString(input, StandardCharsets.UTF_8.name()), params, surveyName);

	}

	public String transform(String input, Map<String, Object> params, String surveyName) throws Exception {
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
