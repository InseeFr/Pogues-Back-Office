package fr.insee.pogues.rest.test.utils;

import com.jayway.restassured.RestAssured;

import static com.jayway.restassured.RestAssured.expect;

/**
 * Created by acordier on 06/07/17.
 */
public class RestAssuredConfig {

    public static String jUsername = "D5WQNO";
    public static String jPassword = "D5WQNO";

    public static void configure(){
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
}
