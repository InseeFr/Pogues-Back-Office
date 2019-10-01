package fr.insee.pogues.transforms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2PDFGenerator;
import fr.insee.eno.postprocessing.PDFEditStructurePagesPostprocessor;
import fr.insee.eno.postprocessing.PDFInsertAccompanyingMailsPostprocessor;
import fr.insee.eno.postprocessing.PDFInsertCoverPagePostprocessor;
import fr.insee.eno.postprocessing.PDFInsertEndQuestionPostprocessor;
import fr.insee.eno.postprocessing.PDFMailingPostprocessor;
import fr.insee.eno.postprocessing.PDFSpecificTreatmentPostprocessor;
import fr.insee.eno.postprocessing.PDFTableColumnPostprocessorFake;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;

@Service
public class DDIToFOImpl implements DDIToFO {

	final static Logger logger = LogManager.getLogger(DDIToFOImpl.class);

	@Override
	public void transform(InputStream input, OutputStream output, Map<String, Object> params, String surveyName)
			throws Exception {
		logger.debug("Eno transformation");
		if (null == input) {
			throw new NullPointerException("Null input");
		}
		if (null == output) {
			throw new NullPointerException("Null output");
		}
		String odt = transform(input, params, surveyName);
		logger.debug("Eno transformation finished");
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
		enoInput = File.createTempFile("eno", ".xml");
		FileUtils.writeStringToFile(enoInput, input, StandardCharsets.UTF_8);
		return transform(enoInput, params, surveyName);
	}

	private String transform(File file, Map<String, Object> params, String surveyName) throws Exception {

		GenerationService genServiceDDI2PDF = new GenerationService(new DDIPreprocessor(), new DDI2PDFGenerator(),
				new Postprocessor[] { new PDFMailingPostprocessor(),
						new PDFTableColumnPostprocessorFake(),
						new PDFInsertEndQuestionPostprocessor(),
						new PDFEditStructurePagesPostprocessor(),
						new PDFSpecificTreatmentPostprocessor(),
						new PDFInsertCoverPagePostprocessor(),
						new PDFInsertAccompanyingMailsPostprocessor()
				/*
				 * new NoopPostprocessor()
				 * 
				 */ });

		if (params.size() > 0) {
			// Create Document from default xml param file
			Document document;
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			document = builder.parse(new File(DDIToFOImpl.class.getResource("/pdf/parameters.xml").toURI()));

			// Xpath to modify params value
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xPath = xPathfactory.newXPath();
			if (params.get("columns") != null) {
				NodeList myNodeList = (NodeList) xPath.compile("//Parameters/Format/Columns/text()").evaluate(document,
						XPathConstants.NODESET);
				myNodeList.item(0).setNodeValue((String) params.get("columns"));
			}
			if (params.get("orientation") != null) {
				NodeList myNodeList = (NodeList) xPath.compile("//Parameters/Format/Orientation/text()")
						.evaluate(document, XPathConstants.NODESET);
				myNodeList.item(0).setNodeValue((String) params.get("orientation"));
			}
			if (params.get("capture") != null) {
				NodeList myNodeList = (NodeList) xPath.compile("//Parameters/Capture/Numeric/text()").evaluate(document,
						XPathConstants.NODESET);
				myNodeList.item(0).setNodeValue((String) params.get("capture"));
			}
			if (params.get("studyunit") != null) {
				NodeList myNodeList = (NodeList) xPath.compile("//Parameters/StudyUnit/text()").evaluate(document,
						XPathConstants.NODESET);
				myNodeList.item(0).setNodeValue((String) params.get("studyunit"));
			}
			if (params.get("timequestion") != null) {
				NodeList myNodeList = (NodeList) xPath.compile("//Parameters/EndQuestion/ResponseTimeQuestion/text()").evaluate(document,
						XPathConstants.NODESET);
				myNodeList.item(0).setNodeValue((String) params.get("timequestion"));
			}
			// Create InputStream from Dom document
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Source xmlSource = new DOMSource(document);
			Result outputTarget = new StreamResult(outputStream);
			TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
			InputStream parametersIS = new ByteArrayInputStream(outputStream.toByteArray());

			// set parameter to the Generation service
			genServiceDDI2PDF.setParameters(parametersIS);
		}
		File conf = new File(DDIToFOImpl.class.getResource("/pdf/fop.xconf").toURI());

		logger.info("Conf file : " + conf.getAbsolutePath());
		File outputFO = genServiceDDI2PDF.generateQuestionnaire(file, surveyName);
		logger.info("FO output file : " + outputFO.getAbsolutePath());
		
		return FileUtils.readFileToString(outputFO, StandardCharsets.UTF_8);
	}
}
