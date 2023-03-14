package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.IllegalFlowControlException;
import fr.insee.pogues.exception.IllegalIterationException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.utils.PoguesModelUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuestionnaireCompositionTest {

    private Questionnaire questionnaire;
    private Questionnaire referenced1;
    private Questionnaire referenced2;

    @BeforeEach
    public void createQuestionnaires() {
        //
        referenced1 = new Questionnaire();
        referenced1.setId("ref1");
        SequenceType sequence11 = new SequenceType();
        sequence11.setId("seq11");
        sequence11.getChild().add(new QuestionType());
        referenced1.getChild().add(sequence11);
        referenced1.setVariables(new Questionnaire.Variables());
        //
        referenced2 = new Questionnaire();
        referenced2.setId("ref2");
        SequenceType sequence21 = new SequenceType();
        sequence21.setId("seq21");
        sequence21.getChild().add(new QuestionType());
        referenced2.getChild().add(sequence21);
        referenced2.setVariables(new Questionnaire.Variables());
        //
        questionnaire = new Questionnaire();
        questionnaire.setId("id");
        SequenceType sequence1 = new SequenceType();
        sequence1.setId("seq1");
        sequence1.getChild().add(new QuestionType());
        questionnaire.getChild().add(sequence1);
        questionnaire.getChild().add(referenced1);
        questionnaire.getChild().add(referenced2);
        SequenceType fakeEndSequence = new SequenceType();
        fakeEndSequence.setId(PoguesModelUtils.FAKE_LAST_ELEMENT_ID);
        questionnaire.getChild().add(fakeEndSequence);
        questionnaire.setVariables(new Questionnaire.Variables());
    }

    @Test
    void insertReference_sequences() throws IllegalIterationException, IllegalFlowControlException {
        assertEquals("ref1", questionnaire.getChild().get(1).getId());
        assertEquals("ref2", questionnaire.getChild().get(2).getId());
        //
        QuestionnaireComposition.insertReference(questionnaire, referenced1);
        QuestionnaireComposition.insertReference(questionnaire, referenced2);
        //
        assertEquals("seq1", questionnaire.getChild().get(0).getId());
        assertEquals("seq11", questionnaire.getChild().get(1).getId());
        assertEquals("seq21", questionnaire.getChild().get(2).getId());
    }

    @Test
    void insertReference_variables() throws IllegalIterationException, IllegalFlowControlException {
        //
        referenced1.getVariables().getVariable().add(new CollectedVariableType());
        referenced2.getVariables().getVariable().add(new CollectedVariableType());
        //
        assertEquals(0, questionnaire.getVariables().getVariable().size());
        //
        QuestionnaireComposition.insertReference(questionnaire, referenced1);
        QuestionnaireComposition.insertReference(questionnaire, referenced2);
        //
        assertEquals(2, questionnaire.getVariables().getVariable().size());
    }

    // TODO: other unit tests

}
