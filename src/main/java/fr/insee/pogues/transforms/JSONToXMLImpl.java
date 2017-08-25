package fr.insee.pogues.transforms;

import fr.insee.pogues.conversion.JSONToXMLTranslator;
import fr.insee.pogues.utils.json.JSONFunctions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;

@Service
public class JSONToXMLImpl implements JSONToXML {

    private JSONToXMLTranslator translator = new JSONToXMLTranslator(true);

    @PostConstruct
    public void onInit(){
        System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
    }

    public void transform(InputStream input, OutputStream output, Map<String, Object> params) throws JAXBException, IOException {
        if(null == input){
            throw new NullPointerException("Null input");
        }
        if(null == output){
            throw new NullPointerException("Null output");
        }
        try {
            StreamSource source = new StreamSource(input);
            byte[] out = translator.translate(source).getBytes(Charset.forName("UTF-8"));
            output.write(out, 0, out.length);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String transform(InputStream input, Map<String, Object> params) throws Exception {
        if(null == input){
            throw new NullPointerException("Null input");
        }
        try {
            StreamSource source = new StreamSource(input);
            return translator.translate(source);
        } catch (Exception e) {
            throw e;
        }
    }

    public String transform(String input, Map<String, Object> params) throws Exception {
        if(null == input){
            throw new NullPointerException("Null input");
        }
        try {
            JSONParser parser = new JSONParser();
            JSONObject questionnaire = (JSONObject) parser.parse(input);
            questionnaire = JSONFunctions.renameQuestionnairePlural(questionnaire);
            return translator.translate(questionnaire.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
