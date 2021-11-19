package fr.insee.pogues.webservice.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Component
@Path("/")
@Tag(name = "Pogues Public Resources")
public class PublicResources {
	
	final static Logger logger = LogManager.getLogger(PublicResources.class);
	
	@Value("${fr.insee.pogues.authentication}")
    String authentificationType;
	
	@GET
	@Path("/init")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getInit", summary = "Initial properties")
	public Response getProperties() throws Exception {
		JSONObject props = new JSONObject();
		try {
			props.put("authType", authentificationType);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Response.status(HttpStatus.SC_OK).entity(props.toString()).build();
	}

}
