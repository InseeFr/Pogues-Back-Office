package fr.insee.pogues.webservice.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("env")
@Api(value = "Pogues Environment")
public class PoguesEnvironment {

    private final static Logger logger = LogManager.getLogger(PoguesEnvironment.class);

    @Autowired
    Environment env;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Get pogues back office environment",
            notes = "This will return a safe (no secrets) projection of the current backend environment"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found")
    })
    public Response getEnvironment() throws Exception {
        try {
            JSONObject entity = new JSONObject();
            entity.put("Swagger Host", env.getProperty("fr.insee.pogues.api.host"));
            entity.put("Swagger Name", env.getProperty("fr.insee.pogues.api.name"));   
            entity.put("Swagger Scheme", env.getProperty("fr.insee.pogues.api.scheme"));
            entity.put("Database", env.getProperty("fr.insee.pogues.persistence.database.host"));
            entity.put("LDAP", env.getProperty("fr.insee.pogues.permission.ldap.hostname"));
            entity.put("Metadata services", env.getProperty("fr.insee.pogues.api.remote.metadata.url"));
            entity.put("Vizualisation service", env.getProperty("fr.insee.pogues.api.remote.stromae.vis.url"));
            entity.put("Eno Webservice", env.getProperty("fr.insee.pogues.api.remote.eno.host"));
            return Response.ok().entity(entity).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }
}
