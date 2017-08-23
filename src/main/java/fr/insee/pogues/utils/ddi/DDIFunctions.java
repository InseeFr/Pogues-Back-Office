package fr.insee.pogues.utils.ddi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class DDIFunctions {

	final static Logger logger = LogManager.getLogger(DDIFunctions.class);

	public static Map<String, String> getDDIControlConstructReferenceListFromFragment(String fragment)
			throws RuntimeException {

		NodeList controlConstructReferenceList = null;
		
		String expression = "/Fragment/Instrument[1]/ControlConstructReference";
		try {
			controlConstructReferenceList = getElementListFromFragmentByXpath(fragment, expression);
			logger.debug("Occurence of ControlConstructReference : " + controlConstructReferenceList.getLength());
		} catch (XPathExpressionException ex) {
			throw new RuntimeException(ex);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (ParserConfigurationException ex) {
			throw new RuntimeException(ex);
		}
		return getMapIdTypeOfObjectFromElementList(controlConstructReferenceList);

	}
	
	public static Map<String, String> getDDIControlConstructReferenceListFromSequenceFragment(String fragment)
			throws RuntimeException {

		NodeList controlConstructReferenceList = null;
		
		String expression = "/Fragment/Sequence[1]/ControlConstructReference";
		try {
			controlConstructReferenceList = getElementListFromFragmentByXpath(fragment, expression);
			logger.debug("Occurence of ControlConstructReference : " + controlConstructReferenceList.getLength());
		} catch (XPathExpressionException ex) {
			throw new RuntimeException(ex);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (ParserConfigurationException ex) {
			throw new RuntimeException(ex);
		}
		return getMapIdTypeOfObjectFromElementList(controlConstructReferenceList);

	}

	public static Map<String, String> getDDIQuestionReferenceIDFromFragment(String fragment) throws RuntimeException {

		NodeList questionReferenceList = null;
		String expression = "/Fragment/QuestionConstruct/r:QuestionReference";
		try {
			questionReferenceList = getElementListFromFragmentByXpath(fragment, expression);
			logger.debug("Occurence of QuestionReference : " + questionReferenceList.getLength());
		} catch (XPathExpressionException ex) {
			throw new RuntimeException(ex);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (ParserConfigurationException ex) {
			throw new RuntimeException(ex);
		}
		return getMapIdTypeOfObjectFromElementList(questionReferenceList);

	}

	public static Map<String, String> getDDISourceParameterReferenceIDFromFragment(String fragment)
			throws RuntimeException {
		NodeList sourceParameterReferenceList = null;
		String expression = "/Fragment/QuestionItem[1]/r:Binding[1]/r:SourceParameterReference[1]";
		try {
			sourceParameterReferenceList = getElementListFromFragmentByXpath(fragment, expression);
			logger.debug("Occurence of SourceParameterReference : " + sourceParameterReferenceList.getLength());
		} catch (XPathExpressionException ex) {
			throw new RuntimeException(ex);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (ParserConfigurationException ex) {
			throw new RuntimeException(ex);
		}
		return getMapIdTypeOfObjectFromElementList(sourceParameterReferenceList);

	}

	public static Map<String, String> getDDITargetParameterReferenceIDFromFragment(String fragment)
			throws RuntimeException {
		NodeList targetParameterReferenceList = null;
		String expression = "/Fragment/QuestionItem[1]/r:Binding[1]/r:TargetParameterReference[1]";
		try {
			targetParameterReferenceList = getElementListFromFragmentByXpath(fragment, expression);
			logger.debug("Occurence of TargetParameterReference : " + targetParameterReferenceList.getLength());
		} catch (XPathExpressionException ex) {
			throw new RuntimeException(ex);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		} catch (ParserConfigurationException ex) {
			throw new RuntimeException(ex);
		}
		return getMapIdTypeOfObjectFromElementList(targetParameterReferenceList);

	}

	public static NodeList getElementListFromFragmentByXpath(String fragment, String xpathExpression)
			throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(fragment));
		Document doc = builder.parse(is);
		Element fragmentElement = doc.getDocumentElement();
		XPathFactory xpf = XPathFactory.newInstance();
		XPath path = xpf.newXPath();
		return (NodeList) path.evaluate(xpathExpression, fragmentElement, XPathConstants.NODESET);

	}

	public static Map<String, String> getMapIdTypeOfObjectFromElementList(NodeList nodeList) {
		Map<String, String> mapIdTypeOfObject = new HashMap<String, String>();
		String id="";
		String type="";
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			NodeList list = n.getChildNodes();
			id = list.item(3).getTextContent();
			type = list.item(7).getTextContent();
			mapIdTypeOfObject.put(id,type);
		}
		return mapIdTypeOfObject;
	}

}
