package fr.insee.pogues.webservice.rest;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import fr.insee.pogues.api.remote.metadata.service.RepositoryQuestionnairesService;

/**
 * Main WebService class of the RMeSAPI service
 * 
 * @author I6VWID
 *
 */
@Path("/RMeSAPI")
public class RMeSAPI {

	final static Logger logger = Logger.getLogger(RMeSAPI.class);

	/**
	 * Dummy GET Helloworld used in unit tests
	 * 
	 * @return "Hello world" as a String
	 */
	@GET
	@Path("helloworld")
	public String helloworld() {
		return "Hello world";
	}

	
	/**
	 * Gets the questionnaire with id {id}
	 * 
	 * @param name:
	 *            id
	 * 
	 *            in: path
	 * 
	 *            description: The identifier of the questionnaire to retrieve
	 * 
	 *            type: string
	 * 
	 * 
	 * @return Response code
	 * 
	 *         200: description: Successful response
	 * 
	 *         404: description: Questionnaire not found
	 *
	 */
	@GET
	@Path("questionnaire/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getQuestionnaire(@PathParam(value = "id") String id) {
		RepositoryQuestionnairesService service = new RepositoryQuestionnairesService();
		String jsonResultat = null;
		try {
			jsonResultat = service.getJSONQuestionnaireByID(id);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ((jsonResultat == null) || (jsonResultat.length() == 0)) {
    		logger.info("Questionnaire not found, returning NOT_FOUND response");
    		return Response.status(Status.NOT_FOUND).build();
    	}
		return Response.status(Status.OK).entity(jsonResultat).build();
	}

	

}