package fr.insee.pogues.utils.model;

import fr.insee.pogues.exception.IllegalFlowControlException;
import fr.insee.pogues.exception.IllegalIterationException;
import fr.insee.pogues.model.DynamicIterationType;
import fr.insee.pogues.model.FlowControlType;
import fr.insee.pogues.model.IterationType;
import fr.insee.pogues.model.QuestionType;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.Questionnaire.Iterations;
import fr.insee.pogues.model.SequenceType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PoguesModelUtilsTest {

    @Test
    void getFlowControlBounds_nullIfTrue() {
        FlowControlType flowControlType = new FlowControlType();
        // The 'IfTrue' property is not set and is null
        assertThrows(IllegalFlowControlException.class, () -> PoguesModelUtils.getFlowControlBounds(flowControlType));
    }

    @Test
    void getIterationBounds_invalidIfTrue() {
        FlowControlType flowControlType = new FlowControlType();
        flowControlType.setIfTrue("foo");
        assertThrows(IllegalFlowControlException.class, () -> PoguesModelUtils.getFlowControlBounds(flowControlType));
    }

    @Test
    void getIterationBounds_emptyMemberReference() {
        IterationType iterationType = new DynamicIterationType();
        assertThrows(IllegalIterationException.class, () -> PoguesModelUtils.getIterationBounds(iterationType));
    }

    @Test
    void getIterationBounds_invalidMemberReference() {
        IterationType iterationType = new DynamicIterationType();
        iterationType.getMemberReference().add("seq1");
        iterationType.getMemberReference().add("seq2");
        iterationType.getMemberReference().add("seq3");
        assertThrows(IllegalIterationException.class, () -> PoguesModelUtils.getIterationBounds(iterationType));
    }

    static class FooIteration extends IterationType {}

    @Test
    void isLinkedLoop_wrongObject_shouldThrowException() {
        // Given an unexpected new type of iteration
        IterationType iterationType = new FooIteration();
        // When + Then
        assertThrows(IllegalIterationException.class, () ->
                PoguesModelUtils.isLinkedLoop(iterationType));
    }

    @Test
    void getLinkedLoopReference_wrongObject_shouldThrowException() {
        // Given an unexpected new type of iteration
        IterationType iterationType = new FooIteration();
        // When + Then
        assertThrows(IllegalIterationException.class, () ->
                PoguesModelUtils.getLinkedLoopReference(iterationType));
    }

    @Test
    void isLinkedLoop_unitTests() throws IllegalIterationException {
        //
        DynamicIterationType mainLoop = new DynamicIterationType();
        mainLoop.setId("main-loop-id");
        DynamicIterationType linkedLoop = new DynamicIterationType();
        linkedLoop.setId("linked-loop-id");
        linkedLoop.setIterableReference("main-loop-id");
        //
        assertFalse(PoguesModelUtils.isLinkedLoop(mainLoop));
        assertTrue(PoguesModelUtils.isLinkedLoop(linkedLoop));
    }

    @Test
    void getLinkedLoopReference_unitTests() throws IllegalIterationException {
        //
        DynamicIterationType mainLoop = new DynamicIterationType();
        mainLoop.setId("main-loop-id");
        DynamicIterationType linkedLoop = new DynamicIterationType();
        linkedLoop.setId("linked-loop-id");
        linkedLoop.setIterableReference("main-loop-id");
        //
        assertNull(PoguesModelUtils.getLinkedLoopReference(mainLoop));
        assertEquals("main-loop-id", PoguesModelUtils.getLinkedLoopReference(linkedLoop));
    }

    @Test
    void getScopeNameFromID_unitTests() {
        // 1: Given a loop
        DynamicIterationType loop = new DynamicIterationType();
        loop.setId("loop-id");
        loop.setName("ma_boucle");
        // 2: Given a question "ma_question"
        QuestionType question = new QuestionType();
        question.setId("q-id");
        question.setName("ma_question");
        SequenceType sequence = new SequenceType();
        sequence.getChild().add(question);
        Iterations iterations = new Iterations();
        iterations.getIteration().add(loop);
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setIterations(iterations);
        questionnaire.getChild().add(sequence);

        // When we get the linked loop reference name
        // Then we return null
        assertNull(PoguesModelUtils.getScopeNameFromID(questionnaire, "non-existing-scope-id"));
        // 1: Then we return the loop name ("ma_boucle")
        assertEquals("ma_boucle", PoguesModelUtils.getScopeNameFromID(questionnaire, "loop-id"));
        // 2: Then we return the question name ("ma_question")
        assertEquals("ma_question", PoguesModelUtils.getScopeNameFromID(questionnaire, "q-id"));
    }

}
