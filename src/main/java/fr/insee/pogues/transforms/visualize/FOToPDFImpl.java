package fr.insee.pogues.transforms.visualize;

import fr.insee.pogues.configuration.FOPConfiguration;
import fr.insee.pogues.utils.ClasspathResourceResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.apps.io.ResourceResolverFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

@Service
@Slf4j
public class FOToPDFImpl implements FOToPDF {
	@Autowired
	private FOPConfiguration fopConfiguration;

	@Override
	public ByteArrayOutputStream transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {

		FopFactory fopFactory = fopConfiguration.getFopFactory();
		// Step 2: Set up output stream.
		// Note: Using BufferedOutputStream for performance reasons
		// (helpful with FileOutputStreams).
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// Step 3: Construct fop with desired output format
		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
		// Step 4: Setup JAXP using identity transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(); // identity
															// transformer
		// Step 5: Setup input and output for XSLT transformation
		// Setup input stream
		Source src = new StreamSource(input);
		// Resulting SAX events (the generated FO) must be piped through
		// to FOP
		Result res = new SAXResult(fop.getDefaultHandler());
		// Step 6: Start XSLT transformation and FOP processing
		transformer.transform(src, res);
		return out;

	}
	
}
