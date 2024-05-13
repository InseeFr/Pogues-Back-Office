package fr.insee.pogues.transforms.visualize;

import fr.insee.pogues.configuration.StaticResourcesForFOPConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.fop.apps.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
public class FOToPDFImpl implements FOToPDF {

	public static final String TEMP_FOLDER_PATH = System.getProperty("java.io.tmpdir") + "/eno";
	
	public static final String FINAL_FO_EXTENSION = "-final-out.fo";

	@Autowired
	private StaticResourcesForFOPConfig staticResourcesForFOPConfig;

	@Override
	public void transform(InputStream input, OutputStream output, Map<String, Object> params, String surveyName)
			throws Exception {
		log.debug("Eno transformation");
		if (null == input) {
			throw new NullPointerException("Null input");
		}
		if (null == output) {
			throw new NullPointerException("Null output");
		}
		String odt = transform(input, params, surveyName);
		log.debug("Eno transformation finished");
		output.write(odt.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {
		if (null == input) {
			throw new NullPointerException("Null input");
		}
		File enoInput;
		enoInput = File.createTempFile("eno", ".xml");
		FileUtils.copyInputStreamToFile(input, enoInput);
		return transform(enoInput, params, surveyName);
	}

	@Override
	public String transform(String input, Map<String, Object> params, String surveyName) throws Exception {
		File enoInput;
		if (null == input) {
			throw new NullPointerException("Null input");
		}
		enoInput = File.createTempFile("eno", ".fo");
		FileUtils.writeStringToFile(enoInput, input, StandardCharsets.UTF_8);
		return transform(enoInput, params, surveyName);
	}

	private String transform(File file, Map<String, Object> params, String surveyName) throws Exception {


		org.apache.fop.configuration.Configuration fopConf = staticResourcesForFOPConfig.getFopConfiguration();
		URI imgFolderUri = staticResourcesForFOPConfig.getImgFolderUri();

		// Step 1: Construct a FopFactory by specifying a reference to the
		// configuration file
		// (reuse if you plan to render multiple documents!)

		FopFactoryBuilder builder = new FopFactoryBuilder(imgFolderUri).setConfiguration(fopConf);
		builder.setBaseURI(imgFolderUri);
		FopFactory fopFactory = builder.build();

		
		File dirTemp = new File(TEMP_FOLDER_PATH);
		if (!dirTemp.exists()) {
			if (dirTemp.mkdir()) {
				log.debug("Create eno temp directory");
			} else {
				log.debug("Fail to create temp directory");
			}
		}		
		String outFilePath = TEMP_FOLDER_PATH + "/form" + FINAL_FO_EXTENSION;
		File outFilePDF = new File(FilenameUtils.removeExtension(outFilePath) + ".pdf");

		// Step 2: Set up output stream.
		// Note: Using BufferedOutputStream for performance reasons
		// (helpful with FileOutputStreams).
		OutputStream out = new BufferedOutputStream(new FileOutputStream(outFilePDF));
		// Step 3: Construct fop with desired output format
		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

		// Step 4: Setup JAXP using identity transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(); // identity
															// transformer

		// Step 5: Setup input and output for XSLT transformation
		// Setup input stream
		Source src = new StreamSource(file);
		log.info("Input "+file.getAbsolutePath());
		// Resulting SAX events (the generated FO) must be piped through
		// to FOP
		Result res = new SAXResult(fop.getDefaultHandler());

		// Step 6: Start XSLT transformation and FOP processing
		transformer.transform(src, res);

		// Clean-up
		out.close();

		log.info("PDF output file : " + outFilePDF.getAbsolutePath());

		return outFilePDF.getAbsolutePath();

	}
	
}
