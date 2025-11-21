package fr.insee.pogues.utils;

import fr.insee.pogues.conversion.JSONSerializer;
import fr.insee.pogues.exception.PoguesSerializationException;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;

/** This should be moved in Pogues-Model. */
@Slf4j
public class PoguesSerializer {

    private static final JSONSerializer jsonSerializer = new JSONSerializer(true);

    private PoguesSerializer() {}

    /**
     * Convert the given questionnaire object in a json string.
     * @param questionnaire Pogues-Model questionnaire.
     * @return Questionnaire as json string.
     */
    public static String questionnaireJavaToString(Questionnaire questionnaire) throws PoguesSerializationException {
        try {
            return jsonSerializer.serialize(questionnaire);
        } catch (JAXBException | UnsupportedEncodingException e) {
            throw new PoguesSerializationException(e);
        }
    }

}
