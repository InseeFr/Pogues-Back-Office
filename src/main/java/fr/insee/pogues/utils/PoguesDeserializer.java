package fr.insee.pogues.utils;

import fr.insee.pogues.exception.PoguesDeserializationException;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.json.simple.JSONObject;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/** This should be moved in Pogues-Model. */
@Slf4j
public class PoguesDeserializer {

    private PoguesDeserializer() {}

    /**
     * Converts the json object questionnaire given in a Pogues-Model questionnaire object.
     * @param jsonQuestionnaire Json object representing a Pogues questionnaire.
     * @return Corresponding Pogues-Model questionnaire object.
     * @throws PoguesDeserializationException if deserialization fails.
     */
    public static Questionnaire questionnaireToJavaObject(JSONObject jsonQuestionnaire)
            throws PoguesDeserializationException {
        String questionnaireId = (String) jsonQuestionnaire.get("id");
        log.info("Deserializing json questionnaire '{}'", questionnaireId);
        try (InputStream inQuestionnaire = new ByteArrayInputStream(jsonQuestionnaire.toString().getBytes())) {
            StreamSource json = new StreamSource(inQuestionnaire);
            //
            JAXBContext context = JAXBContext.newInstance(Questionnaire.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
            unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
            //
            Questionnaire questionnaire = unmarshaller.unmarshal(json, Questionnaire.class).getValue();
            log.info("Successfully deserialized json questionnaire '{}'", questionnaireId);
            return questionnaire;
        } catch (IOException | JAXBException e) {
            throw new PoguesDeserializationException(
                    "Exception occurred while trying to deserialize json questionnaire '"+questionnaireId+"'",
                    e);
        }
    }

}
