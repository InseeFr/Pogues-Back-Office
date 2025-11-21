package fr.insee.pogues.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.exception.PoguesDeserializationException;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PoguesDeserializerTest {


    @Test
    void deserialize_simplestCase() throws JsonProcessingException, PoguesDeserializationException {
        //
        JsonNode jsonObject = jsonStringtoJsonNode("{\"id\":\"foo\"}");
        //
        Questionnaire result = PoguesDeserializer.questionnaireToJavaObject(jsonObject);
        //
        assertEquals("foo", result.getId());
    }

}
