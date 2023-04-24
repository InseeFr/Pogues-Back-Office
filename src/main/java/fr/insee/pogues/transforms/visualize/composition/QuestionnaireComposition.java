package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QuestionnaireComposition {

    private QuestionnaireComposition() {}

    static class DeReferencingPipeline {
        Questionnaire questionnaire;
        Questionnaire referencedQuestionnaire;
        private DeReferencingPipeline(Questionnaire questionnaire, Questionnaire referencedQuestionnaire) {
            this.questionnaire = questionnaire;
            this.referencedQuestionnaire = referencedQuestionnaire;
        }
        static DeReferencingPipeline start(Questionnaire questionnaire, Questionnaire referencedQuestionnaire) {
            log.info("Starting de-referencing of questionnaire '{}' in questionnaire '{}'",
                    referencedQuestionnaire.getId(), questionnaire.getId());
            return new DeReferencingPipeline(questionnaire, referencedQuestionnaire);
        }
        DeReferencingPipeline then(CompositionStep compositionStep) {
            compositionStep.apply(questionnaire, referencedQuestionnaire);
            return this;
        }
    }

    /**
     * Replace referenced questionnaire by its component. Update elements that are impacted.
     * Note: Component group is not updated since it is not used by eno generation.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     * @throws DeReferencingException if an error occurs during de-referencing.
     */
    public static void insertReference(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws DeReferencingException {
        //
        DeReferencingPipeline.start(questionnaire, referencedQuestionnaire)
                .then(new UpdateReferencedVariablesScope())
                .then(new InsertSequences())
                .then(new InsertVariables())
                .then(new InsertCodeLists())
                .then(new UpdateFlowControlBounds())
                .then(new InsertFlowControls())
                .then(new UpdateIterationBounds())
                .then(new InsertIterations());
        //
        log.info("Finished de-referencing of questionnaire '{}' in questionnaire '{}'",
                referencedQuestionnaire.getId(), questionnaire.getId());
    }

}
