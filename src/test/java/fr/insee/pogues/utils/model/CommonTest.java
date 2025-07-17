package fr.insee.pogues.utils.model;

import fr.insee.pogues.model.*;
import fr.insee.pogues.utils.model.question.Common;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.insee.pogues.utils.ModelCreatorUtils.createResponse;
import static fr.insee.pogues.utils.ModelCreatorUtils.initFakeCodeType;
import static fr.insee.pogues.utils.model.question.Common.buildMappingsBasedOnTwoDimensions;
import static fr.insee.pogues.utils.model.question.Common.buildSimpleMappingForMultipleChoiceQuestion;
import static org.junit.jupiter.api.Assertions.*;

class CommonTest {

    private List<CodeType> primaryCodes;
    private List<CodeType> secondaryCodes;

    @BeforeEach
    void initCodesList(){
        this.primaryCodes = List.of(
                initFakeCodeType("H","Homme",""),
                initFakeCodeType("F","Femme",""),
                initFakeCodeType("NB","Non-binaire","")
        );
        this.secondaryCodes = List.of(
                initFakeCodeType("1","Oui",""),
                initFakeCodeType("1","Non","")
        );
    }

    @Test
    void testBuildMappingsWithSecondaryAxis(){
        List<ResponseType> responses = List.of(
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT)
        );
        List<MappingType> mappings = buildMappingsBasedOnTwoDimensions(
                primaryCodes, secondaryCodes, responses
        );
        assertEquals(primaryCodes.size()*secondaryCodes.size(),
                mappings.size());
        assertTrue(mappings.stream()
                        .anyMatch(m-> responses.getFirst().getId()
                                .equals(m.getMappingSource()))
        );
        assertEquals("1 1",mappings.getFirst().getMappingTarget());
        assertEquals(
                String.format("%d %d", secondaryCodes.size(), primaryCodes.size()),
                mappings.getLast().getMappingTarget());
    }

    @Test
    void testBuildSimpleMappingForMultipleChoiceQuestion(){
        List<ResponseType> responses = List.of(
                createResponse(DatatypeTypeEnum.BOOLEAN),
                createResponse(DatatypeTypeEnum.BOOLEAN),
                createResponse(DatatypeTypeEnum.BOOLEAN)
        );
        List<MappingType> mappings = buildSimpleMappingForMultipleChoiceQuestion(responses);
        assertEquals(responses.size(),mappings.size());
        assertTrue(mappings.stream()
                .anyMatch(m-> responses.getFirst().getId()
                        .equals(m.getMappingSource()))
        );
        assertEquals("1",mappings.getFirst().getMappingTarget());
        assertEquals("3", mappings.getLast().getMappingTarget());
    }

    @Test
    void testBuildMappingsAccordingToOneAndTwoMeasures(){
        List<ResponseType> responsesPattern = List.of(
                createResponse(DatatypeTypeEnum.NUMERIC),
                createResponse(DatatypeTypeEnum.TEXT));
        List<ResponseType> responses = List.of(
                createResponse(DatatypeTypeEnum.NUMERIC),
                createResponse(DatatypeTypeEnum.NUMERIC),
                createResponse(DatatypeTypeEnum.NUMERIC),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT)
        );
        List<MappingType> mappings = buildMappingsBasedOnTwoDimensions(primaryCodes, responsesPattern, responses);
        assertEquals(6, mappings.size());
        assertEquals("1 1", mappings.getFirst().getMappingTarget());
        assertEquals("2 3", mappings.getLast().getMappingTarget());
        // Test if each responses id are mapped
        responses.forEach(response -> {
            assertTrue(mappings.stream().anyMatch(m->response.getId().equals(m.getMappingSource())));
        });
    }

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
        Common.limitToSingleClarification(question);

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
