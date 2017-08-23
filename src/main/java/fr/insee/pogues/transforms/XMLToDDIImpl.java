package fr.insee.pogues.transforms;

import net.sf.saxon.s9api.*;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

//import java.io.ByteArrayOutputStream;

/**
 * Created by acordier on 20/07/17.
 * TODO Register as a provider
 */
@Service
public class XMLToDDIImpl implements XMLToDDI {

    private final String XSLT_FILE_0 = "transforms/xslt/1-Goto2IfThen.xsl";
    private final String XSLT_FILE_1 = "transforms/xslt/2-GoGo2ToTo.xsl";
    private final String XSLT_FILE_2 = "transforms/xslt/3-GoTo2Sequence.xsl";
    private final String XSLT_FILE_3 = "transforms/xslt/4-beginEnd2filter.xsl";
    private final String XSLT_FILE_4 = "transforms/xslt/5-xml2DDIDeref.xsl";


    private Logger logger = LogManager.getLogger(XMLToDDIImpl.class);

    public void transform(InputStream input, OutputStream output, Map<String, Object>params) throws Exception {
        try {
            if (null == input) {
                throw new NullPointerException("Null input");
            }
            if (null == output) {
                throw new NullPointerException("Null output");
            }
            Processor processor = new Processor(false);
            Source source = new StreamSource(input);
            XsltTransformer t = createPipeline(source, output, processor);
            t.transform();
        } catch (SaxonApiException e) {
            logger.error(String.format("Message: %s, Line: %d, Error Code: %s",
                    e.getMessage(), e.getLineNumber(), e.getErrorCode()));
            throw e;
        }
    }

    public String transform(InputStream input, Map<String, Object>params) throws Exception {
        ByteArrayOutputStream output = null;
        try {
            if (null == input) {
                throw new NullPointerException("Null input");
            }
            output = new ByteArrayOutputStream();
            transform(input, output, params);
            return output.toString(StandardCharsets.UTF_8).trim();
        } catch (SaxonApiException e) {
            logger.error(String.format("Message: %s, Line: %d, Error Code: %s",
                    e.getMessage(), e.getLineNumber(), e.getErrorCode()));
            throw e;
        } finally {
            output.close();
        }
    }

    public String transform(String input, Map<String, Object>params) throws Exception {
        try {
            if (null == input) {
                throw new NullPointerException("Null input");
            }
            InputStream is = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
            return transform(is, params);
        } catch (SaxonApiException e) {
            logger.error(String.format("Message: %s, Line: %d, Error Code: %s",
                    e.getMessage(), e.getLineNumber(), e.getErrorCode()));
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    private XsltTransformer createPipeline(Source input, OutputStream output, Processor processor) throws Exception {
        DocumentBuilder builder = processor.newDocumentBuilder();
        XdmNode source = builder.build(input);
        XsltTransformer t0 = createTransformer(processor, XSLT_FILE_0);
        XsltTransformer t1 = createTransformer(processor, XSLT_FILE_1);
        XsltTransformer t2 = createTransformer(processor, XSLT_FILE_2);
        XsltTransformer t3 = createTransformer(processor, XSLT_FILE_3);
        XsltTransformer t4 = createTransformer(processor, XSLT_FILE_4);
        Serializer out = createSerializer(processor, output);
        t0.setInitialContextNode(source);
        t0.setDestination(t1);
        t1.setDestination(t2);
        t2.setDestination(t3);
        t3.setDestination(t4);
        t4.setDestination(out);
        return t0;
    }

    private XsltTransformer createTransformer(Processor processor, String path) throws SaxonApiException {
        XsltCompiler compiler = processor.newXsltCompiler();
        InputStream xslResource = getClass().getClassLoader().getResourceAsStream(path);
        if (null == xslResource) {
            throw new NullPointerException("NULL XSLT Resource");
        }
        XsltExecutable xsl = compiler.compile(new StreamSource(xslResource));
        XsltTransformer transformer = xsl.load();
        transformer.setSchemaValidationMode(ValidationMode.LAX);
        transformer.setErrorListener(new PoguesErrorListener());
        return transformer;
    }

    private Serializer createSerializer(Processor processor, OutputStream output) {
        Serializer out = processor.newSerializer(output);
        out.setOutputProperty(Serializer.Property.SAXON_STYLESHEET_VERSION, "2.0");
        out.setOutputProperty(Serializer.Property.METHOD, "xml");
        out.setOutputProperty(Serializer.Property.INDENT, "yes");
        out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
        out.setOutputProperty(Serializer.Property.ENCODING, "utf-8");
        return out;
    }

}
