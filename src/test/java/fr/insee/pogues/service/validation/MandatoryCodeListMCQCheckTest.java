package fr.insee.pogues.service.validation;

import fr.insee.pogues.conversion.JSONDeserializer;
import fr.insee.pogues.model.QuestionType;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.SequenceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;

import static org.junit.jupiter.api.Assertions.*;

class MandatoryCodeListMCQCheckTest {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    @Test
    @DisplayName("Mandatory undefined, should be valid")
    void mandatoryUndefined() throws JAXBException {
        Questionnaire questionnaireWithMCQ = new JSONDeserializer().deserialize(
                classLoader.getResourceAsStream("questionnaires/multiple-choice-questions.json"));
        assertTrue(new MandatoryCodeListMCQCheck().validate(questionnaireWithMCQ));
    }

    @Test
    @DisplayName("Mandatory boolean MCQ, should be valid")
    void mandatoryBooleanMCQ() throws JAXBException {
        Questionnaire questionnaireWithMCQ = new JSONDeserializer().deserialize(
                classLoader.getResourceAsStream("questionnaires/multiple-choice-questions.json"));
        SequenceType sequence = (SequenceType) questionnaireWithMCQ.getChild().getFirst();
        QuestionType booleanMCQ = (QuestionType) sequence.getChild().getFirst();
        booleanMCQ.setMandatory(true);

        var mandatoryCodeListMCQCheck = new MandatoryCodeListMCQCheck();
        assertTrue(mandatoryCodeListMCQCheck.validate(questionnaireWithMCQ));
        assertThrows(IllegalStateException.class, mandatoryCodeListMCQCheck::errorMessage);
    }

    @Test
    @DisplayName("Mandatory code list MCQ, should be invalid")
    void mandatoryCodeListMCQ() throws JAXBException {
        Questionnaire questionnaireWithMCQ = new JSONDeserializer().deserialize(
                classLoader.getResourceAsStream("questionnaires/multiple-choice-questions.json"));
        SequenceType sequence = (SequenceType) questionnaireWithMCQ.getChild().getFirst();
        QuestionType codeListMCQ = (QuestionType) sequence.getChild().get(1);
        codeListMCQ.setMandatory(true);

        var mandatoryCodeListMCQCheck = new MandatoryCodeListMCQCheck();
        assertFalse(mandatoryCodeListMCQCheck.validate(questionnaireWithMCQ));
        assertEquals(
                "Les question QCM de type \"Liste de codes\" ne peuvent pas être obligatoires " +
                        "(question 'MCQ_CODE_LIST').",
                mandatoryCodeListMCQCheck.errorMessage());
    }

}
