package fr.insee.pogues.transforms.visualize;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import fr.insee.pogues.conversion.XMLToJSONTranslator;

import static fr.insee.pogues.transforms.visualize.ModelMapper.inputStream2String;
import static fr.insee.pogues.transforms.visualize.ModelMapper.string2BOAS;

@Service
public class PoguesXMLToPoguesJSONImpl implements PoguesXMLToPoguesJSON {

    private XMLToJSONTranslator translator = new XMLToJSONTranslator(true);

    @Override
    public ByteArrayOutputStream transform(InputStream inputStream, Map<String, Object> params, String surveyName) throws Exception {
        if (null == inputStream) {
            throw new NullPointerException("Null input");
        }
        String inputAsString = inputStream2String(inputStream);
        String outputAsString = transform(inputAsString);
        return string2BOAS(outputAsString);
    }

    private String transform(String input) throws Exception {
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
