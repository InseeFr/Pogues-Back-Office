package fr.insee.pogues.service.modelcleaning.rules;

import fr.insee.pogues.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.insee.pogues.service.modelcleaning.rules.ClarificationCleaner.limitToSingleClarification;
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

        // Add the clarifications to the main question
        mainQuestion.getClarificationQuestion().addAll(
                List.of(clarificationToKeep, clarificationToRemove1, clarificationToRemove2)
        );

        // Add FlowControls
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

        // Call the method
        limitToSingleClarification(mainQuestion);

        // Verify: only one clarification remains
        assertEquals(1, mainQuestion.getClarificationQuestion().size());
        assertEquals("clarif-1", mainQuestion.getClarificationQuestion().getFirst().getId());

        // Verify: clarification 1's variable is retained
        assertEquals("var-1", mainQuestion.getClarificationQuestion().getFirst().getResponse().getFirst().getCollectedVariableReference());

        // Verify: remaining FlowControls only reference clarif-1
        assertEquals(1, mainQuestion.getFlowControl().size());
        assertEquals("clarif-1", mainQuestion.getFlowControl().getFirst().getIfTrue());

        // Verify: removed clarifications have their variable references set to null
        assertNull(response2.getCollectedVariableReference(), "Clarif 2 variable should be null");
        assertNull(response3.getCollectedVariableReference(), "Clarif 3 variable should be null");
    }


}