package fr.insee.pogues.utils.ddi;

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
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public class DDITreeBuilder {

    public static String buildTree(String root, Map<String, String> references) throws Exception {
        Document ddiDoc = getDocument(root);
        Element racine = ddiDoc.getDocumentElement();
        NodeList rootNodes = racine.getChildNodes();
        for (int i = 0; i < rootNodes.getLength(); i++) {
            Node rootNode = rootNodes.item(i);
            if (rootNode.getNodeName().contains("Reference")) {
                String idReference = getId(rootNode);
                String ddiChild = references.get(idReference);
                // TODO why do we need this check ??
                if (ddiChild != null) {
                    ddiChild = buildTree(ddiChild, references);
                    Node ddiChildNode = getNode(ddiChild, ddiDoc);
                    rootNode.getParentNode().appendChild(ddiChildNode);
                    rootNode.getParentNode().removeChild(rootNode);
                }
            }
        }
        return beautifyDDI(ddiDoc);
    }

    private static String beautifyDDI(Object obj) {
        StringWriter stringWriter = new StringWriter();
        Document document;
        try {
            if (obj instanceof String) {
                document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(new InputSource(new ByteArrayInputStream(((String) obj).getBytes("utf-8"))));
            } else if (obj instanceof Document) {
                document = (Document) obj;
            } else
                return null;

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

    private static Node getNode(String ddi, Document doc)throws Exception  {
        Element node = getDocument(ddi).getDocumentElement();
        Node newNode = node.cloneNode(true);
        // Transfer ownership of the new node into the destination document
        doc.adoptNode(newNode);
        return newNode;
    }

    private static String getId(Node refNode) throws Exception{
        NodeList refChildren = refNode.getChildNodes();
        for (int i = 0; i < refChildren.getLength(); i++) {
            if (refChildren.item(i).getNodeName().equals("r:ID")) {
                return refChildren.item(i).getTextContent();
            }
        }
        throw new Exception("No reference found in node");
    }

    private static Document getDocument(String document) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        if (null == document || document.isEmpty()) {
            return builder.newDocument();
        }
        InputSource ddiSource = new InputSource(new StringReader(document));
        return builder.parse(ddiSource);
    }

}
