package fr.insee.pogues.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import fr.insee.pogues.rest.utils.RestAssuredConfig;
import fr.insee.pogues.search.model.PoguesHit;
import fr.insee.pogues.search.model.PoguesItem;
import fr.insee.pogues.utils.jersey.ObjectMapperProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static com.jayway.restassured.RestAssured.expect;

public class TestPoguesSearch {


    final static Logger logger = LogManager.getLogger(TestPoguesPersistence.class);

    final static ObjectMapper mapper = new ObjectMapperProvider().getContext(TestPoguesPersistence.class);


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

    @Test
    public void indexThenDeleteWithSuccess(){
        PoguesHit item = new PoguesHit("foo", "bar", "0", "questionnaire");
        try {
            indexWithSuccess(item.getType(), item);
            deleteWithSuccess(item.getType(), item.getId());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    private void indexWithSuccess(String type, PoguesItem item) throws JsonProcessingException {
        expect()
                .statusCode(201)
                .when()
                .given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsBytes(item))
                .when()
                .post("/pogues/search/" + type);
    }

    private void deleteWithSuccess(String type, String id)  {
        expect()
                .statusCode(204)
                .when()
                .delete(String.format("/pogues/search/%s/%s", type, id));
    }
}
