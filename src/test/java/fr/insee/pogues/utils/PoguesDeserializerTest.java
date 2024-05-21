package fr.insee.pogues.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PoguesDeserializerTest {


    @Test
    void deserialize_simplestCase() throws JAXBException, JsonProcessingException {
        //
        JsonNode jsonObject = jsonStringtoJsonNode("{\"id\":\"foo\"}");
        //
        Questionnaire result = PoguesDeserializer.questionnaireToJavaObject(jsonObject);
        //
        assertEquals("foo", result.getId());
    }

}
