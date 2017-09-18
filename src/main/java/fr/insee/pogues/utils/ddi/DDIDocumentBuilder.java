package fr.insee.pogues.utils.ddi;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public class DDIDocumentBuilder {

    private Document document;

    public DDIDocumentBuilder() {
        this.document = buildDocument();
    }

    private Node getNode(String fragment, Document doc) throws Exception {
        Element node = getDocument(fragment).getDocumentElement();
        Node newNode = node.cloneNode(true);
        // Transfer ownership of the new node into the destination document
        doc.adoptNode(newNode);
        return newNode;
    }

    private static String getId(Node refNode) throws Exception {
        NodeList refChildren = refNode.getChildNodes();
        for (int i = 0; i < refChildren.getLength(); i++) {
            if (refChildren.item(i).getNodeName().equals("r:ID")) {
                return refChildren.item(i).getTextContent();
            }
        }
        throw new Exception("No reference found in node");
    }

    private Document getDocument(String fragment) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        if (null == fragment || fragment.isEmpty()) {
            return builder.newDocument();
        }
        InputSource ddiSource = new InputSource(new StringReader(fragment));
        return builder.parse(ddiSource);
    }

    private Document buildDocument() {
        Document xmlDoc = new DocumentImpl();
        Element root = xmlDoc.createElement("DDIInstance");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:pr", "ddi:ddiprofile:3_2");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:c", "ddi:conceptualcomponent:3_2");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:d", "ddi:datacollection:3_2");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:g", "ddi:group:3_2");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:cm", "ddi:comparative:3_2");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:l", "ddi:logicalproduct:3_2");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xhtml", "http://www.w3.org/1999/xhtml");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:pogues", "http://xml.insee.fr/schema/applis/pogues");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:r", "ddi:reusable:3_2");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:s", "ddi:studyunit:3_2");
        Element agency = xmlDoc.createElement("r:Agency");
        agency.appendChild(xmlDoc.createTextNode("fr.insee"));
        Element id = xmlDoc.createElement("r:ID");
        root.appendChild(id);
        Element version = xmlDoc.createElement("r:Version");
        root.appendChild(version);
        Element citation = xmlDoc.createElement("r:Citation");
        root.appendChild(citation);
        Element title = xmlDoc.createElement("r:Title");
        citation.appendChild(title);
        root.appendChild(agency);
        root.appendChild(id);
        root.appendChild(version);
        root.appendChild(citation);
        xmlDoc.appendChild(root);
        Element studyUnit = xmlDoc.createElement("r:StudyUnit");
        root.appendChild(studyUnit);
        return xmlDoc;
    }

    private void walk(Node root, Document doc, Map<String, String> references) throws Exception {
        NodeList rootNodes = root.getChildNodes();
        for (int i = 0; i < rootNodes.getLength(); i++) {
            Node n = rootNodes.item(i);
            if (n.getNodeName().contains("Reference")) {
                String fragment = references.get(getId(n));
                if (null != fragment) {
                    Node child = getNode(fragment, doc);
                    root.appendChild(child);
                    walk(child, doc, references);
                }
            }
        }
    }


    public DDIDocumentBuilder build(String rootId, Map<String, String> references) throws Exception {
        Node ddiInstrument = getDocument(references.get(rootId)).getFirstChild();
        document.adoptNode(ddiInstrument);
        walk(ddiInstrument, document, references);
        document.getDocumentElement().getElementsByTagName("r:StudyUnit").item(0).appendChild(ddiInstrument);
        return this;
    }

    public String toString() {
        StringWriter stringWriter = new StringWriter();
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", document,
                    XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node node = nodeList.item(i);
                node.getParentNode().removeChild(node);
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            StreamResult streamResult = new StreamResult(stringWriter);
            transformer.transform(new DOMSource(document), streamResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    public Document getDocument() {
        return document;
    }
}
