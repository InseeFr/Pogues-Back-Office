package fr.insee.pogues.rest.test;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

/**
 * Test Class used to test the external REST Web Service Eno-WS, called by Pogues-BO
 * 
 * @author I6VWID
 *
 */
public class TestEnoWS {

	final static Logger logger = Logger.getLogger(TestEnoWS.class);

	/**
	 * Setting up the RestAssured default URI
	 */
	@Before
	public void setUp() {
		logger.debug("Setting the RestAssured base Uri to http://localhost:8080 : for local tests");
		RestAssured.baseURI = "http://localhost:8080";
		// RestAssured.basePath="http://localhost:8080";
	}

	/**
	 * Dummy helloworld test, should return "Hello world"
	 */
	@Test
	public void helloworldTest() {
		logger.debug(
				"Dummy helloworld test : trying to reach /Pogues-BO/Main/Service/helloworld with Status = 200");
		RestAssured.expect().statusCode(200).contentType(ContentType.TEXT).when()
				.get("/Pogues-BO/Main/Service/helloworld");

	}


}
