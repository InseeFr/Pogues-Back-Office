package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of CompositionStep to insert flow controls of a referenced questionnaire.
 */
@Slf4j
class InsertFlowControls implements CompositionStep {

    /**
     * Insert flow controls of the referenced questionnaire in the referencing questionnaire.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     */
    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire) {
        questionnaire.getFlowControl().addAll(referencedQuestionnaire.getFlowControl());
        log.debug("FlowControl from '{}' inserted in '{}'", referencedQuestionnaire.getId(), questionnaire.getId());
    }

}
