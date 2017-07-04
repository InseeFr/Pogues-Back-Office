package fr.insee.pogues.rest.test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static com.jayway.restassured.RestAssured.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


/**
 * Test Class used to test the REST Web Service PoguesUser, called by Pogues UI
 *
 * @author I6VWID
 */
public class TestPoguesUser {

    private static String jUsername = "D5WQNO";
    private static String jPassword = "D5WQNO";

    final static Logger logger = Logger.getLogger(TestPoguesUser.class);

    /**
     * Setting up the RestAssured default URI
     */
    @BeforeClass
    public static void setUp() {
        logger.debug("Setting the RestAssured base Uri to http://localhost:8080 : for local tests");
        RestAssured.baseURI = "http://localhost:8080";
        /* All this boilerplate thing to handle Form auth with tomcat */
        String sessionId;
        sessionId = expect().statusCode(200).log().all()
                .when().get("/").sessionId();
        expect().statusCode(302).log().all()
                .given().param("j_username", jUsername)
                .param("j_password", jPassword).cookie("JSESSIONID", sessionId)
                .post("j_security_check");
        sessionId = expect()
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
        expect().statusCode(200).contentType(ContentType.TEXT).when()
                .get("/pogues/user/helloworld");

    }


    /**
     * getUserID test
     */
    @Test
    public void getUserID() {
        logger.debug(
                "Trying to reach /Pogues-BO/pogues/user/id with Status = 200");
        String id = expect()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get("/pogues/user/id")
                .body()
                .jsonPath()
                .get("id");
        assertEquals(id, jUsername);
    }

    /**
     * getPermissions test
     */
    @Test
    public void getPermissions() {
        logger.debug(
                "Trying to reach /Pogues-BO/pogues/user/permissions with Status = 200");
        int listSize = expect()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get("/pogues/user/permissions")
                .body()
                .jsonPath()
                .getList("$")
                .size();
        assertNotEquals(listSize, 0);
    }


}
