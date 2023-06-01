package fr.insee.pogues.utils.json;

import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.utils.PoguesSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PoguesSerializerTest {

    @Test
    void serialize_simplestCase() {
        //
        Questionnaire questionnaire = new Questionnaire();
        //
        String result = PoguesSerializer.questionnaireJavaToString(questionnaire);
        //
        assertNotNull(result);
    }

}
