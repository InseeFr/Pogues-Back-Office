package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of CompositionStep to insert variables of a referenced questionnaire.
 */
@Slf4j
class InsertVariables implements CompositionStep {

    /**
     * Insert variables of the referenced questionnaire in the referencing questionnaire.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     */
    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire) {
        questionnaire.getVariables().getVariable().addAll(referencedQuestionnaire.getVariables().getVariable());
        log.debug("Variables from '{}' inserted in '{}'", referencedQuestionnaire.getId(), questionnaire.getId());
    }

}
