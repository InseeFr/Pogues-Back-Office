package fr.insee.pogues.utils;

import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.persistence.jaxb.MarshallerProperties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** This should be moved in Pogues-Model. */
@Slf4j
public class PoguesSerializer {

    private PoguesSerializer() {}

    /**
     * Convert the given questionnaire object in a json string.
     * @param questionnaire Pogues-Model questionnaire.
     * @return Questionnaire as json string.
     */
    public static String questionnaireJavaToString(Questionnaire questionnaire) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            JAXBContext context = JAXBContext.newInstance(Questionnaire.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            // Set it to true if you need to include the JSON root element in the JSON output
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
            // Set it to true if you need the JSON output to formatted
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // Marshal the questionnaire object to JSON and put the output in a string
            marshaller.marshal(questionnaire, outputStream);
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (JAXBException | IOException e) {
            log.error("Unable to serialize Pogues questionnaire '{}'.", questionnaire);
            e.printStackTrace();
            return "";
        }
    }

}
