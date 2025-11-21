package fr.insee.pogues.service.validation;

import fr.insee.pogues.conversion.JSONDeserializer;
import fr.insee.pogues.model.QuestionType;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.SequenceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;

import static org.junit.jupiter.api.Assertions.*;

class MandatoryCodeListMCQCheckTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();
    private Questionnaire questionnaireWithMCQ;

    @BeforeEach
    void parseTestQuestionnaire() throws JAXBException {
        questionnaireWithMCQ = new JSONDeserializer().deserialize(
                classLoader.getResourceAsStream("questionnaires/multiple-choice-questions.json"));
    }

    @Test
    @DisplayName("Mandatory undefined, should be valid")
    void mandatoryUndefined() {
        ValidationResult validationResult = new MandatoryCodeListMCQCheck().validate(questionnaireWithMCQ);
        assertTrue(validationResult.isValid());
        assertNull(validationResult.errorMessage());
    }

    @Test
    @DisplayName("Mandatory boolean MCQ, should be valid")
    void mandatoryBooleanMCQ() {
        SequenceType sequence = (SequenceType) questionnaireWithMCQ.getChild().getFirst();
        QuestionType booleanMCQ = (QuestionType) sequence.getChild().getFirst();
        booleanMCQ.setMandatory(true);

        ValidationResult validationResult = new MandatoryCodeListMCQCheck().validate(questionnaireWithMCQ);
        assertTrue(validationResult.isValid());
        assertNull(validationResult.errorMessage());
    }

    @Test
    @DisplayName("Mandatory code list MCQ, should be invalid")
    void mandatoryCodeListMCQ() {
        SequenceType sequence = (SequenceType) questionnaireWithMCQ.getChild().getFirst();
        QuestionType codeListMCQ = (QuestionType) sequence.getChild().get(1);
        codeListMCQ.setMandatory(true);

        ValidationResult validationResult = new MandatoryCodeListMCQCheck().validate(questionnaireWithMCQ);
        assertFalse(validationResult.isValid());
        assertEquals(
                "Les questions à choix multiples dont le type de réponse est \"Liste de codes\" ne peuvent " +
                        "pas être obligatoires (question 'MCQ_CODE_LIST').",
                validationResult.errorMessage());
    }

}
