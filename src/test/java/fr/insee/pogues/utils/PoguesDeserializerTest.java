package fr.insee.pogues.utils;

import fr.insee.pogues.model.Questionnaire;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PoguesDeserializerTest {


    @Test
    void deserialize_simplestCase() throws ParseException, JAXBException {
        //
        JSONObject jsonObject = (JSONObject) new JSONParser().parse("{\"id\":\"foo\"}");
        //
        Questionnaire result = PoguesDeserializer.questionnaireToJavaObject(jsonObject);
        //
        assertEquals("foo", result.getId());
    }

}
