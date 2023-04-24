package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.IllegalFlowControlException;
import fr.insee.pogues.exception.IllegalIterationException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.utils.PoguesModelUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
        SequenceType sequence2 = new SequenceType();
        sequence2.setId("seq1");
        sequence2.getChild().add(new QuestionType());
        questionnaire.getChild().add(sequence2);
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
        referenced1.getVariables().getVariable().add(new ExternalVariableType());
        referenced2.getVariables().getVariable().add(new ExternalVariableType());
        //
        assertEquals(0, questionnaire.getVariables().getVariable().size());
        //
        QuestionnaireComposition.insertReference(questionnaire, referenced1);
        QuestionnaireComposition.insertReference(questionnaire, referenced2);
        //
        assertEquals(2, questionnaire.getVariables().getVariable().size());
    }

    @Test
    void insertReference_loopInReferenced() throws IllegalIterationException, IllegalFlowControlException {
        //
        IterationType iteration = new DynamicIterationType();
        iteration.setId("loop11");
        iteration.getMemberReference().add("seq11"); // begin member
        iteration.getMemberReference().add("seq11"); // end member
        referenced1.setIterations(new Questionnaire.Iterations());
        referenced1.getIterations().getIteration().add(iteration);
        //
        assertNull(questionnaire.getIterations());
        //
        QuestionnaireComposition.insertReference(questionnaire, referenced1);
        //
        assertNotNull(questionnaire.getIterations());
        assertFalse(questionnaire.getIterations().getIteration().isEmpty());
        assertEquals("loop11", questionnaire.getIterations().getIteration().get(0).getId());
        assertEquals("seq11", questionnaire.getIterations().getIteration().get(0).getMemberReference().get(0));
        assertEquals("seq11", questionnaire.getIterations().getIteration().get(0).getMemberReference().get(1));
    }

    @Test
    void insertReference_loopOnReference() throws IllegalIterationException, IllegalFlowControlException {
        //
        IterationType iteration = new DynamicIterationType();
        iteration.setId("loop1");
        iteration.getMemberReference().add("ref1"); // begin member
        iteration.getMemberReference().add("ref1"); // end member
        questionnaire.setIterations(new Questionnaire.Iterations());
        questionnaire.getIterations().getIteration().add(iteration);
        //
        QuestionnaireComposition.insertReference(questionnaire, referenced1);
        //
        assertEquals("seq11", questionnaire.getIterations().getIteration().get(0).getMemberReference().get(0));
        assertEquals("seq11", questionnaire.getIterations().getIteration().get(0).getMemberReference().get(1));
    }

    @Test
    void insertReference_referencedWithinLoop() throws IllegalIterationException, IllegalFlowControlException {
        // Add second sequence in referenced
        SequenceType sequence12 = new SequenceType();
        sequence12.setId("seq12");
        sequence12.getChild().add(new QuestionType());
        referenced1.getChild().add(sequence12);
        //
        IterationType iteration = new DynamicIterationType();
        iteration.setId("loop1");
        iteration.getMemberReference().add("seq1"); // begin member
        iteration.getMemberReference().add("ref1"); // end member
        questionnaire.setIterations(new Questionnaire.Iterations());
        questionnaire.getIterations().getIteration().add(iteration);
        //
        QuestionnaireComposition.insertReference(questionnaire, referenced1);
        //
        assertEquals("seq1", questionnaire.getIterations().getIteration().get(0).getMemberReference().get(0));
        assertEquals("seq12", questionnaire.getIterations().getIteration().get(0).getMemberReference().get(1));
    }

    /** To make sure de-referencing doesn't affect loops that shouldn't be affected. */
    @Test
    void insertReference_referenceOutsideLoop() throws IllegalIterationException, IllegalFlowControlException {
        //
        questionnaire.setIterations(new Questionnaire.Iterations());
        IterationType iteration1 = new DynamicIterationType();
        iteration1.setId("loop1");
        iteration1.getMemberReference().add("seq1"); // begin member
        iteration1.getMemberReference().add("seq1"); // end member
        questionnaire.getIterations().getIteration().add(iteration1);
        IterationType iteration2 = new DynamicIterationType();
        iteration2.setId("loop2");
        iteration2.getMemberReference().add("seq2"); // begin member
        iteration2.getMemberReference().add("seq2"); // end member
        questionnaire.getIterations().getIteration().add(iteration2);
        //
        QuestionnaireComposition.insertReference(questionnaire, referenced1);
        //
        assertEquals("seq1", questionnaire.getIterations().getIteration().get(0).getMemberReference().get(0));
        assertEquals("seq1", questionnaire.getIterations().getIteration().get(0).getMemberReference().get(1));
        assertEquals("seq2", questionnaire.getIterations().getIteration().get(1).getMemberReference().get(1));
        assertEquals("seq2", questionnaire.getIterations().getIteration().get(1).getMemberReference().get(1));
    }

    @Test
    void insertReference_updateVariableScopes() throws IllegalIterationException, IllegalFlowControlException {
        // Add iteration on referenced 1 in referencing questionnaire
        IterationType iteration = new DynamicIterationType();
        iteration.setId("loop1");
        iteration.getMemberReference().add("ref1"); // begin member
        iteration.getMemberReference().add("ref1"); // end member
        questionnaire.setIterations(new Questionnaire.Iterations());
        questionnaire.getIterations().getIteration().add(iteration);
        // Add calculated variable in referenced 1 with no scope
        VariableType calculatedVariable1 = new CalculatedVariableType();
        calculatedVariable1.setId("VAR11");
        referenced1.getVariables().getVariable().add(calculatedVariable1);

        // Add iteration in referenced 2 questionnaire
        IterationType iteration1 = new DynamicIterationType();
        iteration1.setId("loop21");
        iteration1.getMemberReference().add("seq21"); // begin member
        iteration1.getMemberReference().add("seq21"); // end member
        referenced2.setIterations(new Questionnaire.Iterations());
        referenced2.getIterations().getIteration().add(iteration1);
        // Add calculated variable in referenced 2 that has the scope of this iteration
        VariableType calculatedVariable2 = new CalculatedVariableType();
        calculatedVariable2.setId("VAR21");
        calculatedVariable2.setScope("loop21");
        referenced2.getVariables().getVariable().add(calculatedVariable2);

        //
        assertNull(calculatedVariable1.getScope());
        assertEquals("loop21", calculatedVariable2.getScope());

        //
        QuestionnaireComposition.insertReference(questionnaire, referenced1);
        QuestionnaireComposition.insertReference(questionnaire, referenced2);

        // The scope of the variable 1 should have been set with referencing loop's id
        Optional<VariableType> resultVariable1 = questionnaire.getVariables().getVariable().stream()
                .filter(variable -> variable.getId().equals("VAR11"))
                .findAny();
        assertTrue(resultVariable1.isPresent());
        assertEquals("loop1", resultVariable1.get().getScope());
        // The scope of the variable 2 should be unchanged
        Optional<VariableType> resultVariable2 = questionnaire.getVariables().getVariable().stream()
                .filter(variable -> variable.getId().equals("VAR21"))
                .findAny();
        assertTrue(resultVariable2.isPresent());
        assertEquals("loop21", resultVariable2.get().getScope());
    }

    // Other cases are currently covered in integration tests,
    // we might add unit test for these later, though.

    @Test
    @Disabled("Work in progress")
    void insertReference_duplicateIdentifiers() throws IllegalIterationException, IllegalFlowControlException {
        //
        referenced1.getChild().get(0).setId("seq1");
        assertEquals(questionnaire.getChild().get(0).getId(), referenced1.getChild().get(0).getId());
        //
        QuestionnaireComposition.insertReference(questionnaire, referenced1);
        QuestionnaireComposition.insertReference(questionnaire, referenced2);
        //
        assertNotEquals(questionnaire.getChild().get(0).getId(), questionnaire.getChild().get(1).getId());
    }

}
