package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsertSequencesTest {

    private final Questionnaire questionnaire = new Questionnaire();
    private final Questionnaire referenced1 = new Questionnaire();
    private final Questionnaire referenced2 = new Questionnaire();

    @BeforeEach
    public void createQuestionnaires() {
        QuestionnaireCompositionTest.questionnairesContent(questionnaire, referenced1, referenced2);
    }

    @Test
    void insertReference_sequences() {
        assertEquals("ref1", questionnaire.getChild().get(1).getId());
        assertEquals("ref2", questionnaire.getChild().get(2).getId());
        //
        InsertSequences insertSequences = new InsertSequences();
        insertSequences.apply(questionnaire, referenced1);
        insertSequences.apply(questionnaire, referenced2);
        //
        assertEquals("seq1", questionnaire.getChild().get(0).getId());
        assertEquals("seq11", questionnaire.getChild().get(1).getId());
        assertEquals("seq21", questionnaire.getChild().get(2).getId());
    }

}
