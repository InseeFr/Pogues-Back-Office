package fr.insee.pogues.utils.ddi;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
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
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DDIDocumentBuilder {

    private Node itemNode;
    private Node resourcePackageNode;
    private Document packagedDocument;

    public DDIDocumentBuilder() {
        try {
            packagedDocument = buildEnvelope();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public DDIDocumentBuilder build() {
        if (null != itemNode) {
            packagedDocument.getDocumentElement()
                    .getElementsByTagName("r:DataCollection")
                    .item(0)
                    .appendChild(itemNode);
        }
        if (null != resourcePackageNode) {
            packagedDocument.getDocumentElement()
                    .appendChild(resourcePackageNode);
        }
        return this;
    }

    public DDIDocumentBuilder buildItemDocument(String rootId, Map<String, String> references) throws Exception {
        itemNode = buildNode(packagedDocument, rootId, references);
        return this;
    }

    public DDIDocumentBuilder buildResourcePackageDocument(String rootId, Map<String, String> references) throws Exception {
        resourcePackageNode = buildNode(packagedDocument, rootId, references);
        return this;
    }

    public Document getDocument() {
        return packagedDocument;
    }

    public String toString() {
        StringWriter stringWriter = new StringWriter();
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']", packagedDocument,
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
            transformer.transform(new DOMSource(packagedDocument), streamResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    private Node buildNode(Document document, String rootId, Map<String, String> references) throws Exception {
        Node node = getNode(references.get(rootId), document);
        walk(node, document, references);
        return node;
    }

    private Document buildEnvelope() throws Exception {
        URL url = Resources.getResource("transforms/templates/ddi-enveloppe.xml");
        String fragment = FileUtils.readFileToString(new File(url.toURI()), StandardCharsets.UTF_8.name());
        return getDocument(fragment);
    }

    private void walk(Node root, Document document, Map<String, String> references) throws Exception {
        NodeList rootNodes = root.getChildNodes();
        for (int i = 0; i < rootNodes.getLength(); i++) {
            Node n = rootNodes.item(i);
            if (n.getNodeName().contains("Reference")) {
                String fragment = references.get(getId(n));
                if (null != fragment) {
                    Node child = getNode(fragment, document);
                    root.appendChild(child);
                    walk(child, document, references);
                }
            }
        }
    }

    private Node getNode(String fragment, Document doc) throws Exception {
        Element node = getDocument(fragment).getDocumentElement();
        Node newNode = node.cloneNode(true);
        // Transfer ownership of the new node into the destination document
        doc.adoptNode(newNode);
        return newNode;
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


}
