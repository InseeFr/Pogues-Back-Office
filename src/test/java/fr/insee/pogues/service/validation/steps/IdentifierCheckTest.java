package fr.insee.pogues.service.validation.steps;

import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.service.validation.ValidationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IdentifierCheckTest {

    @Test
    @DisplayName("Valid model Id according to id, should be valid")
    void validIdentifier() {
        String poguesId = "validid";
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setId(poguesId);

        ValidationResult validationResult = new IdentifierCheck().validate(questionnaire, poguesId);
        assertTrue(validationResult.isValid());
        assertNull(validationResult.errorMessage());
    }


    @Test
    @DisplayName("Invalid model id according to id, should be invalid")
    void invalidIdentifier() {
        String poguesId = "validid";
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setId("notvalidid");

        ValidationResult validationResult = new IdentifierCheck().validate(questionnaire, poguesId);
        assertFalse(validationResult.isValid());
        assertEquals(
                "L'identifiant du questionnaire 'validid' ne peut pas être modifié avec la valeur 'notvalidid'",
                validationResult.errorMessage());
    }
}
