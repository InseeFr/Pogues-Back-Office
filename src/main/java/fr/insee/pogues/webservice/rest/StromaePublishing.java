package fr.insee.pogues.webservice.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.log4j.Logger;

/**
 * Main WebService class of the StromaePublishing service
 * 
 * @author I6VWID
 *
 */
@Path("/StromaePublishing")
public class StromaePublishing {

	final static Logger logger = Logger.getLogger(StromaePublishing.class);

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