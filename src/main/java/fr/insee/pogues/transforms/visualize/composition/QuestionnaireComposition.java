package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuestionnaireComposition {

    private QuestionnaireComposition() {}

    /**
     * Replace referenced questionnaire by its component. Update elements that are impacted.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     * @throws DeReferencingException if an error occurs during de-referencing.
     */
    public static void insertReference(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws DeReferencingException {
        //
        log.info("Starting de-referencing of questionnaire '{}' in questionnaire '{}'",
                referencedQuestionnaire.getId(), questionnaire.getId());

        // Update the scope of the referenced questionnaire variables
        new UpdateReferencedVariablesScope().apply(questionnaire, referencedQuestionnaire);

        // Add sequences
        new InsertSequences().apply(questionnaire, referencedQuestionnaire);

        // Add variables
        new InsertVariables().apply(questionnaire, referencedQuestionnaire);

        // Add code lists
        new InsertCodeLists().apply(questionnaire, referencedQuestionnaire);

        // Update filters in referencing questionnaire
        new UpdateFlowControlBounds().apply(questionnaire, referencedQuestionnaire);

        // Add flow controls (filters)
        new InsertFlowControls().apply(questionnaire, referencedQuestionnaire);

        // Update loops in referencing questionnaire
        new UpdateIterationBounds().apply(questionnaire, referencedQuestionnaire);

        // Add iterations (loops)
        new InsertIterations().apply(questionnaire, referencedQuestionnaire);

        // Component group is not updated since it is not used by eno generation
    }

}
