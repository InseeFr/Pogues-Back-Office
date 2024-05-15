package fr.insee.pogues.utils;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.conversion.JSONDeserializer;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/** This should be moved in Pogues-Model. */
@Slf4j
public class PoguesDeserializer {

    private static JSONDeserializer jsonDeserializer = new JSONDeserializer();
    private PoguesDeserializer() {}

    /**
     * Converts the json object questionnaire given in a Pogues-Model questionnaire object.
     * @param jsonQuestionnaire Json object representing a Pogues questionnaire.
     * @return Corresponding Pogues-Model questionnaire object.
     */
    public static Questionnaire questionnaireToJavaObject(JsonNode jsonQuestionnaire)
            throws  JAXBException {
        InputStream questionnaireAsIS = new ByteArrayInputStream(jsonQuestionnaire.toString().getBytes(StandardCharsets.UTF_8));
        return jsonDeserializer.deserialize(questionnaireAsIS);
    }

}
