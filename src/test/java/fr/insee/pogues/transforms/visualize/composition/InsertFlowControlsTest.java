package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.model.FlowControlType;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InsertFlowControlsTest {

    private final Questionnaire questionnaire = new Questionnaire();
    private final Questionnaire referenced1 = new Questionnaire();
    private final Questionnaire referenced2 = new Questionnaire();

    @BeforeEach
    public void createQuestionnaires() {
        QuestionnaireCompositionTest.questionnairesContent(questionnaire, referenced1, referenced2);
    }

    @Test
    void insertReference_loopInReferenced() {
        //
        FlowControlType flowControlType = new FlowControlType();
        flowControlType.setId("filter11");
        flowControlType.setIfTrue("seq11-seq11"); // begin-end member
        referenced1.getFlowControl().add(flowControlType);
        //
        assertTrue(questionnaire.getFlowControl().isEmpty());
        //
        InsertFlowControls insertFlowControls = new InsertFlowControls();
        insertFlowControls.apply(questionnaire, referenced1);
        //
        assertFalse(questionnaire.getFlowControl().isEmpty());
        assertEquals("filter11", questionnaire.getFlowControl().get(0).getId());
        assertEquals("seq11-seq11", questionnaire.getFlowControl().get(0).getIfTrue());
    }

}
