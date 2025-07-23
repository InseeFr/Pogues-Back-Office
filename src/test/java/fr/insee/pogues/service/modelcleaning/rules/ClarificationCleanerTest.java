package fr.insee.pogues.service.modelcleaning.rules;

import fr.insee.pogues.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClarificationCleanerTest {

    @Test
    void testLimitToSingleClarification() {

        QuestionType mainQuestion = new QuestionType();
        mainQuestion.setQuestionType(QuestionTypeEnum.SINGLE_CHOICE);

        // Clarification 1 - kept
        QuestionType clarificationToKeep = new QuestionType();
        clarificationToKeep.setId("clarif-1");
        ResponseType response1 = new ResponseType();
        response1.setId("resp-1");
        response1.setCollectedVariableReference("var-1");
        clarificationToKeep.getResponse().add(response1);

        // Clarification 2 - removed
        QuestionType clarificationToRemove1 = new QuestionType();
        clarificationToRemove1.setId("clarif-2");
        ResponseType response2 = new ResponseType();
        response2.setId("resp-2");
        response2.setCollectedVariableReference("var-2");
        clarificationToRemove1.getResponse().add(response2);

        // Clarification 3 - removed
        QuestionType clarificationToRemove2 = new QuestionType();
        clarificationToRemove2.setId("clarif-3");
        ResponseType response3 = new ResponseType();
        response3.setId("resp-3");
        response3.setCollectedVariableReference("var-3");
        clarificationToRemove2.getResponse().add(response3);

        mainQuestion.getClarificationQuestion().addAll(
                List.of(clarificationToKeep, clarificationToRemove1, clarificationToRemove2)
        );

        // FlowControls
        FlowControlType flowToKeep = new FlowControlType();
        flowToKeep.setFlowControlType(FlowControlTypeEnum.CLARIFICATION);
        flowToKeep.setIfTrue("clarif-1");

        FlowControlType flowToRemove1 = new FlowControlType();
        flowToRemove1.setFlowControlType(FlowControlTypeEnum.CLARIFICATION);
        flowToRemove1.setIfTrue("clarif-2");

        FlowControlType flowToRemove2 = new FlowControlType();
        flowToRemove2.setFlowControlType(FlowControlTypeEnum.CLARIFICATION);
        flowToRemove2.setIfTrue("clarif-3");

        mainQuestion.getFlowControl().addAll(List.of(flowToKeep, flowToRemove1, flowToRemove2));

        // Variables & questionnaire
        CollectedVariableType variable1 = new CollectedVariableType();
        variable1.setId("var-1");

        CollectedVariableType variable2 = new CollectedVariableType();
        variable2.setId("var-2");

        CollectedVariableType variable3 = new CollectedVariableType();
        variable3.setId("var-3");

        Questionnaire questionnaire = new Questionnaire();
        Questionnaire.Variables variables = new Questionnaire.Variables();
        variables.getVariable().addAll(List.of(variable1, variable2, variable3));
        questionnaire.setVariables(variables);

        // Add the main question to questionnaire
        questionnaire.getChild().add(mainQuestion);

        // Call the method under test
        new ClarificationCleaner().apply(questionnaire);

        // Assertions
        assertEquals(1, mainQuestion.getClarificationQuestion().size());
        assertEquals("clarif-1", mainQuestion.getClarificationQuestion().getFirst().getId());

        assertEquals(1, mainQuestion.getFlowControl().size());
        assertEquals("clarif-1", mainQuestion.getFlowControl().getFirst().getIfTrue());

        // The removed clarifications' responses should have their variables cleared
        assertNull(response2.getCollectedVariableReference());
        assertNull(response3.getCollectedVariableReference());

        List<String> remainingVariableIds = questionnaire.getVariables().getVariable()
                .stream()
                .map(VariableType::getId)
                .toList();

        assertEquals(1, remainingVariableIds.size());
        assertTrue(remainingVariableIds.contains("var-1"));
    }
}

