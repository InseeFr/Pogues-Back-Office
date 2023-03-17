package fr.insee.pogues.utils;

import fr.insee.pogues.exception.PoguesDeserializationException;
import fr.insee.pogues.model.Questionnaire;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PoguesDeserializerTest {

    @Test
    void deserialize_invalidString() throws ParseException {
        //
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject1 = (JSONObject) jsonParser.parse("{\"foo\":\"bar\"}");
        JSONObject jsonObject2 = (JSONObject) jsonParser.parse("{\"id\":{}}");
        //
        assertThrows(PoguesDeserializationException.class, () ->
                PoguesDeserializer.questionnaireToJavaObject(jsonObject1));
        assertThrows(PoguesDeserializationException.class, () ->
                PoguesDeserializer.questionnaireToJavaObject(jsonObject2));
    }

    @Test
    void deserialize_simplestCase() throws ParseException, PoguesDeserializationException {
        //
        JSONObject jsonObject = (JSONObject) new JSONParser().parse("{\"id\":\"foo\"}");
        //
        Questionnaire result = PoguesDeserializer.questionnaireToJavaObject(jsonObject);
        //
        assertEquals("foo", result.getId());
    }

}
