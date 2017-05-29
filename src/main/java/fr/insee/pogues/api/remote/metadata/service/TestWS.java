package fr.insee.pogues.api.remote.metadata.service;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public class TestWS {

	public static void main(String[] args) {
		
		RepositoryQuestionnairesService service = new RepositoryQuestionnairesService();
		try {
			String ddi = service.getDDIQuestionnaireByID("d3ed6638-583f-4ce0-b677-e31e6df89921");
		} catch (XPathExpressionException | SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
