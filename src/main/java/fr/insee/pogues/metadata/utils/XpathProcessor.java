package fr.insee.pogues.metadata.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface XpathProcessor {

    /**
     * Query a list of nodes using xpath
     * @param fragment - XML fragment as string
     * @param xpathExpression - Valid xpath expression
     * @return - A list of DOM Node elements
     * @throws Exception
     */
    NodeList queryList(String fragment, String xpathExpression) throws Exception;

    /**
     * Query a list of nodes using xpath
     * @param node - XML fragment as DOM Node
     * @param xpathExpression - Valid xpath expression
     * @return - A list of DOM Node elements
     * @throws Exception
     */
    NodeList queryList(Node node, String xpathExpression) throws Exception;

    /**
     * Query String representation of a XML fragment using xpath
     * @param fragment  - XML fragment as string
     * @param xpathExpression - Valid xpath expression
     * @return - An XML fragment represented as a String
     * @throws Exception
     */
    String queryString(String fragment, String xpathExpression) throws Exception;

    /**
     * Query String representation of a XML fragment using xpath
     * @param node - XML fragment as DOM Node
     * @param xpathExpression - Valid xpath expression
     * @return - An XML fragment represented as a String
     * @throws Exception
     */
    String queryString(Node node, String xpathExpression) throws Exception;

    /**
     * Query a text element (ie value) from a XML fragment using xpath
     * @param fragment  - XML fragment as string
     * @param xpathExpression - Valid xpath expression
     * @return - A text element (ie value)
     * @throws Exception
     */
    String queryText(String fragment, String xpathExpression) throws Exception;

    /**
     * Query a text element (ie value) from a XML fragment using xpath
     * @param node - XML fragment as DOM Node
     * @param xpathExpression - Valid xpath expression
     * @return - A text element (ie value)
     * @throws Exception
     */
    String queryText(Node node, String xpathExpression) throws Exception;

    /**
     * Converts an XML fragment represented as a String to a DOM Node representation
     * @param xml
     * @return
     * @throws Exception
     */
    Document toDocument(String xml) throws Exception;

    /**
     * Converts an XML fragment represented as a DOM Node element to its String representation
     * @param node
     * @return
     * @throws Exception
     */
    String toString(Node node) throws Exception;
}
