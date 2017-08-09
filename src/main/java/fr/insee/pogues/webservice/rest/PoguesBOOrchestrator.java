package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.transforms.DDIToXForm;
import fr.insee.pogues.transforms.JSONToXML;
import fr.insee.pogues.transforms.PipeLine;
import fr.insee.pogues.transforms.XMLToDDIImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Main WebService class of the PoguesBOOrchestrator
 * 
 * @author I6VWID
 *
 */
@Path("/transform")
public class PoguesBOOrchestrator {

	final static Logger logger = Logger.getLogger(PoguesBOOrchestrator.class);

	@Autowired
	JSONToXML jsonToXML;

	@Autowired
	XMLToDDIImpl xmlToDDI;

	@Autowired
	DDIToXForm ddiToXForm;

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

	@POST
	@Path("test")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response test(@Context final HttpServletRequest request) throws Exception{
		PipeLine pipeline = new PipeLine();
		try {
			String response = pipeline
					.from(request.getInputStream())
					.map(jsonToXML::transform)
					.map(xmlToDDI::transform)
//					.map(ddiToXForm::transform)
					.transform();
			return Response.ok(response).build();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
