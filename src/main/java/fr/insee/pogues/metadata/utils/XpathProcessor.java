package fr.insee.pogues.metadata.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface XpathProcessor {

    NodeList queryList(String fragment, String xpathExpression) throws Exception;
    NodeList queryList(Node node, String xpathExpression) throws Exception;
    String queryText(String fragment, String xpathExpression) throws Exception;
    String queryText(Node node, String xpathExpression) throws Exception;
    Document toDocument(String xml) throws Exception;
}
