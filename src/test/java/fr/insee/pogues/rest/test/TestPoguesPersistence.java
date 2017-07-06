package fr.insee.pogues.rest.test;

import com.jayway.restassured.RestAssured;
import fr.insee.pogues.rest.test.utils.RestAssuredConfig;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

/**
 * Test Class used to test the REST Web Service PoguesPersistence, called by
 * Pogues-BO
 * 
 * @author I6VWID
 *
 */
public class TestPoguesPersistence {


	final static Logger logger = Logger.getLogger(TestPoguesPersistence.class);

	/**
	 * Setting up the RestAssured default URI and Authentication
	 */
	@BeforeClass
	public static void setUp() {
        RestAssuredConfig.configure();
	}

    @AfterClass
    public static void tearDown(){
        RestAssured.reset();
    }

	/**
	 * Dummy helloworld test, should return "Hello world"
	 */
	@Test
	public void helloworldTest() {
		logger.debug(
				"Dummy helloworld test : trying to reach /pogues/persistence/helloworld with Status = 200");
		RestAssured.expect().statusCode(200).when().get("/pogues/persistence/helloworld");

	}

	/**
	 * Post Questionnaire Test
	 */
	@Test
	public void postQuestionnaire() {
		logger.debug("Trying to post on /pogues/persistence/questionnaires with Created = 201");
		String json1 = "{\"id\":\"17\", \"owner\":\"D5WQNO\",\"Name\":\"FIRSTQUESTIONNAIRE\"}";
		RestAssured.expect()
				.statusCode(201)
				.when()
				.given()
				.contentType(MediaType.APPLICATION_JSON)
				.body(json1)
				.when()
				.post("/pogues/persistence/questionnaires");

	}

	/**
	 * Put Questionnaire Test
	 */
	@Test
	public void putQuestionnaire() {
		logger.debug(
				"Trying to put a questionnaire on /pogues/persistence/questionnaire with Status = 204");
		String json1 = "{\"id\":\"18\",\"Name\":\"FIRSTQUESTIONNAIRE\"}";
		RestAssured.expect()
				.statusCode(404)
				.when()
				.given()
				.contentType(MediaType.APPLICATION_JSON)
				.body(json1)
				.when()
				.put("/pogues/persistence/questionnaire");

	}

	/**
	 * getQuestionnaireById test
	 */
	@Test
	public void getQuestionnaireById() {
		logger.debug("Trying to reach /pogues/persistence/17 with Status = 200");
		RestAssured.expect().statusCode(200).when().get("/pogues/persistence/questionnaire/17");
	}

	@Test
	public void getQuestionnaireByOwner(){
        logger.debug("Trying to reach /pogues/persistence/questionnaires/search with Status = 200");
        RestAssured.expect()
				.statusCode(200)
				.when()
				.given()
				.param("owner", RestAssuredConfig.jUsername)
				.get("/pogues/persistence/questionnaires/search");
	}

	/**
	 * getQuestionnaireList test
	 */
	@Test
	public void getQuestionnaires() {
		logger.debug("Trying to reach /pogues/persistence/questionnaires with Status = 200");
		RestAssured.expect()
				.statusCode(200)
				.when()
				.get("/pogues/persistence/questionnaires");

	}

	/**
	 * deleteQuestionnaireByID
	 */
	@Test
	public void deleteQuestionnaireByID() {
		logger.debug("Trying to reach /pogues/persistence/questionnaire/17 with Status = 200");
		RestAssured.expect().statusCode(204).when().delete("/pogues/persistence/questionnaire/17");

	}

	/**
	 * deleteQuestionnaireByID
	 */
	@Test
	public void deleteQuestionnaireByID2() {
		logger.debug("Trying to reach /pogues/persistence/questionnaire/18 with Status = 200");
		RestAssured.expect().statusCode(204).when().delete("/pogues/persistence/questionnaire/18");

	}

}
