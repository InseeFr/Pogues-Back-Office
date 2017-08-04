package fr.insee.pogues.transforms;

import fr.insee.pogues.conversion.JSONToXMLTranslator;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class JSonToXmlImpl implements JsonToXml {

    private JSONToXMLTranslator translator = new JSONToXMLTranslator();

    public void transform(InputStream input, OutputStream output) throws JAXBException, IOException {
        if(null == input){
            throw new NullPointerException("Null input");
        }
        if(null == output){
            throw new NullPointerException("Null output");
        }
        try {
            StreamSource source = new StreamSource(input);
            String out = translator.translate(source);
            output.write(out.getBytes(Charset.forName("UTF-8")));
        } catch (JAXBException e) {
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }
}
