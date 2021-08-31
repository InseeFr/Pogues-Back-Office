package fr.insee.pogues.webservice.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Component
@Path("env")
@Tag(name = "Pogues Environment")
public class PoguesEnvironment {

	private final static Logger logger = LogManager.getLogger(PoguesEnvironment.class);

	@Autowired
	Environment env;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(operationId = "getEnvironment", summary = "Get pogues back office environment")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "404", description = "Not found") })
	public Response getEnvironment() throws Exception {
		try {
			JSONObject entity = new JSONObject();
			entity.put("Swagger Host", env.getProperty("fr.insee.pogues.api.host"));
			entity.put("Swagger Name", env.getProperty("fr.insee.pogues.api.name"));
			entity.put("Swagger Scheme", env.getProperty("fr.insee.pogues.api.scheme"));
			entity.put("Database", env.getProperty("fr.insee.pogues.persistence.database.host"));
			entity.put("LDAP", env.getProperty("fr.insee.pogues.permission.ldap.hostname"));
			entity.put("Metadata services", env.getProperty("fr.insee.pogues.api.remote.metadata.url"));
			entity.put("Stromae", env.getProperty("fr.insee.pogues.api.remote.stromae.host"));
			entity.put("Eno Webservice", env.getProperty("fr.insee.pogues.api.remote.eno.host"));
			return Response.ok().entity(entity).build();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

	}
}
