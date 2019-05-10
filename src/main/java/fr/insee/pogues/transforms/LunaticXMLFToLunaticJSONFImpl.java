package fr.insee.pogues.transforms;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import fr.insee.lunatic.conversion.XMLLunaticFlatToJSONLunaticFlatTranslator;
import fr.insee.pogues.utils.json.JSONFunctions;

@Service
public class LunaticXMLFToLunaticJSONFImpl implements LunaticXMLFToLunaticJSONF {

	private XMLLunaticFlatToJSONLunaticFlatTranslator translator = new XMLLunaticFlatToJSONLunaticFlatTranslator(true);

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
			return translator.translate(input);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(String.format("%s:%s", getClass().getName(), e.getMessage()));
		}

	}

}
