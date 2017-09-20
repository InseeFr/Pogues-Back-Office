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
            value = "Delete Questionnaire from Index",
            notes = "Index a new `Questionnaire`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Not found")
    })
    public Response getEnvironment() throws Exception {
        try {
            JSONObject entity = new JSONObject();
            entity.put("elasticsearch.index", env.getProperty("fr.insee.pogues.elasticsearch.index.name"));
            return Response.ok().entity(entity).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }
}
