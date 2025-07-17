package fr.insee.pogues.service.modelcleaning.rules;

import fr.insee.pogues.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.insee.pogues.service.modelcleaning.rules.ClarificationRules.limitToSingleClarification;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClarificationRulesTest {

    @Test
    void testLimitToSingleClarification() {

        QuestionType question = new QuestionType();
        question.setQuestionType(QuestionTypeEnum.SINGLE_CHOICE);

        // Clarification 1 - kept
        QuestionType clarification1 = new QuestionType();
        clarification1.setId("clarif-1");
        ResponseType resp1 = new ResponseType();
        resp1.setId("resp-1");
        resp1.setCollectedVariableReference("var-1");
        clarification1.getResponse().add(resp1);

        // Clarification 2 - removed
        QuestionType clarification2 = new QuestionType();
        clarification2.setId("clarif-2");
        ResponseType resp2 = new ResponseType();
        resp2.setId("resp-2");
        resp2.setCollectedVariableReference("var-2");
        clarification2.getResponse().add(resp2);

        // Clarification 3 - removed
        QuestionType clarification3 = new QuestionType();
        clarification3.setId("clarif-3");
        ResponseType resp3 = new ResponseType();
        resp3.setId("resp-3");
        resp3.setCollectedVariableReference("var-3");
        clarification3.getResponse().add(resp3);

        // Add the clarifications to the question
        question.getClarificationQuestion().addAll(List.of(clarification1, clarification2, clarification3));

        // Add FlowControls
        FlowControlType fc1 = new FlowControlType();
        fc1.setFlowControlType(FlowControlTypeEnum.CLARIFICATION);
        fc1.setIfTrue("clarif-1");

        FlowControlType fc2 = new FlowControlType();
        fc2.setFlowControlType(FlowControlTypeEnum.CLARIFICATION);
        fc2.setIfTrue("clarif-2");

        FlowControlType fc3 = new FlowControlType();
        fc3.setFlowControlType(FlowControlTypeEnum.CLARIFICATION);
        fc3.setIfTrue("clarif-3");

        question.getFlowControl().addAll(List.of(fc1, fc2, fc3));

        // Call the method
        limitToSingleClarification(question);

        // Verify: only one clarification remains
        assertEquals(1, question.getClarificationQuestion().size());
        assertEquals("clarif-1", question.getClarificationQuestion().getFirst().getId());

        // Verify: clarification 1's variable is retained
        assertEquals("var-1", question.getClarificationQuestion().getFirst().getResponse().getFirst().getCollectedVariableReference());

        // Verify: remaining FlowControls only reference clarif-1
        assertEquals(1, question.getFlowControl().size());
        assertEquals("clarif-1", question.getFlowControl().getFirst().getIfTrue());

        // Verify: removed clarifications have their variable references set to null
        assertNull(resp2.getCollectedVariableReference(), "Clarif 2 variable should be null");
        assertNull(resp3.getCollectedVariableReference(), "Clarif 3 variable should be null");
    }
}