package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of CompositionStep to insert iterations of a referenced questionnaire.
 */
@Slf4j
class InsertIterations implements CompositionStep {

    /**
     * Insert iterations of the referenced questionnaire in the referencing questionnaire.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     */
    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire) {
        Questionnaire.Iterations refIterations = referencedQuestionnaire.getIterations();
        if (refIterations == null) {
            log.info("No iterations in referenced questionnaire '{}'", referencedQuestionnaire.getId());
            return;
        }
        if (questionnaire.getIterations() == null)
            questionnaire.setIterations(new Questionnaire.Iterations());
        questionnaire.getIterations().getIteration().addAll(refIterations.getIteration());
        log.info("Iterations from '{}' inserted in '{}'", referencedQuestionnaire.getId(), questionnaire.getId());

    }

}
