package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.transforms.TransformService;
import io.swagger.annotations.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 * Main WebService class of the PoguesModelToDDI service
 *
 * @author I6VWID
 *
 */
@Path("/PoguesModelToDDI")
@Api(value = "PoguesModelToDDI", authorizations = {
		@Authorization(value="sampleoauth", scopes = {})
})
public class PoguesModelToDDI {

	final static Logger logger = Logger.getLogger(PoguesModelToDDI.class);

	@Autowired
	private TransformService transformService;

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_XML)
	@ApiOperation(
			value = "Get DDI From Pogues Model",
			notes = "Get Transformed DDI document from Pogues XML"
	)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Error")
	})
    @ApiImplicitParams(value={
            @ApiImplicitParam(name="poguesModel", value="XML representation of the Pogues Model", paramType="body")
    })
	public Response transform(@Context final HttpServletRequest request) {
		try {
			StreamingOutput stream = output -> {
                try {
                    transformService.transform(request.getInputStream(), output);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new PoguesException(500, e.getMessage(), null);
                }
            };
			return Response.ok(stream).build();
		} catch(Exception e) {
			throw e;
		}
	}
}