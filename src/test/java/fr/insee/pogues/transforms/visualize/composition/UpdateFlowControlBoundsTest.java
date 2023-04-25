package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.FlowControlType;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.SequenceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpdateFlowControlBoundsTest {

    private final Questionnaire questionnaire = new Questionnaire();
    private final Questionnaire referenced1 = new Questionnaire();
    private final Questionnaire referenced2 = new Questionnaire();

    @BeforeEach
    public void createQuestionnaires() {
        QuestionnaireCompositionTest.questionnairesContent(questionnaire, referenced1, referenced2);
    }

    @Test
    void endMemberIsReferencedQuestionnaire() throws DeReferencingException {
        //
        FlowControlType flowControlType = new FlowControlType();
        flowControlType.setId("filter1");
        flowControlType.setIfTrue("seq1-ref1");
        questionnaire.getFlowControl().add(flowControlType);
        //
        SequenceType sequence12 = new SequenceType();
        sequence12.setId("seq12");
        referenced1.getChild().add(sequence12);
        //
        UpdateFlowControlBounds updateFlowControlBounds = new UpdateFlowControlBounds();
        updateFlowControlBounds.apply(questionnaire, referenced1);
        //
        assertEquals("seq1-seq12", questionnaire.getFlowControl().get(0).getIfTrue());
    }

}
