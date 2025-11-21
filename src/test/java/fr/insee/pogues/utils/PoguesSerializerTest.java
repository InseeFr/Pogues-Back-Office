package fr.insee.pogues.utils;

import fr.insee.pogues.exception.PoguesSerializationException;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PoguesSerializerTest {

    @Test
    void serialize_simplestCase() throws PoguesSerializationException {
        //
        Questionnaire questionnaire = new Questionnaire();
        //
        String result = PoguesSerializer.questionnaireJavaToString(questionnaire);
        //
        assertNotNull(result);
    }

}
