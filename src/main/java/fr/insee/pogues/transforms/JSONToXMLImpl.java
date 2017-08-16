package fr.insee.pogues.transforms;

import fr.insee.pogues.conversion.JSONToXMLTranslator;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

@Service
public class JSONToXMLImpl implements JSONToXML {

    private JSONToXMLTranslator translator = new JSONToXMLTranslator(true);

    public void transform(InputStream input, OutputStream output) throws JAXBException, IOException {
        if(null == input){
            throw new NullPointerException("Null input");
        }
        if(null == output){
            throw new NullPointerException("Null output");
        }
        try {
            System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
            StreamSource source = new StreamSource(input);
            byte[] out = translator.translate(source).getBytes(Charset.forName("UTF-8"));
            output.write(out, 0, out.length);
        } catch (Exception e) {
            throw e;
        }
    }

    public String transform(InputStream input) throws Exception {
        if(null == input){
            throw new NullPointerException("Null input");
        }
        try {
            System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
            StreamSource source = new StreamSource(input);
            return translator.translate(source);
        } catch (Exception e) {
            throw e;
        }
    }

    public String transform(String input) throws Exception {
        if(null == input){
            throw new NullPointerException("Null input");
        }
        try {
            System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
            return translator.translate(input);
        } catch (Exception e) {
            throw e;
        }
    }
}
