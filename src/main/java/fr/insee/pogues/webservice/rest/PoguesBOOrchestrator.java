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


}