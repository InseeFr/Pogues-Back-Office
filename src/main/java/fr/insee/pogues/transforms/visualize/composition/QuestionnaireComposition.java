package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.IllegalFlowControlException;
import fr.insee.pogues.exception.IllegalIterationException;
import fr.insee.pogues.model.CodeLists;
import fr.insee.pogues.model.FlowControlType;
import fr.insee.pogues.model.IterationType;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import static fr.insee.pogues.transforms.visualize.composition.FilterComposition.updateFlowControlBounds;
import static fr.insee.pogues.transforms.visualize.composition.LoopComposition.updateIterationBounds;
import static fr.insee.pogues.transforms.visualize.composition.LoopComposition.updateReferencedVariablesScope;
import static fr.insee.pogues.transforms.visualize.composition.SequenceComposition.insertSequences;

@Slf4j
public class QuestionnaireComposition {

    private QuestionnaireComposition() {}

    /**
     * Replace referenced questionnaire by its component. Update elements that are impacted.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     * @throws IllegalFlowControlException if one of the FlowControl object involved is invalid.
     * @throws IllegalIterationException if one of the Iteration object involved is invalid.
     */
    public static void insertReference(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws IllegalFlowControlException, IllegalIterationException {
        //
        String id = questionnaire.getId();
        String reference = referencedQuestionnaire.getId();
        log.info("Starting de-referencing of questionnaire '{}' in questionnaire '{}'", reference, id);

        // Update the scope of the referenced questionnaire variables
        if (questionnaire.getIterations() != null)
            updateReferencedVariablesScope(questionnaire, referencedQuestionnaire);

        // Add sequences
        insertSequences(questionnaire, referencedQuestionnaire);
        log.info("Sequences from '{}' inserted in '{}'", reference, id);

        // Add variables
        questionnaire.getVariables().getVariable().addAll(referencedQuestionnaire.getVariables().getVariable());
        log.info("Variables from '{}' inserted in '{}'", reference, id);

        // Add code lists
        CodeLists refCodeLists = referencedQuestionnaire.getCodeLists();
        if (refCodeLists != null) {
            questionnaire.setCodeLists(new CodeLists());
            questionnaire.getCodeLists().getCodeList().addAll(refCodeLists.getCodeList());
            log.info("Code lists from '{}' inserted in '{}'", reference, id);
        } else {
            log.info("No code lists in referenced questionnaire '{}'", reference);
        }

        // Update filters in referencing questionnaire
        for (FlowControlType flowControlType : questionnaire.getFlowControl()) {
            updateFlowControlBounds(referencedQuestionnaire, flowControlType);
        }
        log.info("Flow controls' bounds updated in '{}' when de-referencing '{}'", id, reference);

        // Add flow controls (filters)
        questionnaire.getFlowControl().addAll(referencedQuestionnaire.getFlowControl());
        log.info("FlowControl from '{}' inserted in '{}'", reference, id);

        // Update loops in referencing questionnaire
        if (questionnaire.getIterations() != null) {
            for (IterationType iterationType : questionnaire.getIterations().getIteration()) {
                updateIterationBounds(referencedQuestionnaire, iterationType);
            }
            log.info("Iterations' bounds updated in '{}' when de-referencing '{}'", id, reference);
        }

        // Add iterations (loops)
        Questionnaire.Iterations refIterations = referencedQuestionnaire.getIterations();
        if (refIterations != null) {
            questionnaire.setIterations(new Questionnaire.Iterations());
            questionnaire.getIterations().getIteration().addAll(refIterations.getIteration());
            log.info("Iterations from '{}' inserted in '{}'", reference, id);
        } else {
            log.info("No iterations in referenced questionnaire '{}'", reference);
        }

        // Component group is not updated since it is not used by eno generation
    }

}
