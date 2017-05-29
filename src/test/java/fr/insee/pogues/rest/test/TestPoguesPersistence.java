package fr.insee.pogues.rest.test;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;

/**
 * Test Class used to test the REST Web Service PoguesPersistence, called by Pogues-BO
 * 
 * @author I6VWID
 *
 */
public class TestPoguesPersistence {

	final static Logger logger = Logger.getLogger(TestPoguesPersistence.class);

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
				"Dummy helloworld test : trying to reach /Pogues-BO/pogues/persistence/helloworld with Status = 200");
		RestAssured.expect().statusCode(200).contentType(ContentType.TEXT).when()
				.get("/Pogues-BO/pogues/persistence/helloworld");

	}
	
		
	/**
	 * getQuestionnaireById test
	 */
	@Test
	public void getQuestionnaireById() {
		logger.debug(
				"Trying to reach /Pogues-BO/=pogues/persistence/3 with Status = 200");
		RestAssured.expect().statusCode(200).contentType(MediaType.APPLICATION_JSON).when()
				.get("/Pogues-BO/pogues/persistence/questionnaire/izqyt9iq");

	}


	/**
	 * getQuestionnaireList test
	 */
	@Test
	public void getQuestionnaires() {
		logger.debug(
				"Trying to reach /Pogues-BO/pogues/persistence/questionnaires with Status = 200");
		RestAssured.expect().statusCode(200).contentType(MediaType.APPLICATION_JSON).when()
				.get("/Pogues-BO/pogues/persistence/questionnaires");

	}

	/**
	 * Post Questionnaire Test
	 */
	@Test
	public void postQuestionnaire() {
		logger.debug(
				"Trying to post on /Pogues-BO/pogues/persistence/questionnaire with Created = 201");
		String json1 = "{\"id\":\"17\",\"Name\":\"FIRSTQUESTIONNAIRE\"}";
		RestAssured.expect().statusCode(201).contentType(MediaType.APPLICATION_JSON).when().
		given().contentType(MediaType.APPLICATION_JSON).body(json1).when().post("/Pogues-BO/pogues/persistence/questionnaires");

	}
 
	/**
	 * Post Questionnaire Test
	 */
	@Test
	public void putQuestionnaire() {
		logger.debug(
				"Trying to put a questionnaire on /Pogues-BO/pogues/persistence/questionnaire with Created = 201");
		String json1 = "{\"id\":\"18\",\"Name\":\"FIRSTQUESTIONNAIRE\"}";
		RestAssured.expect().statusCode(201).contentType(MediaType.APPLICATION_JSON).when().
		given().contentType(MediaType.APPLICATION_JSON).body(json1).when().put("/Pogues-BO/pogues/persistence/questionnaire");

	}

	

}
