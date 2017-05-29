package fr.insee.pogues.webservice.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

/**
 * Main WebService class of the PoguesBOOrchestrator
 * 
 * @author I6VWID
 *
 */
@Path("/PoguesBOOrchestrator")
public class PoguesBOOrchestrator {

	final static Logger logger = Logger.getLogger(PoguesBOOrchestrator.class);

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

//	/**
//	 * Main WS method called to generate a questionnaire from an input xml DDI
//	 * file
//	 * 
//	 * @param uploadedInputStream
//	 *            The inputStream that will be used to write the file locally
//	 * @param fileDetail
//	 *            The proper file that was sent to the method
//	 * @return a Response Object (.ok) with the created generator if everything
//	 *         went as expected OR a Response Object (.ok) with the error that
//	 *         occured during the generation if something wrong happened
//	 */
//	@POST
//	@Path("Generation")
//	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	public Response generation(@FormDataParam("file") InputStream uploadedInputStream,
//			@FormDataParam("file") FormDataContentDisposition fileDetail,
//			@FormDataParam("parameters") InputStream parametersInputStream,
//			@FormDataParam("parameters") FormDataContentDisposition parametersDetail) {
//
//	
//		logger.debug("WebService called with parameter file : " + fileDetail);
//		logger.debug("WebService called with parameter parameters : " + parametersDetail);
//		try {
//			
//
//			return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
//					.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"").build();
//
//		} catch (Exception e) {
//
//			logger.error("Error during the generation :" + e.getMessage());
//			logger.error(e, e);
//
//			return Response.ok("Error during the generation : " + e.toString()).build();
//		}
//	}



}