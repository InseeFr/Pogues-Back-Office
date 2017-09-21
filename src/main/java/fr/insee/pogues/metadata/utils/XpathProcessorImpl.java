package fr.insee.pogues.metadata.utils;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;

@Service
public class XpathProcessorImpl implements XpathProcessor {

    @Override
    public NodeList queryList(String fragment, String xpathExpression) throws Exception {
        Document node = toDocument(fragment);
        XPathFactory xpf = XPathFactory.newInstance();
        XPath path = xpf.newXPath();
        XPathExpression exp = path.compile(xpathExpression);
        return (NodeList) exp.evaluate(node, XPathConstants.NODESET);
    }

    @Override
    public NodeList queryList(Node node, String xpathExpression) throws Exception {
        XPathFactory xpf = XPathFactory.newInstance();
        XPath path = xpf.newXPath();
        XPathExpression exp = path.compile(xpathExpression);
        return (NodeList) exp.evaluate(node, XPathConstants.NODESET);
    }

    @Override
    public String queryString(String fragment, String xpathExpression) throws Exception {
        return queryString(toDocument(fragment), xpathExpression);
    }

    @Override
    public String queryString(Node node, String xpathExpression) throws Exception {
        StringBuilder sb = new StringBuilder();
        NodeList nodes = queryList(node, xpathExpression);
        for(int i = 0; i < nodes.getLength(); i++) {
            sb.append(toString(nodes.item(i)));
        }
        return sb.toString();
    }

    @Override
    public String queryText(Node node, String xpathExpression) throws Exception {
        XPathFactory xpf = XPathFactory.newInstance();
        XPath path = xpf.newXPath();
        XPathExpression exp = path.compile(xpathExpression);
        return (String) exp.evaluate(node, XPathConstants.STRING);
    }

    @Override
    public String queryText(String fragment, String xpathExpression) throws Exception {
        Document node = toDocument(fragment);
        XPathFactory xpf = XPathFactory.newInstance();
        XPath path = xpf.newXPath();
        XPathExpression exp = path.compile(xpathExpression);
        return (String) exp.evaluate(node, XPathConstants.STRING);
    }

    @Override
    public Document toDocument(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource source = new InputSource(new StringReader(xml));
        return builder.parse(source);
    }

    @Override
    public String toString(Node node) throws TransformerException {
        StringWriter buf = new StringWriter();
        Transformer xForm = TransformerFactory.newInstance().newTransformer();
        xForm.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        xForm.setOutputProperty(OutputKeys.INDENT, "yes");
        xForm.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        xForm.transform(new DOMSource(node), new StreamResult(buf));
        return (buf.toString());
    }
}
