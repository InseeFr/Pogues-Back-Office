package fr.insee.pogues.rest.test;

import com.jayway.restassured.RestAssured;
import fr.insee.pogues.rest.test.utils.RestAssuredConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
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


    final static Logger logger = LogManager.getLogger(TestPoguesUser.class);

    /**
     * Setting up the RestAssured default URI
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
        assertEquals(id, RestAssuredConfig.jUsername);
    }

    @Test
    public void getAttributes(){
        logger.debug(
                "Trying to reach /Pogues-BO/pogues/user/id with Status = 200");
        String id = expect()
                .statusCode(200)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get(String.format("/pogues/user/attributes", RestAssuredConfig.jUsername))
                .body()
                .jsonPath()
                .get("id");
        assertEquals(id, RestAssuredConfig.jUsername);
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
