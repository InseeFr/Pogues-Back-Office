package fr.insee.pogues.rest.test;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

/**
 * Test Class used to test the REST Web Service PoguesUser, called by Pogues UI
 * 
 * @author I6VWID
 *
 */
public class TestPoguesUser {

	final static Logger logger = Logger.getLogger(TestPoguesUser.class);

	/**
	 * Setting up the RestAssured default URI
	 */
	@Before
	public void setUp() {
		logger.debug("Setting the RestAssured base Uri to http://localhost:8080 : for local tests");
		RestAssured.baseURI = "http://localhost:8080";
	}

	/**
	 * Dummy helloworld test, should return "Hello world"
	 */
	@Test
	public void helloworldTest() {
		logger.debug(
				"Dummy helloworld test : trying to reach /Pogues-BO/pogues/user/helloworld with Status = 200");
		RestAssured.expect().statusCode(200).contentType(ContentType.TEXT).when()
				.get("/Pogues-BO/pogues/user/helloworld");

	}
	
		
	/**
	 * getUserID test
	 */
	@Test
	public void getUserID() {
		logger.debug(
				"Trying to reach /Pogues-BO/pogues/user/id with Status = 200");
		RestAssured.expect().statusCode(200).contentType(MediaType.APPLICATION_JSON).when()
				.get("/Pogues-BO/pogues/user/id");

	}

	

}
