package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class InsertVariables implements CompositionStep {

    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws DeReferencingException {
        questionnaire.getVariables().getVariable().addAll(referencedQuestionnaire.getVariables().getVariable());
        log.info("Variables from '{}' inserted in '{}'", referencedQuestionnaire.getId(), questionnaire.getId());
    }

}
