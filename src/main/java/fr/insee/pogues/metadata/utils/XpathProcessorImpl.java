package fr.insee.pogues.metadata.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

@Service
public class XpathProcessorImpl implements XpathProcessor {

    final static Logger logger = LogManager.getLogger(XpathProcessor.class);

    public NodeList queryList(String fragment, String xpathExpression) throws Exception {
        try {
            Document node = toDocument(fragment);
            XPathFactory xpf = XPathFactory.newInstance();
            XPath path = xpf.newXPath();
            XPathExpression exp = path.compile(xpathExpression);
            return (NodeList) exp.evaluate(node, XPathConstants.NODESET);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    public NodeList queryList(Node node, String xpathExpression) throws Exception {
        try {
            XPathFactory xpf = XPathFactory.newInstance();
            XPath path = xpf.newXPath();
            XPathExpression exp = path.compile(xpathExpression);
            return (NodeList) exp.evaluate(node, XPathConstants.NODESET);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    public String queryText(Node node, String xpathExpression) throws Exception {
        try {
            XPathFactory xpf = XPathFactory.newInstance();
            XPath path = xpf.newXPath();
            XPathExpression exp = path.compile(xpathExpression);
            return (String) exp.evaluate(node, XPathConstants.STRING);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    public String queryText(String fragment, String xpathExpression) throws Exception {
        try {
            Document node = toDocument(fragment);
            XPathFactory xpf = XPathFactory.newInstance();
            XPath path = xpf.newXPath();
            XPathExpression exp = path.compile(xpathExpression);
            return (String) exp.evaluate(node, XPathConstants.STRING);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }


    public Document toDocument(String xml) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource source = new InputSource(new StringReader(xml));
            return builder.parse(source);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}
