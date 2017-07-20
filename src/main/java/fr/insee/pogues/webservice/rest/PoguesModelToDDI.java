package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.transforms.TransformService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Main WebService class of the PoguesModelToDDI service
 * 
 * @author I6VWID
 *
 */
@Path("/PoguesModelToDDI")
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
	})
	public Response transform(@Context final HttpServletRequest request) {
		try {
			StreamingOutput stream = new StreamingOutput() {
				public void write(OutputStream output) throws IOException, WebApplicationException {
					try {
						transformService.transform(request.getInputStream(), output);
					} catch (Exception e) {
						throw new PoguesException(500, e.getMessage(), null);
					}
				}
			};
			return Response.ok(stream).build();
		} catch(Exception e) {
			throw e;
		}
	}
}