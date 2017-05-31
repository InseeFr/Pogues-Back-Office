package fr.insee.pogues.webservice.rest;

import java.util.HashMap;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import fr.insee.pogues.user.service.UserService;

/**
 * WebService class for the Identity Service
 * 
 * 
 * @author I6VWID
 * 
 *         schemes: - http
 * 
 *         consumes: - application/json
 * 
 *         produces: - application/json
 *
 */
@Singleton
@Path("/user")
public class PoguesUser {

	
	@Context
    HttpServletRequest request;
	
	final static Logger logger = Logger.getLogger(PoguesUser.class);

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
	 * Get the user id of the connected user
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
	@Path("id")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getID() {
		UserService service = new UserService(request);
		String jsonResultat = service.getUserID();
		if ((jsonResultat == null) || (jsonResultat.length() == 0)) {
    		logger.info("No user connected , returning NOT_FOUND response");
    		return Response.status(Status.NOT_FOUND).build();
    	}
		return Response.status(Status.OK).entity(jsonResultat).build();
	}
	
	/**
	 * Get the user attribute of the connected user
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
	@Path("attributes")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getAttribute() {
		UserService service = new UserService(request);
		String jsonResultat = service.getNameAndPermission();
		if ((jsonResultat == null) || (jsonResultat.length() == 0)) {
    		logger.info("No user connected , returning NOT_FOUND response");
    		return Response.status(Status.NOT_FOUND).build();
    	}
		return Response.status(Status.OK).entity(jsonResultat).build();
	}
	
	

	
	

	

}
