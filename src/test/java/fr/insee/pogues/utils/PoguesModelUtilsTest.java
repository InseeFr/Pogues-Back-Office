package fr.insee.pogues.utils;

import fr.insee.pogues.exception.IllegalFlowControlException;
import fr.insee.pogues.exception.IllegalIterationException;
import fr.insee.pogues.model.DynamicIterationType;
import fr.insee.pogues.model.FlowControlType;
import fr.insee.pogues.model.IterationType;
import fr.insee.pogues.utils.model.PoguesModelUtils;
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

}
