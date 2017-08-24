package fr.insee.pogues.webservice.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * Main WebService class of the StromaePublishing service
 * 
 * @author I6VWID
 *
 */
@Path("/StromaePublishing")
@Api(value = "Pogues Stromae Publishing")
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
	@ApiOperation(value = "publishQuestionnaire",
    notes = "Publish a questionnaire on the vizualisation platform",
    response = String.class)
	public Response publishQuestionnaire(String jsonContent) {

		return Response.status(Status.NOT_IMPLEMENTED).build();

	}
	
	


}