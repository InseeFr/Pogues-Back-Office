package fr.insee.pogues.transforms;

import fr.insee.pogues.conversion.JSONToXMLTranslator;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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
            System.out.println("XXX transform json to xml");
            System.out.println("XXX " + input.getClass().getName());
            System.out.println("XXX " + output.getClass().getName());
            System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
            StreamSource source = new StreamSource(input);
            System.out.println("XXX set stream source: " + input);
            byte[] out = translator.translate(source).getBytes(Charset.forName("UTF-8"));
            System.out.println("XXX JSON translated to XML");
            output.write(out, 0, out.length);
            System.out.println("XXX output written");
        } catch (JAXBException e) {
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
//            input.close();
//            output.close();
        }
    }

    public String transform(InputStream input) throws Exception {
        if(null == input){
            throw new NullPointerException("Null input");
        }
        try {
            System.out.println("XXX transform json to xml");
            System.out.println("XXX " + input.getClass().getName());
            System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
            StreamSource source = new StreamSource(input);
            System.out.println("XXX set stream source: " + input);
            return translator.translate(source);
        } catch (JAXBException e) {
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
//            input.close();
//            output.close();
        }
    }

    public String transform(String input) throws Exception {
        if(null == input){
            throw new NullPointerException("Null input");
        }
        try {
            System.out.println("XXX transform json to xml");
            System.out.println("XXX " + input.getClass().getName());
            System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
            return translator.translate(input);
        } catch (JAXBException e) {
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
//            input.close();
//            output.close();
        }
    }
}
