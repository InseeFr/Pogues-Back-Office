package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

/**
 * Class for the composition feature.
 * Contains the logic to de-reference a questionnaire that contains references.
 * De-referencing consists in replacing questionnaire references by their content.
 * Note: 'composition' and 'de-referencing' words are used interchangeably.
 */
@Slf4j
public class QuestionnaireComposition {

    private QuestionnaireComposition() {}

    /**
     * Inner class of the composition class designed to chain de-referencing steps.
     */
    static class DeReferencingPipeline {
        Questionnaire questionnaire;
        Questionnaire referencedQuestionnaire;
        private DeReferencingPipeline(Questionnaire questionnaire, Questionnaire referencedQuestionnaire) {
            this.questionnaire = questionnaire;
            this.referencedQuestionnaire = referencedQuestionnaire;
        }

        /**
         * Return an instance of de-referencing pipeline.
         * @param questionnaire Referencing questionnaire.
         * @param referencedQuestionnaire Referenced questionnaire.
         * @return A DeReferencingPipeline instance.
         */
        static DeReferencingPipeline start(Questionnaire questionnaire, Questionnaire referencedQuestionnaire) {
            log.info("Starting de-referencing of questionnaire '{}' in questionnaire '{}'",
                    referencedQuestionnaire.getId(), questionnaire.getId());
            return new DeReferencingPipeline(questionnaire, referencedQuestionnaire);
        }
        /**
         * Applies the composition step processing given. This method can be chained.
         * @param compositionStep An implementation of the CompositionStep interface.
         * @return The DeReferencingPipeline instance.
         */
        DeReferencingPipeline then(CompositionStep compositionStep) throws DeReferencingException {
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
