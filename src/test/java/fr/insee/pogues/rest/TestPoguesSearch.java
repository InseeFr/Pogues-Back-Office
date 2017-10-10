package fr.insee.pogues.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import fr.insee.pogues.rest.utils.RestAssuredConfig;
import fr.insee.pogues.utils.jersey.ObjectMapperProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

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


}
