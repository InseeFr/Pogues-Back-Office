package fr.insee.pogues.rest.test;

import javax.ws.rs.core.MediaType;

import com.jayway.restassured.authentication.FormAuthConfig;
import com.jayway.restassured.response.Response;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
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
	@BeforeClass
	public static void setUp() {
		logger.debug("Setting the RestAssured base Uri to http://localhost:8080 : for local tests");
		RestAssured.baseURI = "http://localhost:8080";
        String sessionId;
        sessionId = RestAssured.expect().statusCode(200).log().all()
                .when().get("/").sessionId();
        RestAssured.expect().statusCode(302).log().all()
                .given().param("j_username", "D5WQNO")
                .param("j_password", "D5WQNO").cookie("JSESSIONID", sessionId)
                .post("j_security_check");
        sessionId = RestAssured.expect()
                .statusCode(404)
                .log().all()
                .given().cookie("JSESSIONID", sessionId)
                .when()
                .get().sessionId();
        RestAssured.sessionId = sessionId;
    }

	/**
	 * Dummy helloworld test, should return "Hello world"
	 */
	@Test
	public void helloworldTest() {
		logger.debug(
				"Dummy helloworld test : trying to reach /Pogues-BO/pogues/user/helloworld with Status = 200");
		RestAssured.expect().statusCode(200).contentType(ContentType.TEXT).when()
				.get("/pogues/user/helloworld");

	}
	
		
	/**
	 * getUserID test
	 */
	@Test
	public void getUserID() {
		logger.debug(
				"Trying to reach /Pogues-BO/pogues/user/id with Status = 200");
		RestAssured.expect().statusCode(200).contentType(MediaType.APPLICATION_JSON).when()
				.get("/pogues/user/id");

	}

	/**
	 * getPermissions test
	 */
	@Test
	public void getPermissions() {
		logger.debug(
				"Trying to reach /Pogues-BO/pogues/user/permissions with Status = 200");
        RestAssured.expect()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get("/pogues/user/permissions");

    }

	

}
