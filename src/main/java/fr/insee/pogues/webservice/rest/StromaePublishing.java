package fr.insee.pogues.webservice.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Main WebService class of the StromaePublishing service
 * 
 * @author I6VWID
 *
 */
@Path("/StromaePublishing")
@Tag(name = "Pogues Stromae Publishing")
@Hidden
public class StromaePublishing {

	final static Logger logger = LogManager.getLogger(StromaePublishing.class);

	/**
	 * Publish a questionnaire on the vizualisation platform
	 * 
	 * @param name:
	 *            questionnaire
	 * 
	 *            in: body
	 * 
	 *            description: The questionnaire to publish
	 * 
	 *            required: true
	 *
	 * 
	 * @return 200: description: The questionnaire was published
	 *
	 *         400: description: Malformed object in the query
	 * 
	 *         401: description: The client is not authorized for this operation
	 */
	@PUT
	@Path("questionnaire")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response publishQuestionnaire(String jsonContent) {

		return Response.status(Status.NOT_IMPLEMENTED).build();

	}
	
	


}