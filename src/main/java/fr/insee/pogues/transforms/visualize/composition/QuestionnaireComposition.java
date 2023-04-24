package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.CodeLists;
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
        String id = questionnaire.getId();
        String reference = referencedQuestionnaire.getId();
        log.info("Starting de-referencing of questionnaire '{}' in questionnaire '{}'", reference, id);

        // Update the scope of the referenced questionnaire variables
        if (questionnaire.getIterations() != null)
            new UpdateReferencedVariablesScope().apply(questionnaire, referencedQuestionnaire);

        // Add sequences
        new InsertSequences().apply(questionnaire, referencedQuestionnaire);
        log.info("Sequences from '{}' inserted in '{}'", reference, id);

        // Add variables
        new InsertVariables().apply(questionnaire, referencedQuestionnaire);
        log.info("Variables from '{}' inserted in '{}'", reference, id);

        // Add code lists
        CodeLists refCodeLists = referencedQuestionnaire.getCodeLists();
        if (refCodeLists != null) {
            new InsertCodeLists().apply(questionnaire, referencedQuestionnaire);
            log.info("Code lists from '{}' inserted in '{}'", reference, id);
        } else {
            log.info("No code lists in referenced questionnaire '{}'", reference);
        }

        // Update filters in referencing questionnaire
        new UpdateFlowControlBounds().apply(questionnaire, referencedQuestionnaire);
        log.info("Flow controls' bounds updated in '{}' when de-referencing '{}'", id, reference);

        // Add flow controls (filters)
        new InsertFlowControls().apply(questionnaire, referencedQuestionnaire);
        log.info("FlowControl from '{}' inserted in '{}'", reference, id);

        // Update loops in referencing questionnaire
        if (questionnaire.getIterations() != null) {
            new UpdateIterationBounds().apply(questionnaire, referencedQuestionnaire);
            log.info("Iterations' bounds updated in '{}' when de-referencing '{}'", id, reference);
        }

        // Add iterations (loops)
        Questionnaire.Iterations refIterations = referencedQuestionnaire.getIterations();
        if (refIterations != null) {
            new InsertIterations().apply(questionnaire, referencedQuestionnaire);
            log.info("Iterations from '{}' inserted in '{}'", reference, id);
        } else {
            log.info("No iterations in referenced questionnaire '{}'", reference);
        }

        // Component group is not updated since it is not used by eno generation
    }

}
