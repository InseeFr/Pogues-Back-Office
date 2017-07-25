package fr.insee.pogues.rest.test.utils;

import com.jayway.restassured.RestAssured;
import org.apache.log4j.Logger;

import static com.jayway.restassured.RestAssured.expect;

/**
 * Created by acordier on 06/07/17.
 */
public class RestAssuredConfig {

    private static Logger logger = Logger.getLogger(RestAssuredConfig.class);

    public static String jUsername = "D5WQNO";
    public static String jPassword = "D5WQNO";
    public static String jUserPermission = "DG75-L120";


    public static void configure(){
        RestAssured.baseURI = "http://localhost:8080";
        /* All this boilerplate thing to handle Form auth with tomcat */
        String sessionId;
        logger.debug(
                "Trying to reach / should show login form with 200");
        sessionId = expect()
                .statusCode(200)
                .log().all()
                .when()
                .get("/")
                .sessionId();
        logger.debug(
                "Trying to post credentials should return 302");
        expect().statusCode(302)
                .log().all()
                .given()
                .param("j_username", jUsername)
                .param("j_password", jPassword).cookie("JSESSIONID", sessionId)
                .post("j_security_check");
        logger.debug(
                "Trying to reach / should now return 404");
        sessionId = expect()
                .statusCode(404)
                .log().all()
                .given().cookie("JSESSIONID", sessionId)
                .when()
                .get().sessionId();
        RestAssured.sessionId = sessionId;
    }

}
