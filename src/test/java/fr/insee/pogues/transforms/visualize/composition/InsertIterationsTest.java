package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.model.DynamicIterationType;
import fr.insee.pogues.model.IterationType;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InsertIterationsTest {

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
        IterationType iteration = new DynamicIterationType();
        iteration.setId("loop11");
        iteration.getMemberReference().add("seq11"); // begin member
        iteration.getMemberReference().add("seq11"); // end member
        referenced1.setIterations(new Questionnaire.Iterations());
        referenced1.getIterations().getIteration().add(iteration);
        //
        assertNull(questionnaire.getIterations());
        //
        InsertIterations insertIterations = new InsertIterations();
        insertIterations.apply(questionnaire, referenced1);
        //
        assertNotNull(questionnaire.getIterations());
        assertFalse(questionnaire.getIterations().getIteration().isEmpty());
        assertEquals("loop11", questionnaire.getIterations().getIteration().get(0).getId());
        assertEquals("seq11", questionnaire.getIterations().getIteration().get(0).getMemberReference().get(0));
        assertEquals("seq11", questionnaire.getIterations().getIteration().get(0).getMemberReference().get(1));
    }

}
