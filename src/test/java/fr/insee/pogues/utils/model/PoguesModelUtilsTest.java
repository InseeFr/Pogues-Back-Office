package fr.insee.pogues.utils.model;

import fr.insee.pogues.exception.questionnaire.composition.IllegalFlowControlException;
import fr.insee.pogues.exception.questionnaire.composition.IllegalIterationException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.model.Questionnaire.Iterations;

import fr.insee.pogues.utils.model.question.Table;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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
    void getQuestionnaireScopes_withoutIterations(){
        // Given empty questionnaire
        Questionnaire questionnaire = new Questionnaire();

        // When
        Map<String, String> scopes = PoguesModelUtils.getQuestionnaireScopes(questionnaire);
        // Then
        assertThat(scopes).isEmpty();
    }

    @Test
    void getQuestionnaireScopes_unitTests(){
        // Given a loop
        DynamicIterationType loop = new DynamicIterationType();
        loop.setId("loop-id");
        loop.setName("ma_boucle");
        // Given a pairwise question
        QuestionType pairwise = new QuestionType();
        pairwise.setQuestionType(QuestionTypeEnum.PAIRWISE);
        pairwise.setId("p-id");
        pairwise.setName("PAIRWISE");
        // Given a Table Loop
        QuestionType dynamicTable = new QuestionType();
        dynamicTable.setQuestionType(QuestionTypeEnum.TABLE);
        dynamicTable.setId("d-id");
        dynamicTable.setName("DYNAMIC_TABLE");
        DimensionType dynamicDimension = new DimensionType();
        dynamicDimension.setDimensionType(DimensionTypeEnum.PRIMARY);
        dynamicDimension.setDynamic(Table.DYNAMIC_LENGTH_DIMENSION);
        ResponseStructureType responseStructureType = new ResponseStructureType();
        responseStructureType.getDimension().add(dynamicDimension);
        dynamicTable.setResponseStructure(responseStructureType);

        Questionnaire questionnaire = new Questionnaire();
        Iterations iterations = new Iterations();
        iterations.getIteration().add(loop);
        questionnaire.setIterations(iterations);
        questionnaire.getChild().add(pairwise);
        questionnaire.getChild().add(dynamicTable);

        // When
        Map<String, String> scopes = PoguesModelUtils.getQuestionnaireScopes(questionnaire);

        // Then
        assertThat(scopes).hasSize(3);
        assertThat(scopes).containsKeys("loop-id","p-id","d-id");
        assertThat(scopes).containsValues("DYNAMIC_TABLE", "ma_boucle", "PAIRWISE");
    }

}
