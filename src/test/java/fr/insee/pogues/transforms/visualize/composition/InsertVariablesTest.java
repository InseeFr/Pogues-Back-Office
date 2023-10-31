package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.model.ExternalVariableType;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InsertVariablesTest {

    private final Questionnaire questionnaire = new Questionnaire();
    private final Questionnaire referenced1 = new Questionnaire();
    private final Questionnaire referenced2 = new Questionnaire();

    @BeforeEach
    public void createQuestionnaires() {
        QuestionnaireCompositionTest.questionnairesContent(questionnaire, referenced1, referenced2);
    }

    @Test
    void insertReference_variables() {
        //
        referenced1.getVariables().getVariable().add(new ExternalVariableType());
        referenced2.getVariables().getVariable().add(new ExternalVariableType());
        //
        assertEquals(0, questionnaire.getVariables().getVariable().size());
        //
        InsertVariables insertVariables = new InsertVariables();
        insertVariables.apply(questionnaire, referenced1);
        insertVariables.apply(questionnaire, referenced2);
        //
        assertEquals(2, questionnaire.getVariables().getVariable().size());
    }

}
