package fr.insee.pogues.rest.test;

import com.jayway.restassured.RestAssured;
import fr.insee.pogues.rest.test.mock.QuestionnaireMocks;
import fr.insee.pogues.rest.test.utils.RestAssuredConfig;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static com.jayway.restassured.RestAssured.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


/**
 * Test Class used to test the REST Web Service PoguesPersistence, called by
 * Pogues-BO
 *
 * @author I6VWID
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
    public static void tearDown() {
        RestAssured.reset();
    }

    /**
     * Dummy helloworld test, should return "Hello world"
     */
    @Test
    public void helloworldTest() {
        logger.debug(
                "Dummy helloworld test : trying to reach /pogues/persistence/helloworld with Status = 200");
        expect()
                .statusCode(200)
                .when()
                .get("/pogues/persistence/helloworld");

    }

    /**
     * Post Questionnaire Test
     */
    @Test
    public void postQuestionnaireThenDeleteWithSuccess() {
        logger.debug("Trying to post on /pogues/persistence/questionnaires with Created = 201");
        JSONObject questionnaire = QuestionnaireMocks.createMockQuestionnaire();
        postQuestionnaireWithSuccess(questionnaire);
        deleteQuestionnaireWithSuccess(questionnaire.get("id").toString());

    }

    /**
     * Post Questionnaire Test
     */
    @Test
    public void postQuestionnaireThenRePostWithError() {
        JSONObject questionnaire = QuestionnaireMocks.createMockQuestionnaire();
        postQuestionnaireWithSuccess(questionnaire);
        expect()
                .statusCode(400)
                .when()
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(questionnaire.toJSONString())
                .when()
                .post("/pogues/persistence/questionnaires");
        deleteQuestionnaireWithSuccess(questionnaire.get("id").toString());
    }

    /**
     * Put Questionnaire Test
     */
    @Test
    public void putQuestionnaireWithNotFoundError() {
        logger.debug("Trying to post on /pogues/persistence/questionnaires with Created = 201");
        JSONObject questionnaire = QuestionnaireMocks.createMockQuestionnaire();
        expect()
                .statusCode(404)
                .when()
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(questionnaire.toJSONString())
                .when()
                .put("/pogues/persistence/questionnaire/notfound");

    }

    /**
     * Put Questionnaire Test
     */
    @Test
    public void postQuestionnaireThenUpdateWithNoContent() {
        JSONObject questionnaire = QuestionnaireMocks.createMockQuestionnaire();
        postQuestionnaireWithSuccess(questionnaire);
        expect()
                .statusCode(204)
                .when()
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(questionnaire.toJSONString())
                .when()
                .put("/pogues/persistence/questionnaire/" + questionnaire.get("id"));
        deleteQuestionnaireWithSuccess(questionnaire.get("id").toString());
    }

    @Test
    public void postQuestionnaireThenUpdateWithUnauthorized() {
        JSONObject questionnaire = QuestionnaireMocks.createMockQuestionnaire();
        questionnaire.put("owner", "DR31-DIR");
        postQuestionnaireWithSuccess(questionnaire);
        expect()
                .statusCode(403)
                .when()
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(questionnaire.toJSONString())
                .when()
                .put("/pogues/persistence/questionnaire/" + questionnaire.get("id"));
        deleteQuestionnaireWithSuccess(questionnaire.get("id").toString());
    }

    /**
     * getQuestionnaireById test
     */
    @Test
    public void postQuestionnaireThenGetWithSuccess() {
        logger.debug("Trying to reach /pogues/persistence/17 with Status = 200");
        JSONObject questionnaire = QuestionnaireMocks.createMockQuestionnaire();
        postQuestionnaireWithSuccess(questionnaire);
        expect()
                .statusCode(200)
                .when()
                .get("/pogues/persistence/questionnaire/" + questionnaire.get("id"));
        deleteQuestionnaireWithSuccess(questionnaire.get("id").toString());

    }

    @Test
    public void getQuestionnaireByIdWithNotFound() {
        logger.debug("Trying to reach /pogues/persistence/notfound with Status = 404");
        expect()
                .statusCode(404)
                .when()
                .get("/pogues/persistence/questionnaire/notfound");
    }

    @Test
    public void postQuestionnaireThenGetByOwnerWithSuccess() {
        JSONObject questionnaire = QuestionnaireMocks.createMockQuestionnaire();
        questionnaire.put("owner", "DR31-DIR");
        postQuestionnaireWithSuccess(questionnaire);
        int listSize = expect()
                .statusCode(200)
                .when()
                .given()
                .param("owner", questionnaire.get("owner").toString())
                .get("/pogues/persistence/questionnaires/search")
                .body()
                .jsonPath()
                .getList("$")
                .size();
        assertNotEquals(listSize, 0);
        deleteQuestionnaireWithSuccess(questionnaire.get("id").toString());
    }

    /**
     * getQuestionnaireList test
     */
    @Test
    public void getQuestionnairesWithNotFound() {
        logger.debug("Trying to reach /pogues/persistence/questionnaires with Status = 200");
        expect()
                .statusCode(404)
                .when()
                .get("/pogues/persistence/questionnaires");
    }

    @Test
    public void postQuestionnaireThenGetListWithSuccess() {
        JSONObject questionnaire = QuestionnaireMocks.createMockQuestionnaire();
        postQuestionnaireWithSuccess(questionnaire);
        int listSize = expect()
                .statusCode(200)
                .when()
                .given()
                .get("/pogues/persistence/questionnaires")
                .body()
                .jsonPath()
                .getList("$")
                .size();
        assertEquals(listSize, 1);
        deleteQuestionnaireWithSuccess(questionnaire.get("id").toString());
    }

    private void postQuestionnaireWithSuccess(JSONObject questionnaire){
        expect()
                .statusCode(201)
                .when()
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(questionnaire.toJSONString())
                .when()
                .post("/pogues/persistence/questionnaires");
    }

    private void deleteQuestionnaireWithSuccess(String id){
        expect()
                .statusCode(204)
                .when()
                .delete("/pogues/persistence/questionnaire/" + id);
    }

}
