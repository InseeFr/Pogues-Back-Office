package fr.insee.pogues.rest.test;

import com.jayway.restassured.RestAssured;
import fr.insee.pogues.rest.test.utils.RestAssuredConfig;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Test Class used to test the external REST Web Service RMéS API, called by Pogues-BO
 * 
 * @author I6VWID
 *
 */
public class TestRMeSAPI {

	final static Logger logger = Logger.getLogger(TestRMeSAPI.class);

	/**
	 * Setting up the RestAssured default URI
	 */
	@BeforeClass
	public void setUp() {
		RestAssuredConfig.configure();
	}

	@AfterClass
	public static void tearDown(){
		RestAssured.reset();
	}



}
